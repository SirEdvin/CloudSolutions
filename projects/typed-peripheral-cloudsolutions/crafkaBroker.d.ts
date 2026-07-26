import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
type CrafkaResult = LuaMultiReturn<[
    boolean | null,
    number | string | null
]>;
type CrafkaSubscription = {
    cursor: number;
    fragile: boolean;
    autoCursor: boolean;
};
/** @noSelf **/
export interface CrafkaBroker extends ConfigurationAPI<object> {
    createTopic(name: string, messageLimit?: number): CrafkaResult;
    listTopics(): string[];
    deleteTopic(name: string): Result;
    publish(topic: string, message: string): Result;
    fetchMessages(topic: string, cursor: number): LuaTable<number, string>;
    describeTopic(topic: string): {
        messageLimit: number;
        firstMessage: number;
        lastMessage: number;
    };
    subscribe(topic: string, options?: {
        fragile?: boolean;
        autoCursor?: boolean;
        cursor?: number;
    }): CrafkaResult;
    unsubscribe(topic: string): Result;
    getSubscription(topic: string): LuaMultiReturn<[
        CrafkaSubscription | null,
        string | null
    ]>;
    setCursor(topic: string, cursor: number): Result;
}
export declare const crafkaBrokerPeripheralProvider: IPeripheralProvider<CrafkaBroker>;
export {};
