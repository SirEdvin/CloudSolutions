import { BasicTest, asserts, TestSuite } from "@siredvin/soteria";
import { calculateLength } from "@siredvin/cc-utils";
import { CrafkaBroker, findPeripheral } from "./contracts";
import { runSuite } from "./run";

type BrokerEvent = [string, number, string];

function pullMessage(expectedTopic: string): BrokerEvent {
    while (true) {
        const [, topic, messageID, message] = os.pullEvent("crafka_broker_message");
        if (topic == expectedTopic) return [topic as string, messageID as number, message as string];
    }
}

function assertMessage(event: BrokerEvent, topic: string, id: number, message: string): void {
    asserts.assertEqual(event[0], topic, "Message event has the wrong topic");
    asserts.assertEqual(event[1], id, "Message event has the wrong ID");
    asserts.assertEqual(event[2], message, "Message event has the wrong body");
}

function assertSuccess(result: boolean | null, error: string | null, action: string): void {
    asserts.assert(result == true, `${action} failed: ${error ?? "unknown error"}`);
}

abstract class BrokerTest extends BasicTest {
    readonly topic: string;

    constructor(name: string, readonly broker: CrafkaBroker, timeout = 1_000_000) {
        super(name, timeout);
        this.topic = name;
    }

    bootstrap(): void {
        os.queueEvent("crafka_test_flush");
        os.pullEvent("crafka_test_flush");
        this.cleanupTopics();
        asserts.assertEqual(this.broker.listTopics().length, 0, "Broker should have no topics before the test");
    }

    cleanupTopics(): void {
        for (const topic of this.broker.listTopics()) {
            this.broker.unsubscribe(topic);
            this.broker.deleteTopic(topic);
        }
    }

    teardown(): void {
        this.cleanupTopics();
    }

    createTopic(): void {
        const [result, error] = this.broker.createTopic(this.topic);
        assertSuccess(result, error, "Creating topic");
    }

    publish(message: string): void {
        const [result, error] = this.broker.publish(this.topic, message);
        assertSuccess(result, error, "Publishing message");
    }

    deleteTopic(): void {
        const [result, error] = this.broker.deleteTopic(this.topic);
        assertSuccess(result, error, "Deleting topic");
        asserts.assertEqual(this.broker.listTopics().length, 0, "Topic remains after deletion");
    }
}

class BaseBrokerTest extends BrokerTest {
    constructor(broker: CrafkaBroker) { super("base_broker_test", broker); }

    execute(): void {
        this.createTopic();
        parallel.waitForAll(
            () => {
                for (let i = 1; i <= 3; i++) this.publish(`c${i}`);
            },
            () => {
                this.broker.subscribe(this.topic, {});
                for (let i = 1; i <= 3; i++) assertMessage(pullMessage(this.topic), this.topic, i, `c${i}`);
                this.broker.unsubscribe(this.topic);
            }
        );
        const info = this.broker.describeTopic(this.topic);
        asserts.assertEqual(info.messageLimit, 512, "Topic has the wrong message limit");
        asserts.assertEqual(info.firstMessage, 1, "Topic has the wrong first message ID");
        asserts.assertEqual(info.lastMessage, 3, "Topic has the wrong last message ID");
        asserts.assertEqual(calculateLength(this.broker.fetchMessages(this.topic, 0)), 3, "Topic has the wrong number of messages");
        this.deleteTopic();
    }
}

class SubscriptionBrokerTest extends BrokerTest {
    constructor(broker: CrafkaBroker) { super("subscription_broker_test", broker); }

    execute(): void {
        this.createTopic();
        for (let i = 1; i <= 3; i++) this.publish(`c${i}`);
        this.broker.subscribe(this.topic, {});
        for (let i = 1; i <= 3; i++) assertMessage(pullMessage(this.topic), this.topic, i, `c${i}`);
        this.broker.unsubscribe(this.topic);
        this.broker.subscribe(this.topic, { cursor: 1 });
        for (let i = 2; i <= 3; i++) assertMessage(pullMessage(this.topic), this.topic, i, `c${i}`);

        let event: BrokerEvent | null = null;
        parallel.waitForAny(
            () => os.sleep(2),
            () => { event = pullMessage(this.topic); }
        );
        asserts.assertNull(event, "Subscription emitted a message after reaching the cursor");
        this.deleteTopic();
    }
}

class ManualCursorBrokerTest extends BrokerTest {
    constructor(broker: CrafkaBroker) { super("manual_cursor_broker_test", broker); }

    execute(): void {
        this.createTopic();
        this.publish("c1");
        this.broker.subscribe(this.topic, { autoCursor: false });
        for (let i = 1; i <= 5; i++) assertMessage(pullMessage(this.topic), this.topic, 1, "c1");
        this.broker.setCursor(this.topic, 1);

        let event: BrokerEvent | null = null;
        parallel.waitForAny(
            () => os.sleep(2),
            () => { event = pullMessage(this.topic); }
        );
        asserts.assertNull(event, "Acknowledged message was resent");
        this.deleteTopic();
    }
}

class ManualCursorOrderBrokerTest extends BrokerTest {
    constructor(broker: CrafkaBroker) { super("manual_cursor_order_broker_test", broker); }

    execute(): void {
        this.createTopic();
        this.publish("c1");
        this.publish("c2");
        this.broker.subscribe(this.topic, { autoCursor: false });
        for (let i = 1; i <= 5; i++) assertMessage(pullMessage(this.topic), this.topic, 1, "c1");
        this.broker.setCursor(this.topic, 1);
        assertMessage(pullMessage(this.topic), this.topic, 2, "c2");
        this.broker.setCursor(this.topic, 2);

        let event: BrokerEvent | null = null;
        parallel.waitForAny(
            () => os.sleep(2),
            () => { event = pullMessage(this.topic); }
        );
        asserts.assertNull(event, "Acknowledged second message was resent");
        this.deleteTopic();
    }
}

class BrokenManualCursorBrokerTest extends BrokerTest {
    constructor(broker: CrafkaBroker) { super("broken_manual_cursor_broker_test", broker); }

    execute(): void {
        this.createTopic();
        this.publish("c1");
        const configuration = this.broker.getConfiguration();
        const attempts = configuration.get("maxResendSteps") as number;
        const resendDelay = configuration.get("resendDelay") as number;
        this.broker.subscribe(this.topic, { autoCursor: false });
        for (let i = 1; i <= 5; i++) assertMessage(pullMessage(this.topic), this.topic, 1, "c1");
        const retryWindow = (resendDelay * attempts * (attempts + 1)) / 2000 + 2;
        const deadline = os.epoch("utc") / 1000 + retryWindow;
        while (os.epoch("utc") / 1000 <= deadline) os.sleep(0.1);
        asserts.assertNull(this.broker.getSubscription(this.topic), "Broken subscription was not removed");
        this.deleteTopic();
    }
}

const broker = findPeripheral<CrafkaBroker>("crafka_broker");
const suite = new TestSuite("crafka_broker");
suite.addTest(new BaseBrokerTest(broker));
suite.addTest(new SubscriptionBrokerTest(broker));
suite.addTest(new ManualCursorBrokerTest(broker));
suite.addTest(new ManualCursorOrderBrokerTest(broker));
suite.addTest(new BrokenManualCursorBrokerTest(broker));
runSuite(suite);
