import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
/** @noSelf **/
export interface StatsDBridge extends ConfigurationAPI<object> {
    count(aspect: String, delta: number): any;
    delta(aspect: String, delta: number): any;
    gauge(aspect: String, value: number): any;
    set(aspect: String, eventName: String): any;
    time(aspect: String, timeInMs: number): any;
}
/** @noSelf **/
export declare class DummyStatsDBridge implements StatsDBridge {
    getConfiguration(): LuaTable;
    count(aspect: String, delta: number): void;
    delta(aspect: String, delta: number): void;
    gauge(aspect: String, value: number): void;
    set(aspect: String, eventName: String): void;
    time(aspect: String, timeInMs: number): void;
}
export declare const statsDBridgeProvider: IPeripheralProvider<StatsDBridge>;
