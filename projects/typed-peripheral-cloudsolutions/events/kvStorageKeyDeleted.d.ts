import * as events from "@siredvin/cc-events";
export declare const KV_STORAGE_KEY_DELETED = "kv_storage_key_deleted";
export declare class KVStorageKeyDeletedEvent extends events.BaseEvent<[string]> {
    getKey(): string;
}
