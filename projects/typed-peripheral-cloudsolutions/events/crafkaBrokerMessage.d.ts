import * as events from "@siredvin/cc-events";
export declare const CRAFKA_BROKER_MESSAGE = "crafka_broker_message";
export declare class CrafkaBrokerMessageEvent extends events.BaseEvent<[
    string,
    number,
    string
]> {
    getTopic(): string;
    getMessageID(): number;
    getMessage(): string;
}
