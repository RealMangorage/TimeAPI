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

package org.mangorage.timeapi.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.mangorage.timeapi.common.core.annotations.OfficialTimeAPI;
import org.mangorage.timeapi.common.core.tickresolver.Resolvers;
import org.mangorage.timeapi.common.core.tickresolver.TickResolver;
import org.slf4j.Logger;

@OfficialTimeAPI
public class TimeAPI {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Handles ticking a block, once.
     * May try to tick it using a different method.
     * @param level -> needs to be a {@link ServerLevel}
     * @param pos -> needs to be a {@link BlockPos}
     */

    @OfficialTimeAPI
    @SuppressWarnings("unchecked")
    public static void tickBlock(ServerLevel level, BlockPos pos) {
        simulateBlockTick(level, pos).tick();
    }

    @OfficialTimeAPI
    public static SimulatedTick simulateBlockTick(ServerLevel level, BlockPos pos) {
        SimulatedTick simulatedTick = new SimulatedTick(level, pos);
        var blockState = level.getBlockState(pos);
        if (blockState == Blocks.VOID_AIR.defaultBlockState()) return simulatedTick;
        var block = blockState.getBlock();

        var blockEntity = level.getBlockEntity(pos);

        @SuppressWarnings("unchecked")
        BlockEntityTicker<BlockEntity> ticker = blockEntity != null ? (BlockEntityTicker<BlockEntity>) blockState.getTicker(level, blockEntity.getType()) : null;

        var resolvedTicker = blockEntity != null ? Resolvers.getResolvedTickHandler(blockEntity.getClass()) : null;

        if (resolvedTicker != null) {
            try {
                simulatedTick.setRunnable(() -> resolvedTicker.tickUnsafe(blockEntity));
            } catch (ClassCastException classCastException) {
                LOGGER.warn(classCastException.getMessage());
            }
        } else if (ticker != null) {
            simulatedTick.setRunnable(() -> ticker.tick(level, pos, blockState, blockEntity));
        } else if (block.isRandomlyTicking(blockState)) {
            simulatedTick.setRunnable(() -> blockState.randomTick(level, pos, level.getRandom()));
        }

        return simulatedTick;
    }

    /**
     * Ticks an Entity, Once. We don't have much here.
     *
     * Might go unused for a while, before becoming useful?
     * @param entity
     */
    @OfficialTimeAPI
    public static void tickEntity(Entity entity) {
        entity.tick();
    }


    /**
     * Returns the amount of ticks needed to accelerate a block at x rate for y seconds
     * @param rate
     * @param seconds
     * @return
     */
    @OfficialTimeAPI
    public static int getTicksAtRate(int rate, int seconds) {
        return (rate / 2 ) * (seconds * 20);
    }

    /**
     * Returns the amount of ticks needed to accelerate for x seconds at y rate
     *
     * We pass through the ticks Remaining when you are adding/increasing a given accelerator.
     * Such as the Time in a bottle making a block go from 2x -> 4x or 8x -> 16x. It will return the difference.
     * To ensure that it keeps running for at most 30 seconds.
     * @param rate
     * @param seconds
     * @param ticksRemaining
     * @return
     */
    @OfficialTimeAPI
    public static int getTicksAtRate(int rate, int seconds, int ticksRemaining) {
        return ((rate / 2) * (seconds * 20)) - ticksRemaining;
    }
}
