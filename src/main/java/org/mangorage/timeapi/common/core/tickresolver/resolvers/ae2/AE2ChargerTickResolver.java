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

package org.mangorage.timeapi.common.core.tickresolver.resolvers.ae2;

import appeng.blockentity.misc.ChargerBlockEntity;
import org.mangorage.timeapi.api.RegisterResolver;
import org.mangorage.timeapi.common.core.tickresolver.TickResolver;

@RegisterResolver(modID = "ae2", resolverID = AE2ChargerTickResolver.ID)
public class AE2ChargerTickResolver extends TickResolver<ChargerBlockEntity> {
    protected static final String ID = "ae2Charger";
    @Override
    public void tick(ChargerBlockEntity blockEntity) {
        blockEntity.tickingRequest(blockEntity.getGridNode(), 10_000);
    }
    @Override
    public Class<ChargerBlockEntity> getResolvedClass() {
        return ChargerBlockEntity.class;
    }

    @Override
    public String getID() {
        return ID;
    }
}
