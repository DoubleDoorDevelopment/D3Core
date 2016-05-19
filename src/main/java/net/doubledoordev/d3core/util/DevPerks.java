/*
 * Copyright (c) 2014, DoubleDoorDevelopment
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
 *  Neither the name of the project nor the names of its
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
 */

package net.doubledoordev.d3core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;

/**
 * Something other than capes for once
 *
 * @author Dries007
 */
public class DevPerks
{
    private JsonObject  perks = new JsonObject();

    public DevPerks()
    {
        try
        {
            perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKSURL), Charset.forName("UTF-8"))).getAsJsonObject();
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
    }

    public static ItemStack getItemStackFromJson(JsonObject data, int defaultMeta, int defaultStacksize)
    {
        int meta = data.has("meta") ? data.get("meta").getAsInt() : defaultMeta;
        int size = data.has("size") ? data.get("size").getAsInt() : defaultStacksize;
        ItemStack stack = GameRegistry.makeItemStack(data.get("name").getAsString(), size, meta,null);
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
            for (JsonElement element : data.getAsJsonArray("lore")) lore.appendTag(new NBTTagString(element.getAsString()));
            display.setTag("Lore", lore);
            root.setTag("display", display);
            stack.setTagCompound(root);
        }
        return stack;
    }

    /**
     * Something other than capes for once
     */
    @SubscribeEvent
    public void nameFormatEvent(PlayerEvent.NameFormat event)
    {
        try
        {
            if (D3Core.debug()) perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKSURL), Charset.forName("UTF-8"))).getAsJsonObject();
            if (perks.has(event.getUsername()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getUsername());
                if (perk.has("displayname")) event.setDisplayname(perk.get("displayname").getAsString());
                if (perk.has("hat") && (event.getEntityPlayer().inventory.armorInventory[3] == null || event.getEntityPlayer().inventory.armorInventory[3].stackSize == 0))
                {
                    ItemStack hat = getItemStackFromJson(perk.getAsJsonObject("hat"), 0, 0);
                    hat.stackSize = 0;
                    event.getEntityPlayer().inventory.armorInventory[3] = hat;
                }
            }
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void cloneEvent(PlayerEvent.Clone event)
    {
        try
        {
            if (D3Core.debug()) perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKSURL), Charset.forName("UTF-8"))).getAsJsonObject();
            if (perks.has(event.getOriginal().getCommandSenderEntity().getName()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getOriginal().getCommandSenderEntity().getName());
                if (perk.has("hat") && (event.getEntityPlayer().inventory.armorInventory[3] == null || event.getEntityPlayer().inventory.armorInventory[3].stackSize == 0))
                {
                    ItemStack hat = getItemStackFromJson(perk.getAsJsonObject("hat"), 0, 0);
                    hat.stackSize = 0;
                    event.getEntityPlayer().inventory.armorInventory[3] = hat;
                }
            }
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void deathEvent(PlayerDropsEvent event)
    {
        try
        {
            if (D3Core.debug()) perks = new JsonParser().parse(IOUtils.toString(new URL(CoreConstants.PERKSURL), Charset.forName("UTF-8"))).getAsJsonObject();
            if (perks.has(event.getEntityPlayer().getCommandSenderEntity().getName()))
            {
                JsonObject perk = perks.getAsJsonObject(event.getEntityPlayer().getCommandSenderEntity().getName());
                if (perk.has("drop"))
                {
                    event.getDrops().add(new EntityItem(event.getEntityPlayer().getEntityWorld(), event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, getItemStackFromJson(perk.getAsJsonObject("drop"), 0, 1)));
                }
            }
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
    }

    public void update(boolean sillyness)
    {
        try
        {
            if (sillyness) MinecraftForge.EVENT_BUS.register(this);
            else MinecraftForge.EVENT_BUS.unregister(this);
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
        try
        {
            if (sillyness) FMLCommonHandler.instance().bus().register(this);
            else FMLCommonHandler.instance().bus().unregister(this);
        }
        catch (Exception e)
        {
            if (D3Core.debug()) e.printStackTrace();
        }
    }
}
