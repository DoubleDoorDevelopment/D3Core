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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

import static net.doubledoordev.d3core.util.CoreConstants.MODID;

/**
 * @author Dries007
 */
public class VoidRefunds
{
    public static final VoidRefunds VOID_REFUNDS = new VoidRefunds();
    private int[] voidRefundDimensions;

    private final HashMap<UUID, InventoryPlayer> map = new HashMap<>();

    private VoidRefunds()
    {
    }

    public void config(Configuration configuration)
    {
        final String catVoidDeaths = MODID + ".VoidDeaths";
        configuration.addCustomCategoryComment(catVoidDeaths, "In these dimensions, when you die to void damage, you will keep your items.");
        voidRefundDimensions = configuration.get(catVoidDeaths, "refundDimensions", new int[] {}).getIntList();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingDeathEvent(LivingDeathEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        if (event.source != DamageSource.outOfWorld || !(event.entity instanceof EntityPlayer)) return;
        if (event.entityLiving.lastDamage >= (Float.MAX_VALUE / 2)) return; // try to ignore /kill command
        for (int dim : voidRefundDimensions)
        {
            if (dim != event.entity.dimension) continue;
            event.setCanceled(true);

            InventoryPlayer tempCopy = new InventoryPlayer(null);
            tempCopy.copyInventory(((EntityPlayer) event.entity).inventory);
            map.put(event.entity.getPersistentID(), tempCopy);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        InventoryPlayer oldInventory = map.get(event.player.getPersistentID());
        if (oldInventory == null) return;
        event.player.inventory.copyInventory(oldInventory);
        map.remove(event.player.getPersistentID());
    }
}
