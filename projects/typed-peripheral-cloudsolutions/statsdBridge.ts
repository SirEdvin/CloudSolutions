import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";


/** @noSelf **/
export interface StatsDBridge extends ConfigurationAPI<object> {
    count(aspect: String, delta: number);
    delta(aspect: String, delta: number);
    gauge(aspect: String, value: number);
    set(aspect: String, eventName: String);
    time(aspect: String, timeInMs: number);
}

/** @noSelf **/
export class DummyStatsDBridge implements StatsDBridge {
    getConfiguration(): LuaTable {
        return new LuaTable();
    }
    count(aspect: String, delta: number) {}
    delta(aspect: String, delta: number) {}
    gauge(aspect: String, value: number) {}
    set(aspect: String, eventName: String) {}
    time(aspect: String, timeInMs: number) {}
}

export const statsDBridgeProvider = new IPeripheralProvider<StatsDBridge>(
    "statsd_bridge",
    () => new DummyStatsDBridge()
);
