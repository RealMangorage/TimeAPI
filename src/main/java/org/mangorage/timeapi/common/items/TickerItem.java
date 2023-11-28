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

package org.mangorage.timeapi.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.mangorage.timeapi.api.TimeAPI;
import org.mangorage.timeapi.common.core.annotations.TimeInternal;
import org.mangorage.timeapi.common.entities.AcceleratorEntity;

import java.util.List;

/**
 * Ticks a Block when clicking the block... by 20 ticks.
 */

@TimeInternal
public class TickerItem extends Item {
    private static final String NBT_KEY = "tick_rate";

    private static CompoundTag getOrDefaultNBT(ItemStack stack) {
        var nbt = stack.getOrCreateTag();
        if (!nbt.contains(NBT_KEY))
            nbt.putInt(NBT_KEY, 20);
        return nbt;
    }


    private final boolean useEntity;
    public TickerItem(boolean useEntity) {
        super(new Properties().stacksTo(1));
        this.useEntity = useEntity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var item = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide()) {
            var nbt = getOrDefaultNBT(item);
            var ticks = nbt.getInt(NBT_KEY);
            if (pPlayer.isShiftKeyDown()) {
                ticks += 20;
            } else {
                ticks = Math.max(ticks - 20, 1);
            }
            nbt.putInt(NBT_KEY, ticks);
            item.setTag(nbt);
            return InteractionResultHolder.pass(item);
        }
        return InteractionResultHolder.sidedSuccess(item, false);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity entity, InteractionHand pUsedHand) {
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            var item = pPlayer.getItemInHand(pUsedHand);
            serverPlayer.displayClientMessage(Component.literal("Tick Accelerating Entity: %s".formatted(entity)), true);
            for (int i = 0; i < getOrDefaultNBT(item).getInt(NBT_KEY); i++)
                TimeAPI.tickEntity(
                        entity
                );

            return InteractionResult.PASS;
        }
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel serverLevel) {
            var serverPlayer = (ServerPlayer) pContext.getPlayer();
            var item = pContext.getItemInHand();
            var blockPos = pContext.getClickedPos();
            assert serverPlayer != null;
            serverPlayer.displayClientMessage(Component.literal("Tick Accelerating Block: %sx %sy %sz".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ())), true);

            if (useEntity) {
                AcceleratorEntity entity = new AcceleratorEntity(serverLevel, blockPos);
                entity.setRemainingTime(getOrDefaultNBT(item).getInt(NBT_KEY));
                entity.setTickRate(256);
                serverLevel.addFreshEntity(entity);
            } else {
                for (int i = 0; i < getOrDefaultNBT(item).getInt(NBT_KEY); i++)
                    TimeAPI.tickBlock(
                            serverLevel,
                            blockPos
                    );
            }

            return InteractionResult.PASS;
        }
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        var nbt = getOrDefaultNBT(pStack);
        pTooltipComponents.add(Component.literal("Ticks: %s".formatted(nbt.getInt(NBT_KEY))));
    }
}
