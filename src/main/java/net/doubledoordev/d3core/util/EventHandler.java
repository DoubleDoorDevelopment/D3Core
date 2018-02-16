/*
 * Copyright (c) 2014-2016, Dries007 & DoubleDoorDevelopment
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
 *  Neither the name of DoubleDoorDevelopment nor the names of its
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
 */

package net.doubledoordev.d3core.util;

import com.google.gson.JsonParseException;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Dries007
 */
@Mod.EventBusSubscriber(modid = CoreConstants.MODID)
public final class EventHandler
{
    public static boolean enableStringID;
    public static boolean enableUnlocalizedName;
    public static boolean enableOreDictionary;
    public static boolean enableBurnTime;
    public static boolean nosleep;
    public static boolean printDeathCoords = true;
    public static boolean claysTortureMode;
    public static boolean norain;
    public static boolean insomnia;
    public static boolean lilypad;
    public static boolean achievementFireworks;

    private EventHandler() {}

    @SubscribeEvent
    public static void achievementEvent(AdvancementEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (achievementFireworks && FMLCommonHandler.instance().getEffectiveSide().isServer() && player.getServer() != null)
        {
            CoreConstants.spawnRandomFireworks(player, 1, 1);
        }
    }

    @SubscribeEvent
    public static void entityDeathEvent(LivingDropsEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer && claysTortureMode)
        {
            event.setCanceled(true);
        }
        else if (event.getEntityLiving() instanceof EntityEnderman && EndermanGriefing.dropCarrying)
        {
            EntityEnderman entityEnderman = ((EntityEnderman) event.getEntityLiving());
            IBlockState state = entityEnderman.getHeldBlockState();
            if (state != null && state.getBlock() != Blocks.AIR)
            {
                ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
                event.getDrops().add(new EntityItem(entityEnderman.world, entityEnderman.posX, entityEnderman.posY, entityEnderman.posZ, stack));
            }
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer && printDeathCoords)
        {
            TextComponentString posText = new TextComponentString("X: " + MathHelper.floor(event.getEntityLiving().posX) + " Y: " + MathHelper.floor(event.getEntityLiving().posY + 0.5d) + " Z: " + MathHelper.floor(event.getEntityLiving().posZ));
            try
            {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (server.getCommandManager().getPossibleCommands(event.getEntityLiving()).contains(server.getCommandManager().getCommands().get("tp")))
                {
                    posText.setStyle(new Style().setItalic(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to teleport!")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + MathHelper.floor(event.getEntityLiving().posX) + " " + MathHelper.floor(event.getEntityLiving().posY + 0.5d) + " " + MathHelper.floor(event.getEntityLiving().posZ))));
                }
                if (server.getCommandManager().getPossibleCommands(event.getEntityLiving()).contains(server.getCommandManager().getCommands().get("tpx")))
                {
                    posText.setStyle(new Style().setItalic(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to teleport!")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpx " + ((EntityPlayer) event.getEntityLiving()).dimension + " " + MathHelper.floor(event.getEntityLiving().posX) + " " + MathHelper.floor(event.getEntityLiving().posY + 0.5d) + " " + MathHelper.floor(event.getEntityLiving().posZ))));
                }
            }
            catch (Exception ignored)
            {

            }            
            event.getEntityLiving().sendMessage(new TextComponentString("You died at ").setStyle(new Style().setColor(TextFormatting.AQUA)).appendSibling(posText));
        }
    }

    @SubscribeEvent
    public static void sleepEvent(PlayerSleepInBedEvent event)
    {
        if (nosleep)
        {
            event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
        }
    }

    @SubscribeEvent
    public static void worldTickHandler(TickEvent.WorldTickEvent event)
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
    public static void playerTickHandler(TickEvent.PlayerTickEvent event)
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

    @SubscribeEvent
    public static void playerLoggedInEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event)
    {
        File file = new File(D3Core.getFolder(), "loginmessage.txt");
        if (file.exists())
        {
            try
            {
                String txt = FileUtils.readFileToString(file, Charset.defaultCharset());
                try
                {
                    event.player.sendMessage(ITextComponent.Serializer.jsonToComponent(txt));
                }
                catch (JsonParseException jsonparseexception)
                {
                    event.player.sendMessage(new TextComponentString(txt));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (lilypad) lilypad(event.player);
    }

    @SubscribeEvent
    public static void playerRespawnEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event)
    {
        if (lilypad) lilypad(event.player);
    }

    private static void lilypad(EntityPlayer player)
    {
        World world = player.world;

        BlockPos blockPos = new BlockPos((int)(player.posX),(int)(player.posY),(int)(player.posZ));

        if (blockPos.getX() < 0) blockPos = blockPos.add(-1,0,0);
        if (blockPos.getZ() < 0) blockPos = blockPos.add(0,0,-1);

        int limiter = world.getActualHeight() * 2;

        while (world.getBlockState(blockPos).getMaterial() == Material.WATER && --limiter != 0) blockPos = blockPos.add(0,1,0);
        while (world.getBlockState(blockPos).getMaterial() == Material.AIR && --limiter != 0) blockPos = blockPos.add(0,-1,0);
        if (limiter == 0) return;
        if (world.getBlockState(blockPos).getMaterial() == Material.WATER)
        {
            world.setBlockState(blockPos.add(0,1,0), Blocks.WATERLILY.getDefaultState());
            player.setPositionAndUpdate(blockPos.getX() + 0.5,blockPos.getY() + 2,blockPos.getZ() + 0.5);
        }
    }
}
