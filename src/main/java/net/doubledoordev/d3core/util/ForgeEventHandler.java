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

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
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
        if (event.showAdvancedItemTooltips)
        {
            if (enableStringID) event.toolTip.add(EnumChatFormatting.DARK_AQUA + GameData.getItemRegistry().getNameForObject(event.itemStack.getItem()));
            if (enableUnlocalizedName) event.toolTip.add(EnumChatFormatting.DARK_GREEN + event.itemStack.getUnlocalizedName());
            if (enableOreDictionary) for (int id : OreDictionary.getOreIDs(event.itemStack)) event.toolTip.add(EnumChatFormatting.DARK_PURPLE + OreDictionary.getOreName(id));
            if (enableBurnTime && TileEntityFurnace.isItemFuel(event.itemStack)) event.toolTip.add(EnumChatFormatting.GOLD + "Burns for " + TileEntityFurnace.getItemBurnTime(event.itemStack) + " ticks");
        }
    }

    @SubscribeEvent()
    public void entityDeathEvent(LivingDropsEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer && claysTortureMode)
        {
            event.setCanceled(true);
        }
        else if (event.entityLiving instanceof EntityEnderman && EndermanGriefing.dropCarrying)
        {
            EntityEnderman entityEnderman = ((EntityEnderman) event.entityLiving);
            if (entityEnderman.func_146080_bZ() != Blocks.air)
            {
                ItemStack stack = new ItemStack(entityEnderman.func_146080_bZ(), 1, entityEnderman.getCarryingData());
                event.drops.add(new EntityItem(entityEnderman.worldObj, entityEnderman.posX, entityEnderman.posY, entityEnderman.posZ, stack));
            }
        }
    }

    @SubscribeEvent()
    public void playerDeath(LivingDeathEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer && printDeathCoords)
        {
            ChatComponentText posText = new ChatComponentText("X: " + MathHelper.floor_double(event.entityLiving.posX) + " Y: " + MathHelper.floor_double(event.entityLiving.posY + 0.5d) + " Z: " + MathHelper.floor_double(event.entityLiving.posZ));
            try
            {
                if (!MinecraftServer.getServer().getCommandManager().getPossibleCommands((ICommandSender) event.entityLiving, "tp").isEmpty())
                {
                    posText.setChatStyle(new ChatStyle().setItalic(true)
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to teleport!")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + event.entityLiving.posX + " " + (event.entityLiving.posY + 0.5d) + " " + event.entityLiving.posZ)));
                }
            }
            catch (Exception ignored)
            {

            }

            ((EntityPlayer) event.entityLiving).addChatComponentMessage(new ChatComponentText("You died at ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)).appendSibling(posText));
        }
    }

    @SubscribeEvent()
    public void sleepEvent(PlayerSleepInBedEvent event)
    {
        if (nosleep || CoreConstants.isAprilFools())
        {
            event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
        }
    }

    @SubscribeEvent
    public void aprilFools(ServerChatEvent event)
    {
        if (CoreConstants.isAprilFools())
        {
            ChatStyle style = event.component.getChatStyle();
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
            style.setColor(EnumChatFormatting.values()[CoreConstants.RANDOM.nextInt(EnumChatFormatting.values().length)]);
            event.component.setChatStyle(style);
        }
    }

    @SubscribeEvent
    public void aprilFools(PlayerEvent.NameFormat event)
    {
        if (CoreConstants.isAprilFools())
        {
            event.displayname = "§k" + event.displayname;
        }
    }
}
