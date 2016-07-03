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

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author Dries007
 */
public class ForgeEventHandler
{
    public static final ForgeEventHandler FORGE_EVENT_HANDLER = new ForgeEventHandler();
    public boolean enableStringID;
    public boolean enableUnlocalizedName;
    public boolean enableOreDictionary;
    public boolean enableBurnTime;
    public boolean nosleep;
    public boolean printDeathCoords = true;
    public boolean claysTortureMode;

    private ForgeEventHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemTooltipEventHandler(ItemTooltipEvent event)
    {
        if (event.isShowAdvancedItemTooltips())
        {
            if (enableStringID) event.getToolTip().add(TextFormatting.DARK_AQUA + event.getItemStack().getItem().getRegistryName().toString());
            if (enableUnlocalizedName) event.getToolTip().add(TextFormatting.DARK_GREEN + event.getItemStack().getUnlocalizedName());
            if (enableOreDictionary) for (int id : OreDictionary.getOreIDs(event.getItemStack())) event.getToolTip().add(TextFormatting.DARK_PURPLE + OreDictionary.getOreName(id));
            if (enableBurnTime && TileEntityFurnace.isItemFuel(event.getItemStack())) event.getToolTip().add(TextFormatting.GOLD + "Burns for " + TileEntityFurnace.getItemBurnTime(event.getItemStack()) + " ticks");
        }
    }

    @SubscribeEvent()
    public void entityDeathEvent(LivingDropsEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer && claysTortureMode)
        {
            event.setCanceled(true);
        }
        else if (event.getEntityLiving() instanceof EntityEnderman && EndermanGriefing.dropCarrying)
        {
            EntityEnderman entityEnderman = ((EntityEnderman) event.getEntityLiving());
            if (entityEnderman.getHeldBlockState() != Blocks.AIR)
            {
                ItemStack stack = new ItemStack(entityEnderman.getHeldBlockState().getBlock(), 1, entityEnderman.getHeldBlockState().getBlock().getMetaFromState(entityEnderman.getHeldBlockState()));
                event.getDrops().add(new EntityItem(entityEnderman.worldObj, entityEnderman.posX, entityEnderman.posY, entityEnderman.posZ, stack));
            }
        }
    }

    @SubscribeEvent()
    public void playerDeath(LivingDeathEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer && printDeathCoords)
        {
            TextComponentString posText = new TextComponentString("X: " + MathHelper.floor_double(event.getEntityLiving().posX) + " Y: " + MathHelper.floor_double(event.getEntityLiving().posY + 0.5d) + " Z: " + MathHelper.floor_double(event.getEntityLiving().posZ));
            try
            {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (!server.getCommandManager().getPossibleCommands((ICommandSender) event.getEntityLiving()).contains(server.getCommandManager().getCommands().get("tp")))
                {
                    posText.setStyle(new Style().setItalic(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to teleport!")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + event.getEntityLiving().posX + " " + (event.getEntityLiving().posY + 0.5d) + " " + event.getEntityLiving().posZ)));
                }
            }
            catch (Exception ignored)
            {

            }

            ((EntityPlayer) event.getEntityLiving()).addChatComponentMessage(new TextComponentString("You died at ").setStyle(new Style().setColor(TextFormatting.AQUA)).appendSibling(posText));
        }
    }

    @SubscribeEvent()
    public void sleepEvent(PlayerSleepInBedEvent event)
    {
        if (nosleep || CoreConstants.isAprilFools())
        {
            event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
        }
    }

    @SubscribeEvent
    public void aprilFools(ServerChatEvent event)
    {
        if (CoreConstants.isAprilFools())
        {

            Style style = event.getComponent().getStyle();
            float chance = 0.25f;
            if (CoreConstants.RANDOM.nextFloat() < chance)
            {
                style.setBold(true);
                chance *= chance;
            }
            if (CoreConstants.RANDOM.nextFloat() < chance)
            {
                style.setItalic(true);
                chance *= chance;
            }
            if (CoreConstants.RANDOM.nextFloat() < chance)
            {
                style.setUnderlined(true);
                chance *= chance;
            }
            if (CoreConstants.RANDOM.nextFloat() < chance)
            {
                style.setStrikethrough(true);
                chance *= chance;
            }
            if (CoreConstants.RANDOM.nextFloat() < chance)
            {
                style.setObfuscated(true);
            }
            style.setColor(TextFormatting.values()[CoreConstants.RANDOM.nextInt(TextFormatting.values().length)]);
            event.getComponent().setStyle(style);

        }
    }

    @SubscribeEvent
    public void aprilFools(PlayerEvent.NameFormat event)
    {
        if (CoreConstants.isAprilFools())
        {
            event.setDisplayname("§k" + event.getDisplayname());
        }
    }
}
