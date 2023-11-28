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
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Resolvers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<Class<?>, TickResolver<?>> RESOLVERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> TickResolver<T> getResolvedTickHandler(Class<T> objClass) {
        return (TickResolver<T>) RESOLVERS.get(objClass);
    }

    private static <T extends BlockEntity> void register(TickResolver<T> resolver) {
        RESOLVERS.put(resolver.getResolvedClass(), resolver);
    }

    static {
        LOGGER.info("Started Loading Resolvers for TimeAPI");
        var modList = ModList.get();
        var RegisterType = Type.getType(RegisterResolver.class);

        modList.getAllScanData().forEach(data -> {
            data.getAnnotations().forEach(annotationData -> {
                var clazz = annotationData.annotationType();
                if (clazz.equals(RegisterType)) {
                    String modID = (String) annotationData.annotationData().get("modID");
                    LOGGER.info("Registering Tick Resolver '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), modID));

                    if (!modList.isLoaded(modID)) {
                        LOGGER.warn("Cannot register Tick Resolver '%s' for mod '%s' due to mod not being loaded.".formatted(annotationData.clazz().getClassName(), modID));
                    } else {
                        try {
                            Class<?> resolverClazz = Class.forName(annotationData.memberName());

                            if (TickResolver.class.isAssignableFrom(resolverClazz)) {
                                register((TickResolver<?>) resolverClazz.getConstructor().newInstance());
                                LOGGER.info("Successfully registered Tick Resolver '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), modID));
                            } else {
                                LOGGER.warn("Ran into an issue while trying to register Tick Resolver '%s' for mod '%s', This Tick Resolver needs to extend 'org.mangorage.timeapi.common.core.tickresolver.TickResolver'".formatted(annotationData.clazz().getClassName(), modID));
                            }
                        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                                 InstantiationException | InvocationTargetException e) {
                            LOGGER.warn("Ran into an issue while trying to register Tick Resolver '%s' for mod '%s'".formatted(annotationData.clazz().getClassName(), modID));
                        }
                    }
                }
            });
        });

        LOGGER.info("Finished Loading Resolvers for TimeAPI");
    }

    public static void init() {}
}
