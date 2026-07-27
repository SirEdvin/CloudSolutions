import * as events from "@siredvin/cc-events";

export const KV_STORAGE_KEY_DELETED = "kv_storage_key_deleted";

export class KVStorageKeyDeletedEvent extends events.BaseEvent<[string]> {
    getKey(): string {
        return this.args[0];
    }
}

events.eventInitializers.set(
    KV_STORAGE_KEY_DELETED,
    (name: string, args: [string]) => new KVStorageKeyDeletedEvent(name, args)
);
