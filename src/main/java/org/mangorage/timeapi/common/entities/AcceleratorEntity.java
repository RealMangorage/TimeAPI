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

package org.mangorage.timeapi.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.mangorage.timeapi.api.SimulatedTick;
import org.mangorage.timeapi.api.TimeAPI;
import org.mangorage.timeapi.common.core.registries.ModRegistries;

public class AcceleratorEntity extends Entity {
    public static final EntityDataAccessor<Integer> TIME_RATE = SynchedEntityData.defineId(AcceleratorEntity.class, EntityDataSerializers.INT);
    public static final String TIME_RATE_NBT = "time_rate";
    public static final String TIME_REMAINING_NBT = "remaining_time";
    public static final String BLOCK_POS_NBT = "blockpos";

    private int remainingTime;
    private BlockPos pos;
    private SimulatedTick simulatedTick = SimulatedTick.EMPTY;

    public AcceleratorEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        entityData.set(TIME_RATE, 1);
    }

    public AcceleratorEntity(Level worldIn, BlockPos pos) {
        this(ModRegistries.ACCELERATOR_ENTITY.get(), worldIn);
        this.pos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5); // Handles making it centered...
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getTimeRate() {
        return entityData.get(TIME_RATE);
    }

    public void setRemainingTime(int ticks) {
        this.remainingTime = ticks;
    }

    public void setTickRate(int rate) {
        entityData.set(TIME_RATE, rate);
    }

    @Override
    public void tick() {
        super.baseTick();
        if (level().isClientSide) return;

        ServerLevel level = (ServerLevel) level();

        if (simulatedTick == SimulatedTick.EMPTY)
            this.simulatedTick = TimeAPI.simulateBlockTick(level, pos); // Create this once. No need for more...

        if (!(pos == null || !simulatedTick.canTick() || this.remainingTime <= 0)) {
            for (int i = 0; i < getTimeRate(); i++)
                simulatedTick.tick();

            this.remainingTime -= 1;
        } else
            this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(TIME_RATE, 1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        entityData.set(TIME_RATE, compound.getInt(TIME_RATE_NBT));
        setRemainingTime(compound.getInt(TIME_REMAINING_NBT));
        this.pos = NbtUtils.readBlockPos(compound.getCompound(BLOCK_POS_NBT));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt(TIME_RATE_NBT, getTimeRate());
        compound.putInt(TIME_REMAINING_NBT, getRemainingTime());
        compound.put(BLOCK_POS_NBT, NbtUtils.writeBlockPos(this.pos));
    }
}
