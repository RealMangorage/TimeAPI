/*
 * Copyright (c) 2023. MangoRage
 * MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.timeapi.common.core.tickresolver;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import org.mangorage.timeapi.api.RegisterResolver;
import org.mangorage.timeapi.common.core.config.TimeAPIConfig;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class Resolvers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<Class<?>, TickResolver<?>> RESOLVERS = new HashMap<>();
    private static final HashMap<String, TickResolver<?>> RESOLVERS_BY_ID = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> TickResolver<T> getResolvedTickHandler(Class<T> objClass) {
        return (TickResolver<T>) RESOLVERS.get(objClass);
    }

    private static <T extends BlockEntity> void register(TickResolver<T> resolver) {
        RESOLVERS.put(resolver.getResolvedClass(), resolver);
        RESOLVERS_BY_ID.put(resolver.getID(), resolver);
    }

    private static boolean isResolversForModEnabled(String modID) {
        if (!ModList.get().isLoaded(modID)) return false;
        return !TimeAPIConfig.isModDisabled(modID);
    }

    private static boolean isResolverEnabled(String id) {
        return !TimeAPIConfig.isResolverDisabled(id);
    }

    static {
        LOGGER.info("Started Loading Resolvers for TimeAPI");
        var modList = ModList.get();
        var RegisterType = Type.getType(RegisterResolver.class);
        var toRegister = new ArrayList<Runnable>();

        modList.getAllScanData().forEach(data -> {
            data.getAnnotations().forEach(annotationData -> {
                var clazz = annotationData.annotationType();
                if (clazz.equals(RegisterType)) {
                    String modID = (String) annotationData.annotationData().get("modID");
                    String resolverID = (String) annotationData.annotationData().get("resolverID");
                    LOGGER.info("Found Tick Resolver '%s' with ID '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), resolverID, modID));

                    if (!isResolversForModEnabled(modID)) {
                        LOGGER.warn("Cannot register Tick Resolver '%s' with ID '%s' for mod '%s' due to mod not being loaded.".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                    } else if (!isResolverEnabled(resolverID)) {
                        LOGGER.warn("Cannot register Tick Resolver '%s' with ID '%s' for mod '%s' due to resolver being disabled in configs.".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                    } else {
                        toRegister.add(() -> {
                            LOGGER.info("Registering Tick Resolver '%s' with ID '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                            try {
                                Class<?> resolverClazz = Class.forName(annotationData.memberName());

                                if (TickResolver.class.isAssignableFrom(resolverClazz)) {
                                    // Crashes if something does not exist within class.
                                    // Figure out how to deal with this issue. TODO
                                   register((TickResolver<?>) resolverClazz.getConstructor().newInstance());
                                } else {
                                    LOGGER.warn("Ran into an issue while trying to register Tick Resolver '%s' with ID '%s' for mod '%s', This Tick Resolver needs to extend 'org.mangorage.timeapi.common.core.tickresolver.TickResolver'".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                                }
                            } catch (Exception e) {
                                LOGGER.warn("Ran into an issue while trying to register Tick Resolver '%s' with ID '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                            } finally {
                                LOGGER.info("Successfully registered Tick Resolver '%s' with ID '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), resolverID, modID));
                            }
                        });
                    }
                }
            });
        });

        toRegister.forEach(e -> {
            try {
                e.run();
            } catch (Exception ae) {

            }
        }); // Initialize them and do the proper registration. This allows for us to list out all possible resolvers

        LOGGER.info("Finished Loading Resolvers for TimeAPI");
    }

    public static void init() {}
}
