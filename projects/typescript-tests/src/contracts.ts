/** @noSelf **/
export interface KVStorage {
    delete(key: string): void;
    put(key: string, value: string, expire?: number | null): void;
    get(key: string): string | null;
    mput(values: Record<string, string>): void;
    mget(values: string[]): LuaTable<string, string>;
    list(glob?: string): string[];
    incr(key: string, value?: number): number;
    decr(key: string, value?: number): number;
    subscribe(type: "changed" | "deleted", pattern: string): void;
    unsubscribe(type: "changed" | "deleted", pattern: string): void;
    getSubscriptions(type: "changed" | "deleted"): string[];
}

/** @noSelf **/
export interface CrafkaBroker {
    createTopic(name: string, messageLimit?: number): LuaMultiReturn<[boolean | null, string | null]>;
    listTopics(): string[];
    deleteTopic(name: string): LuaMultiReturn<[boolean | null, string | null]>;
    publish(topic: string, message: string): LuaMultiReturn<[boolean | null, string | null]>;
    fetchMessages(topic: string, cursor: number): LuaTable<number, string>;
    describeTopic(topic: string): { messageLimit: number; firstMessage: number; lastMessage: number };
    subscribe(topic: string, options: { fragile?: boolean; autoCursor?: boolean; cursor?: number }): void;
    unsubscribe(topic: string): void;
    getSubscription(topic: string): { cursor: number; fragile: boolean; autoCursor: boolean } | null;
    setCursor(topic: string, cursor: number): void;
    getConfiguration(): LuaTable<string, number>;
}

export function findPeripheral<T>(type: string): T {
    const [foundByType] = peripheral.find(type) as LuaMultiReturn<[T | null]>;
    const found = foundByType ?? peripheral.wrap("back") as T | null;
    if (found == null) throw `Unable to find ${type} peripheral`;
    return found;
}
