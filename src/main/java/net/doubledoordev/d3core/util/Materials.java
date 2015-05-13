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

import cpw.mods.fml.common.registry.GameRegistry;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Dries007
 */
public class Materials
{
    public static void load()
    {
        File file = new File(D3Core.getFolder(), "materials.json");
        if (!file.exists())
        {
            try
            {
                FileUtils.write(file, "{}", "utf-8");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Map<String, String> map = CoreConstants.GSON.<Map<String, String>>fromJson(FileUtils.readFileToString(file, "utf-8"), Map.class);
            for (ToolMaterial material : ToolMaterial.values())
            {
                String itemName = map.get(material.name());
                if (itemName != null)
                {
                    String modid = itemName.substring(0, itemName.indexOf(':'));
                    String name = itemName.substring(itemName.indexOf(':' + 1));
                    Item item = GameRegistry.findItem(modid, name);
                    if (item != null) material.customCraftingMaterial = item;
                    else
                    {
                        D3Core.getLogger().warn("Tried to assign item {} to material {}. That item doesn't exist.", itemName, material.name());
                    }
                    map.remove(material.name());
                }
            }
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                D3Core.getLogger().warn("Tried to assign item {} to material {}. That material doesn't exist.", entry.getValue(), entry.getKey());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
