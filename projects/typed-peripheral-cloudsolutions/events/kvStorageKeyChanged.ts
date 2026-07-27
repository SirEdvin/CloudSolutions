import * as events from "@siredvin/cc-events";

export const KV_STORAGE_KEY_CHANGED = "kv_storage_key_changed";

export class KVStorageKeyChangedEvent extends events.BaseEvent<
    [string, string]
> {
    getKey(): string {
        return this.args[0];
    }

    getValue(): string {
        return this.args[1];
    }
}

events.eventInitializers.set(
    KV_STORAGE_KEY_CHANGED,
    (name: string, args: [string, string]) =>
        new KVStorageKeyChangedEvent(name, args)
);
