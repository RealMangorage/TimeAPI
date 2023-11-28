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

import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class TickResolver<R extends BlockEntity> {
    abstract public void tick(R blockEntity);
    abstract public Class<R> getResolvedClass();

    /**
     * Only use this if you know what your doing.
     * Otherwise use the above tick method.
     * @param blockEntity
     * @throws ClassCastException
     */
    @SuppressWarnings("all")
    public void tickUnsafe(BlockEntity blockEntity) throws ClassCastException {
        tick((R) blockEntity);
    }
}
