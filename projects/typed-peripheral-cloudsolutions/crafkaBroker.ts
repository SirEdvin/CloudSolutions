import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";

/** @noSelf **/
export interface CrafkaBroker extends ConfigurationAPI<object> {
    createTopic(name: string, messageLimit?: number): Result;
    listTopics(): string[];
    deleteTopic(name: string): Result;
    publish(topic: string, message: string): Result;
    fetchMessages(topic: string, cursor: number): [number, string][];
    describeTopic(topic: string): {
        messageLimit: number;
        firstMessage: number;
        lastMessage: number;
    };
    subscribe(
        topic: string,
        options: {
            fragile?: boolean;
            autoCursor?: boolean;
            consumerGroup?: string;
            cursor?: number;
        }
    );
    unsubscribe(topic: string);
    getSubscription(topic: string): {
        cursor: number;
        fragile: boolean;
        autoCursor: boolean;
        consumerGroup?: string;
    };
    setCursor(topic: string, cursor: number);
}

export const crafkaBrokerPeripheralProvider =
    new IPeripheralProvider<CrafkaBroker>("crafka_broker", () => null);
