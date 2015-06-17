/*
 * Copyright (c) 2014,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the {organization} nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package net.doubledoordev.d3core.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.storage.WorldInfo;

/**
 * @author Dries007
 */
public class FMLEventHandler
{
    public static final FMLEventHandler FML_EVENT_HANDLER = new FMLEventHandler();
    private FMLEventHandler()
    {
    }

    public boolean norain;
    public boolean insomnia;

    @SubscribeEvent
    public void worldTickHandler(TickEvent.WorldTickEvent event)
    {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.START) return;

        if (norain)
        {
            WorldInfo worldInfo = event.world.getWorldInfo();
            worldInfo.setThundering(false);
            worldInfo.setRaining(false);
            worldInfo.setRainTime(Integer.MAX_VALUE);
            worldInfo.setThunderTime(Integer.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public void playerTickHandler(TickEvent.PlayerTickEvent event)
    {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.START) return;

        if (insomnia)
        {
            if (event.player.sleepTimer > 90)
            {
                event.player.sleepTimer = 90;
            }
        }
    }
}
