package org.mangorage.timeapi.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class TimeAPIConfig {
    private static ForgeConfigSpec.ConfigValue<List<String>> MODS_DISABLED;
    private static ForgeConfigSpec.ConfigValue<List<String>> RESOLVERS_DISABLED;

    public static void registerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("List any mods here that you dont want resolvers for. E.G if you don't want to resolve for AE2 put 'ae2'");
        MODS_DISABLED = builder.define("mods_disabled", new ArrayList<String>());

        builder.comment("List any resolvers here that you don't want to load. E.G if you don't want to load the ae2CableBus resolver put 'ae2CableBus'");
        RESOLVERS_DISABLED = builder.define("resolvers_disabled", new ArrayList<String>());
    }

    public static boolean isModDisabled(String modID) {
        return MODS_DISABLED.get().contains(modID);
    }

    public static boolean isResolverDisabled(String resolverID) {
        return RESOLVERS_DISABLED.get().contains(resolverID);
    }
}
