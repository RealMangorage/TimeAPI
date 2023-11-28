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

package org.mangorage.timeapi.common.core.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mangorage.timeapi.common.core.Constants;
import org.mangorage.timeapi.common.core.annotations.TimeInternal;
import org.mangorage.timeapi.common.entities.AcceleratorEntity;
import org.mangorage.timeapi.common.items.TickerItem;

@TimeInternal
public class ModRegistries {
    @TimeInternal
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
    @TimeInternal
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MODID);
    @TimeInternal
    private static final RegistryObject<TickerItem> TICKER_ITEM = ITEMS.register("ticker_debug", () -> new TickerItem(false));
    @TimeInternal
    private static final RegistryObject<TickerItem> TICKER_ITEM_2 = ITEMS.register("ticker_debug_2", () -> new TickerItem(true));
    @TimeInternal
    public static final RegistryObject<EntityType<AcceleratorEntity>> ACCELERATOR_ENTITY = ENTITIES.register("accelerator", () -> EntityType.Builder.<AcceleratorEntity>of(AcceleratorEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .build(new ResourceLocation(Constants.MODID, "accelerator").toString()));
    @TimeInternal
    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ENTITIES.register(eventBus);
    }
}
