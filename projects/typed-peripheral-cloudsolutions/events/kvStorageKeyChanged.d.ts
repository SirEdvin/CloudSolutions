import * as events from "@siredvin/cc-events";
export declare const KV_STORAGE_KEY_CHANGED = "kv_storage_key_changed";
export declare class KVStorageKeyChangedEvent extends events.BaseEvent<[
    string,
    string
]> {
    getKey(): string;
    getValue(): string;
}
