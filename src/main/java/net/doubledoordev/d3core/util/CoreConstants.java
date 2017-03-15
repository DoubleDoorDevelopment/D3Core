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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class CoreConstants
{
    public static final String MODID = "d3core";
    public static final String NAME = "D³Core";
    public static final String BASE_URL = "http://doubledoordev.net/";
    public static final String PERKS_URL = BASE_URL + "perks.json";
    public static final String UPDATE_URL = BASE_URL + MODID + ".json";

    /** @see net.doubledoordev.d3core.client.ModConfigGuiFactory */
    public static final String MOD_GUI_FACTORY = "net.doubledoordev.d3core.client.ModConfigGuiFactory";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Random RANDOM = new Random();
    public static final String LOGIN_MESSAGE_FILE = "loginmessage.txt";
    public static final Pattern PATTERN_ITEMSTACK = Pattern.compile("^(?:(?<mod>.*):)?(?<name>.*?) ?(?<meta>\\*|\\d+)? ?(?<stacksize>\\d+)?$");

    public static void spawnRandomFireworks(Entity target, int rad, int rockets)
    {
        while (rockets-- > 0)
        {
            ItemStack itemStack = new ItemStack(Items.FIREWORKS);
            NBTTagCompound fireworks = new NBTTagCompound();
            NBTTagList explosions = new NBTTagList();

            int charges = 1 + CoreConstants.RANDOM.nextInt(3);
            while (charges-- > 0)
            {
                NBTTagCompound explosion = new NBTTagCompound();

                if (CoreConstants.RANDOM.nextBoolean()) explosion.setBoolean("Flicker", true);
                if (CoreConstants.RANDOM.nextBoolean()) explosion.setBoolean("Trail", true);

                int[] colors = new int[1 + CoreConstants.RANDOM.nextInt(3)];

                for (int i = 0; i < colors.length; i++)
                {
                    colors[i] = (CoreConstants.RANDOM.nextInt(256) << 16) + (CoreConstants.RANDOM.nextInt(256) << 8) + CoreConstants.RANDOM.nextInt(256);
                }

                explosion.setIntArray("Colors", colors);
                explosion.setByte("Type", (byte) CoreConstants.RANDOM.nextInt(5));

                if (CoreConstants.RANDOM.nextBoolean())
                {
                    int[] fadeColors = new int[1 + CoreConstants.RANDOM.nextInt(3)];

                    for (int i = 0; i < fadeColors.length; i++)
                    {
                        fadeColors[i] = (CoreConstants.RANDOM.nextInt(256) << 16) + (CoreConstants.RANDOM.nextInt(256) << 8) + CoreConstants.RANDOM.nextInt(256);
                    }
                    explosion.setIntArray("FadeColors", fadeColors);
                }

                explosions.appendTag(explosion);
            }
            fireworks.setTag("Explosions", explosions);
            fireworks.setByte("Flight", (byte) (CoreConstants.RANDOM.nextInt(2)));

            NBTTagCompound root = new NBTTagCompound();
            root.setTag("Fireworks", fireworks);
            itemStack.setTagCompound(root);
            target.world.spawnEntity(new EntityFireworkRocket(target.world, target.posX + CoreConstants.RANDOM.nextInt(rad) - rad / 2.0, target.posY, target.posZ + CoreConstants.RANDOM.nextInt(rad) - rad / 2.0, itemStack));
        }
    }
}
