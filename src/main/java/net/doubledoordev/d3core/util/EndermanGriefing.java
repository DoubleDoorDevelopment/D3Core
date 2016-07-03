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

import net.doubledoordev.d3core.D3Core;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Dries007
 */
public class EndermanGriefing
{
    public static boolean disable;
    public static boolean dropCarrying;
    public static String[] blacklist;
    public static String[] addList;

    private static HashMap<String, Boolean> reverseMap = new HashMap<>();

    public static void init()
    {
        if (disable)
        {
            RegistryNamespacedDefaultedByKey<ResourceLocation, Block> blockData = Block.REGISTRY;
            for (ResourceLocation key : blockData.getKeys())
            {
                Block block = blockData.getObject(key);
                reverseMap.put(blockData.getNameForObject(block).toString(), EntityEnderman.getCarriable(block));
                EntityEnderman.setCarriable(block, false);
            }
        }
        else
        {
            int added = 0, removed = 0;
            for (String item : addList)
            {
                Set<Block> blocks = matchBlock(item);
                if (blocks.isEmpty())  D3Core.getLogger().warn("[EndermanGriefing] '{}' does not match any block...", item);
                else
                {
                    for (Block block : blocks)
                    {
                        reverseMap.put(item, EntityEnderman.getCarriable(block));
                        EntityEnderman.setCarriable(block, true);
                        added ++;
                    }
                }
            }
            for (String item : blacklist)
            {
                Set<Block> blocks = matchBlock(item);
                if (blocks.isEmpty()) D3Core.getLogger().warn("[EndermanGriefing] '{}' does not match any block...", item);
                else
                {
                    for (Block block : blocks)
                    {
                        reverseMap.put(item, EntityEnderman.getCarriable(block));
                        EntityEnderman.setCarriable(block, false);
                        removed ++;
                    }
                }
            }
            D3Core.getLogger().info("[EndermanGriefing] Added {} and removed {} blocks to the Enderman grab list...", added, removed);
        }
    }

    private static Set<Block> matchBlock(String item)
    {
        Set<Block> blocks = new HashSet<>();
        Pattern pattern = Pattern.compile(item.replace("*", ".*?"));
        RegistryNamespacedDefaultedByKey<ResourceLocation, Block> blockData = Block.REGISTRY;
        for (Block block : blockData)
        {
            if (pattern.matcher(blockData.getNameForObject(block).toString()).matches())
            {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static void undo()
    {
        for (String entry : reverseMap.keySet())
        {
            EntityEnderman.setCarriable(Block.REGISTRY.getObject(new ResourceLocation(entry)), reverseMap.get(entry));
        }
    }
}
