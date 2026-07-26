import * as events from "@siredvin/cc-events";

export const CRAFKA_BROKER_MESSAGE = "crafka_broker_message";

export class CrafkaBrokerMessageEvent extends events.BaseEvent<
    [string, number, string]
> {
    getTopic(): string {
        return this.args[0];
    }

    getMessageID(): number {
        return this.args[1];
    }

    getMessage(): string {
        return this.args[2];
    }
}

events.eventInitializers.set(
    CRAFKA_BROKER_MESSAGE,
    (name: string, args: [string, number, string]) =>
        new CrafkaBrokerMessageEvent(name, args)
);
