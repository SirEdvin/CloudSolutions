import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
/** @noSelf **/
export interface StatsDBridge extends ConfigurationAPI<object> {
    count(aspect: string, delta: number): void;
    delta(aspect: string, delta: number): void;
    gauge(aspect: string, value: number): void;
    set(aspect: string, eventName: string): void;
    time(aspect: string, timeInMs: number): void;
}
/** @noSelf **/
export declare class DummyStatsDBridge implements StatsDBridge {
    getConfiguration(): LuaTable;
    count(aspect: string, delta: number): void;
    delta(aspect: string, delta: number): void;
    gauge(aspect: string, value: number): void;
    set(aspect: string, eventName: string): void;
    time(aspect: string, timeInMs: number): void;
}
export declare const statsDBridgeProvider: IPeripheralProvider<StatsDBridge>;
