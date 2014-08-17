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

package net.doubledoordev.d3core.client;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.doubledoordev.d3core.util.CoreConstants;
import net.doubledoordev.d3core.util.ID3Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Dries007
 */
public class ModConfigGuiFactory implements IModGuiFactory
{
    public static class D3ConfigGuiScreen extends GuiConfig
    {
        public D3ConfigGuiScreen(GuiScreen parentScreen)
        {
            super(parentScreen, getConfigElements(), CoreConstants.MODID, false, false, I18n.format("d3.core.config.title"));
        }

        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> list = new ArrayList<>();
            for (ModContainer modContainer : Loader.instance().getActiveModList())
            {
                if (modContainer.getMod() instanceof ID3Mod)
                {
                    ((ID3Mod) modContainer.getMod()).addConfigElements(list);
                }
            }
            return list;
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {

    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return D3ConfigGuiScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}
