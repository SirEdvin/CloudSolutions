import { calculateLength } from "@siredvin/cc-utils";
import { BasicTest, asserts, TestSuite } from "@siredvin/soteria";
import { findPeripheral, KVStorage } from "./contracts";
import { runSuite } from "./run";

abstract class KVTest extends BasicTest {
    constructor(name: string, readonly storage: KVStorage, timeout = 1_000_000) {
        super(name, timeout);
    }

    bootstrap(): void {
        asserts.assertEqual(this.storage.list().length, 0, "KVStorage should be empty before the test");
    }

    teardown(): void {
        for (const key of this.storage.list()) this.storage.delete(key);
    }
}

function waitUntilExpired(expire: number): void {
    while (os.epoch("utc") / 1000 <= expire) os.sleep(0.1);
}

class BaseKVTest extends KVTest {
    constructor(storage: KVStorage) { super("base_test", storage); }

    execute(): void {
        this.storage.put("t1", "5");
        asserts.assertEqual(this.storage.get("t1"), "5", "First key has the wrong value");
        this.storage.delete("t1");
        asserts.assertNull(this.storage.get("t1"), "Deleted key is still present");

        this.storage.mput({ t1: "t1", t2: "t2", t3: "t3" });
        this.storage.mput({ t4: "t1", t5: "t2" });
        const keys = ["t1", "t2", "t3", "t4", "t5"];
        asserts.assertEqual(calculateLength(this.storage.mget(keys)), 5, "Some keys were not stored");
        for (const key of keys) this.storage.delete(key);
        asserts.assertEqual(calculateLength(this.storage.mget(keys)), 0, "Deleted keys are still present");
    }
}

class ExpirationKVTest extends KVTest {
    constructor(storage: KVStorage) { super("expiration_test", storage); }

    execute(): void {
        this.storage.put("t1", "5", os.epoch("utc") / 1000 - 20);
        asserts.assertNull(this.storage.get("t1"), "Already expired key is present");
        const firstExpire = os.epoch("utc") / 1000 + 2;
        this.storage.put("t2", "5", firstExpire);
        asserts.assertEqual(this.storage.get("t2"), "5", "Unexpired key is missing");
        waitUntilExpired(firstExpire);
        asserts.assertNull(this.storage.get("t2"), "Expired key is present");

        const secondExpire = os.epoch("utc") / 1000 + 2;
        this.storage.put("t1", "5", secondExpire);
        this.storage.put("t2", "5", secondExpire);
        this.storage.put("t3", "5", secondExpire);
        const keys = ["t1", "t2", "t3"];
        asserts.assertEqual(calculateLength(this.storage.mget(keys)), 3, "Some expiring keys are missing");
        waitUntilExpired(secondExpire);
        asserts.assertEqual(calculateLength(this.storage.mget(keys)), 0, "Some expired keys are present");
    }
}

class ChangedSubscriptionTest extends KVTest {
    constructor(storage: KVStorage) { super("changed_subscription_test", storage); }

    bootstrap(): void {
        super.bootstrap();
        asserts.assertEqual(this.storage.getSubscriptions("changed").length, 0, "Changed subscriptions should be empty");
    }

    execute(): void {
        this.storage.subscribe("changed", "t.*");
        parallel.waitForAll(
            () => {
                const [, key, value] = os.pullEvent("kv_storage_key_changed");
                asserts.assertEqual(key, "t1", "Changed event has the wrong key");
                asserts.assertEqual(value, "5", "Changed event has the wrong value");
            },
            () => this.storage.put("t1", "5")
        );
        parallel.waitForAll(
            () => {
                const [, firstKey, firstValue] = os.pullEvent("kv_storage_key_changed");
                const [, secondKey, secondValue] = os.pullEvent("kv_storage_key_changed");
                const values: Record<string, string> = { t3: "5", t4: "6" };
                asserts.assertEqual(firstValue, values[firstKey as string], "First mput event has the wrong value");
                asserts.assertEqual(secondValue, values[secondKey as string], "Second mput event has the wrong value");
                asserts.assert(firstKey != secondKey, "mput emitted the same changed key twice");
            },
            () => this.storage.mput({ t3: "5", t4: "6" })
        );
    }

    teardown(): void {
        super.teardown();
        for (const pattern of this.storage.getSubscriptions("changed")) this.storage.unsubscribe("changed", pattern);
    }
}

class DeletedSubscriptionTest extends KVTest {
    constructor(storage: KVStorage) { super("deleted_subscription_test", storage); }

    bootstrap(): void {
        super.bootstrap();
        asserts.assertEqual(this.storage.getSubscriptions("deleted").length, 0, "Deleted subscriptions should be empty");
    }

    execute(): void {
        this.storage.subscribe("deleted", "t.*");
        parallel.waitForAll(
            () => {
                const [, key] = os.pullEvent("kv_storage_key_deleted");
                asserts.assertEqual(key, "t1", "Delete event has the wrong key");
            },
            () => {
                this.storage.put("t1", "5");
                this.storage.delete("t1");
            }
        );
        parallel.waitForAll(
            () => {
                const [, key] = os.pullEvent("kv_storage_key_deleted");
                asserts.assertEqual(key, "t2", "Expiration event has the wrong key");
            },
            () => this.storage.put("t2", "5", os.epoch("utc") / 1000 - 20)
        );
    }

    teardown(): void {
        super.teardown();
        for (const pattern of this.storage.getSubscriptions("deleted")) this.storage.unsubscribe("deleted", pattern);
    }
}

class IncrementKVTest extends KVTest {
    constructor(storage: KVStorage) { super("increment_test", storage); }

    execute(): void {
        asserts.assertEqual(this.storage.incr("t1"), 1, "Initial increment is incorrect");
        asserts.assertEqual(this.storage.incr("t1", 5), 6, "First increment is incorrect");
        asserts.assertEqual(this.storage.incr("t1", 5), 11, "Second increment is incorrect");
        asserts.assertEqual(this.storage.decr("t1", 5), 6, "First decrement is incorrect");
        asserts.assertEqual(this.storage.decr("t1", 5), 1, "Second decrement is incorrect");
        asserts.assertEqual(this.storage.decr("t1"), 0, "Third decrement is incorrect");
        asserts.assertEqual(this.storage.incr("t2", 5), 5, "Increment-created key is incorrect");
        asserts.assertEqual(this.storage.decr("t3"), -1, "Default decrement-created key is incorrect");
        asserts.assertEqual(this.storage.decr("t4", 5), -5, "Decrement-created key is incorrect");
    }
}

const storage = findPeripheral<KVStorage>("kv_storage");
const suite = new TestSuite("kvstorage");
suite.addTest(new BaseKVTest(storage));
suite.addTest(new ExpirationKVTest(storage));
suite.addTest(new ChangedSubscriptionTest(storage));
suite.addTest(new DeletedSubscriptionTest(storage));
suite.addTest(new IncrementKVTest(storage));
runSuite(suite);
