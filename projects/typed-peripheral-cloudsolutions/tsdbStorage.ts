import { ConfigurationAPI } from "@siredvin/typed-peripheral-api/configuration";
import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";

/** @noSelf **/
export interface TSDBStorage extends ConfigurationAPI<object> {
    registerTimeseries(
        name: string,
        tags: LuaTable<string, string>,
        retention?: number
    ): string;
    queryTimeseries(
        namePattern: string,
        tagsQuery: LuaTable<string, string>,
        fromTimestamp: number,
        toTimestamp: number
    ): LuaTable<string, LuaTable<number, number>>;
    getTimeseries(): LuaTable<string, unknown>[];
    postMeasurement(id: string, value: number): void;
    postMeasurement(values: LuaTable<string, number>): void;
}

export const tsdbStoragePeripheralProvider =
    new IPeripheralProvider<TSDBStorage>("tsdb_storage", () => null);
