import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";

/** @noSelf **/
export interface KVStorage extends ConfigurationAPI<object> {
    delete(key: string): void;
    put(key: string, value: string, expire?: number | null): void;
    get(key: string): string | null;
    mput(values: { [key: string]: string });
    mget(values: string[]): LuaTable<string, string>;
    list(glob?: string): string[];
    get_ex(key: string): number | null;
    put_ex(key: string, expire?: number | null): void;
    incr(key: string, value?: number): number;
    decr(key: string, value?: number): number;
    subscribe(type: "changed" | "deleted", pattern: string);
    unsubscribe(type: "changed" | "deleted", pattern: string);
    getSubscriptions(type: "changed" | "deleted"): string[];
}

export const kvStoragePeripheralProvider = new IPeripheralProvider<KVStorage>(
    "kv_storage",
    () => null
);
