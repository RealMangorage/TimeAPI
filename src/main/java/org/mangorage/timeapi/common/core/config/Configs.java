package org.mangorage.timeapi.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Configs {
    public final static ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public final static ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public final static ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static void register() {
        registerServerConfigs();
        //registerCommonConfigs();
        //registerClientConfigs();
    }

    private static void registerServerConfigs() {
        TimeAPIConfig.registerConfig(SERVER_BUILDER);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    private static void registerCommonConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

    private static void registerClientConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}
