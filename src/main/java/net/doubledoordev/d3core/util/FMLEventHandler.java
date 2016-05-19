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

import com.google.gson.JsonParseException;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.doubledoordev.d3core.D3Core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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
    public boolean lilypad;

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

    int aprilFoolsDelay = 0;
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


        if (CoreConstants.isAprilFools())
        {
            if (aprilFoolsDelay-- == 0)
            {
                aprilFoolsDelay = 100 * (5 + CoreConstants.RANDOM.nextInt(FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount()));
                CoreConstants.spawnRandomFireworks(event.player, 1 + CoreConstants.RANDOM.nextInt(5), 1 + CoreConstants.RANDOM.nextInt(5));
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        File file = new File(D3Core.getFolder(), "loginmessage.txt");
        if (file.exists())
        {
            try
            {
                String txt = FileUtils.readFileToString(file);
                try
                {
                    event.player.addChatMessage(ITextComponent.Serializer.jsonToComponent(txt));
                }
                catch (JsonParseException jsonparseexception)
                {
                    event.player.addChatMessage(new TextComponentString(txt));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (lilypad) lilypad(event.player);
        if (CoreConstants.isAprilFools()) CoreConstants.spawnRandomFireworks(event.player, 1 + CoreConstants.RANDOM.nextInt(5), 1 + CoreConstants.RANDOM.nextInt(5));
    }

    @SubscribeEvent
    public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        if (lilypad) lilypad(event.player);
        if (CoreConstants.isAprilFools()) CoreConstants.spawnRandomFireworks(event.player, 1 + CoreConstants.RANDOM.nextInt(5), 1 + CoreConstants.RANDOM.nextInt(5));
    }

    private void lilypad(EntityPlayer player)
    {
        World world = player.worldObj;

        BlockPos blockPos = new BlockPos((int)(player.posX),(int)(player.posY),(int)(player.posZ));

        if (blockPos.getX() < 0) blockPos.add(-1,0,0);
        if (blockPos.getZ() < 0) blockPos.add(0,0,-1);

        int limiter = world.getActualHeight() * 2;

        while (world.getBlockState(blockPos).getMaterial() == Material.WATER && --limiter != 0) blockPos.add(0,1,0);
        while (world.getBlockState(blockPos).getMaterial() == Material.AIR && --limiter != 0) blockPos.add(0,-1,0);
        if (limiter == 0) return;
        if (world.getBlockState(blockPos).getMaterial() == Material.WATER)
        {
            world.setBlockState(blockPos.add(0,1,0), Blocks.WATERLILY.getDefaultState());
            player.setPositionAndUpdate(blockPos.getX() + 0.5,blockPos.getY() + 2,blockPos.getZ() + 0.5);
        }
    }
}
