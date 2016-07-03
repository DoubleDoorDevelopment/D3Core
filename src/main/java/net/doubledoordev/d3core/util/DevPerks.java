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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Something other than capes for once
 *
 * @author Dries007
 */
public class DevPerks
{
    private JsonObject perks = new JsonObject();

    public DevPerks()
    {
        update();
    }

    private void update()
    {
        try
        {
            perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKS_URL), Charset.forName("UTF-8"))).getAsJsonObject();
        }
        catch (Exception e)
        {
            D3Core.getLogger().warn("There may be an error in devperks, no sillyness for you...", e);
            if (D3Core.isDebug()) e.printStackTrace();
        }
        if (perks == null) perks = new JsonObject();
    }

    private static ItemStack getItemStackFromJson(JsonObject data, int defaultMeta, int defaultStacksize)
    {
        int meta = data.has("meta") ? data.get("meta").getAsInt() : defaultMeta;
        int size = data.has("size") ? data.get("size").getAsInt() : defaultStacksize;
        ItemStack stack = GameRegistry.makeItemStack(data.get("name").getAsString(), size, meta, null);
        if (stack == null) return null;
        if (data.has("display")) stack.setStackDisplayName(data.get("display").getAsString());
        if (data.has("color"))
        {
            NBTTagCompound root = stack.getTagCompound();
            if (root == null) root = new NBTTagCompound();
            NBTTagCompound display = root.getCompoundTag("display");
            display.setInteger("color", data.get("color").getAsInt());
            root.setTag("display", display);
            stack.setTagCompound(root);
        }
        if (data.has("lore"))
        {
            NBTTagCompound root = stack.getTagCompound();
            if (root == null) root = new NBTTagCompound();
            NBTTagCompound display = root.getCompoundTag("display");
            NBTTagList lore = new NBTTagList();
            for (JsonElement element : data.getAsJsonArray("lore"))
                lore.appendTag(new NBTTagString(element.getAsString()));
            display.setTag("Lore", lore);
            root.setTag("display", display);
            stack.setTagCompound(root);
        }
        return stack;
    }

    @SubscribeEvent
    public void nameFormatEvent(PlayerEvent.NameFormat event)
    {
        try
        {
            if (D3Core.isDebug()) update();
            if (perks.has(event.getUsername()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getUsername());
                if (perk.has("displayname")) event.setDisplayname(perk.get("displayname").getAsString());
                doHat(perk, event.getEntityPlayer());
            }
        }
        catch (Exception e)
        {
            if (D3Core.isDebug()) e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        try
        {
            if (D3Core.isDebug()) update();
            if (perks.has(event.player.getName()))
            {
                JsonObject perk = perks.getAsJsonObject(event.player.getName());
                if (perk.has("fireworks"))
                {
                    JsonObject fw = perk.getAsJsonObject("fireworks");
                    if (fw.has("login"))
                    {
                        JsonObject obj = fw.getAsJsonObject("login");
                        int rad = obj.has("radius") ? obj.get("radius").getAsInt() : 5;
                        int rockets = obj.has("rockets") ? obj.get("rockets").getAsInt() : 5;
                        CoreConstants.spawnRandomFireworks(event.player, rad + CoreConstants.RANDOM.nextInt(rad), rockets + CoreConstants.RANDOM.nextInt(rockets));
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (D3Core.isDebug()) e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void achievementEvent(AchievementEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && player.getServer() != null)
        {
            StatisticsManagerServer sms = player.getServer().getPlayerList().getPlayerStatsFile(player);
            if (sms.canUnlockAchievement(event.getAchievement()) && !sms.hasAchievementUnlocked(event.getAchievement()))
            {
                try
                {
                    if (D3Core.isDebug()) update();
                    if (perks.has(player.getName()))
                    {
                        JsonObject perk = perks.getAsJsonObject(player.getName());
                        if (perk.has("fireworks"))
                        {
                            JsonObject fw = perk.getAsJsonObject("fireworks");
                            if (fw.has("achievement"))
                            {
                                JsonObject obj = fw.getAsJsonObject("achievement");
                                int rad = obj.has("radius") ? obj.get("radius").getAsInt() : 5;
                                int rockets = obj.has("rockets") ? obj.get("rockets").getAsInt() : 5;
                                CoreConstants.spawnRandomFireworks(player, rad + CoreConstants.RANDOM.nextInt(rad), rockets + CoreConstants.RANDOM.nextInt(rockets));
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    if (D3Core.isDebug()) e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void cloneEvent(PlayerEvent.Clone event)
    {
        try
        {
            if (D3Core.isDebug()) update();
            if (perks.has(event.getOriginal().getName()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getOriginal().getName());
                doHat(perk, event.getEntityPlayer());
            }
        }
        catch (Exception e)
        {
            if (D3Core.isDebug()) e.printStackTrace();
        }
    }

    private void doHat(JsonObject perk, EntityPlayer player)
    {
        if (perk.has("hat") && (player.inventory.armorInventory[3] == null || player.inventory.armorInventory[3].stackSize == 0))
        {
            ItemStack hat = getItemStackFromJson(perk.getAsJsonObject("hat"), 0, 0);
            if (hat == null) return;
            hat.stackSize = 0;
            player.inventory.armorInventory[3] = hat;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void deathEvent(PlayerDropsEvent event)
    {
        try
        {
            if (D3Core.isDebug())
                perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKS_URL), Charset.forName("UTF-8"))).getAsJsonObject();
            if (perks.has(event.getEntityPlayer().getName()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getEntityPlayer().getName());
                if (perk.has("drop"))
                {
                    ItemStack stack = getItemStackFromJson(perk.getAsJsonObject("drop"), 0, 1);
                    if (stack == null) return;
                    event.getDrops().add(new EntityItem(event.getEntityPlayer().getEntityWorld(), event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, stack));
                }
            }
        }
        catch (Exception e)
        {
            if (D3Core.isDebug()) e.printStackTrace();
        }
    }

    public void update(boolean silliness)
    {
        try
        {
            if (silliness) MinecraftForge.EVENT_BUS.register(this);
            else MinecraftForge.EVENT_BUS.unregister(this);
        }
        catch (Exception e)
        {
            if (D3Core.isDebug()) e.printStackTrace();
        }
    }
}
