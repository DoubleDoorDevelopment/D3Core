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

package net.doubledoordev.d3core.client;

import net.doubledoordev.d3core.events.D3LanguageInjectEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This whole file is a hack, and contains code not fit for human eyes. It may make you dumber.
 * It's one and only purpose is to dynamically add roman numerals to the language file.
 * It requires 2 lines in the access transformers, since I didn't feel like using reflection.
 * It also has to register a ReloadListener with the ResourceManager since language files can be reloaded.
 * It also figures out the maximum possible enchant level by setting an Enchantment on an actual ItemStack, since mojang code does weird casting.
 *
 */
public class LanguageHelper
{
    private final static LinkedHashMap<String, Integer> ROMAN_NUMERALS = new LinkedHashMap<>();
    public static final LanguageHelper I = new LanguageHelper();

    static
    {
        ROMAN_NUMERALS.put("M", 1000);
        ROMAN_NUMERALS.put("CM", 900);
        ROMAN_NUMERALS.put("D", 500);
        ROMAN_NUMERALS.put("CD", 400);
        ROMAN_NUMERALS.put("C", 100);
        ROMAN_NUMERALS.put("XC", 90);
        ROMAN_NUMERALS.put("L", 50);
        ROMAN_NUMERALS.put("XL", 40);
        ROMAN_NUMERALS.put("X", 10);
        ROMAN_NUMERALS.put("IX", 9);
        ROMAN_NUMERALS.put("V", 5);
        ROMAN_NUMERALS.put("IV", 4);
        ROMAN_NUMERALS.put("I", 1);
    }
    private static final String PREFIX = "enchantment.level.";

    public static void run()
    {
        //noinspection NullableProblems
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener()
        {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager)
            {
                D3LanguageInjectEvent event = new D3LanguageInjectEvent();
                MinecraftForge.EVENT_BUS.post(event);
                LanguageMap.replaceWith(event.map);
            }
        });
    }

    /*
     * Thanks stackoverflow:
     * http://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
     */
    private static String romanNumerals(int Int)
    {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet())
        {
            int matches = Int/entry.getValue();
            String s = entry.getKey();
            for (int i = 0; i < matches; i++) builder.append(s);
            Int = Int % entry.getValue();
        }
        return builder.toString();
    }

    @SubscribeEvent
    public void d3LanguageInjectEvent(D3LanguageInjectEvent event)
    {
        Enchantment enchantment = null;
        //noinspection StatementWithEmptyBody
        for (int i = 0; enchantment == null; enchantment = Enchantment.REGISTRY.getObjectById(i++));
        Item item = null;
        //noinspection StatementWithEmptyBody
        for (int i = 0; item == null; item = Item.REGISTRY.getObjectById(i++));
        ItemStack s = new ItemStack(item);
        s.addEnchantment(enchantment, Integer.MAX_VALUE);
        final int max = EnchantmentHelper.getEnchantmentLevel(enchantment, s);
        for (int i = 0; i < max; i++)
        {
            String key = PREFIX + i;
            if (!event.map.containsKey(key))
            {
                String val = romanNumerals(i);
                event.map.put(key, val);
            }
        }
    }
}
