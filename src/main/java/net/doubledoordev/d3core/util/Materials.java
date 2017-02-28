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

import com.google.common.base.Strings;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author Dries007
 */
public class Materials
{
    public static void load() throws IOException
    {
        File file = new File(D3Core.getFolder(), "materials.json");
        if (!file.exists()) return;

        Map<String, String> stringMap = CoreConstants.GSON.<Map<String, String>>fromJson(FileUtils.readFileToString(file, "utf-8"), Map.class);
        Map<String, ItemStack> itemStackMap = new HashMap<>(stringMap.size());

        boolean stop = false;
        for (Map.Entry<String, String> entry : stringMap.entrySet())
        {
            Matcher matcher = CoreConstants.PATTERN_ITEMSTACK.matcher(entry.getValue());
            if (!matcher.matches())
            {
                D3Core.getLogger().error("Entry in materials.json does not match a valid ItemStack text: {}.", entry);
                stop = true;
                continue;
            }
            String mod = matcher.group("mod");
            String name = matcher.group("name");
            String metaString = matcher.group("meta");
            String stacksizeString = matcher.group("stacksize");
            if (Strings.isNullOrEmpty(mod)) mod = "minecraft";
            if (!Loader.isModLoaded(mod))
            {
                D3Core.getLogger().warn("Skipped materials.json entry {} because mod {} is not loaded.", entry, mod);
                continue;
            }
            int meta = net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;
            int stacksize = 1;
            // should not throw NumberFormatException since the regex does number check
            if (!Strings.isNullOrEmpty(metaString) && !metaString.equals("*")) meta = Integer.parseInt(metaString);
            if (!Strings.isNullOrEmpty(stacksizeString)) stacksize = Integer.parseInt(stacksizeString);

            Item item = Item.REGISTRY.getObject(new ResourceLocation(mod, name));
            if (item == null)
            {
                D3Core.getLogger().error("Entry in materials.json {} invalid. Item {}:{} does not exist.", entry, mod, name);
                stop = true;
                continue;
            }
            ItemStack stack = new ItemStack(item, stacksize, meta);
            itemStackMap.put(entry.getKey().toLowerCase(), stack);
        }
        if (stop)
        {
            D3Core.getLogger().error("The proper format for materials.json entries is (in regex): {}", CoreConstants.PATTERN_ITEMSTACK.pattern());
            RuntimeException e = new RuntimeException("You have invalid entries in your materials.json file. Check the log for more info.");
            e.setStackTrace(new StackTraceElement[0]); // No need for this
            throw e;
        }

        for (ToolMaterial material : ToolMaterial.values())
        {
            ItemStack stack = itemStackMap.get(material.name().toLowerCase());
            if (stack == ItemStack.EMPTY || material.getRepairItemStack() != ItemStack.EMPTY) continue;
            material.setRepairItem(stack);
        }
    }
}
