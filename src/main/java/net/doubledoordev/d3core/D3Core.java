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

package net.doubledoordev.d3core;

import net.doubledoordev.d3core.client.LanguageHelper;
import net.doubledoordev.d3core.util.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static net.doubledoordev.d3core.util.CoreConstants.*;
import static net.doubledoordev.d3core.util.VoidRefunds.VOID_REFUNDS;

/**
 * @author Dries007
 */
@Mod(modid = MODID, name = NAME, updateJSON = UPDATE_URL, guiFactory = MOD_GUI_FACTORY)
public class D3Core
{
    @SuppressWarnings("WeakerAccess")
    @Mod.Instance(MODID)
    public static D3Core instance;

    @Mod.Metadata
    private ModMetadata metadata;

    private Logger logger;
    private DevPerks devPerks;
    private Configuration configuration;
    private File folder;

    private boolean debug = false;
    private boolean silliness = true;
    private boolean aprilFools = true;

    private boolean pastPost;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventHandler.I);
        MinecraftForge.EVENT_BUS.register(VOID_REFUNDS);

        folder = new File(event.getModConfigurationDirectory(), MODID);
        //noinspection ResultOfMethodCallIgnored
        folder.mkdir();

        configuration = new Configuration(new File(folder, event.getSuggestedConfigurationFile().getName()));
        updateConfig();

        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel()
            {
                return MODID;
            }

            @Override
            public String call() throws Exception
            {
                return "Debug: " + debug + " Silliness: " + silliness + " AprilFools: " + aprilFools + " PastPost:" + pastPost;
            }
        });
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException
    {
        Materials.load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        EndermanGriefing.init();
        pastPost = true;
        if (event.getSide().isClient()) LanguageHelper.run();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandSetLoginMessage());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) updateConfig();
    }

    private void updateConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.core.config.core").setCategoryComment(MODID, "d3.core.config.core");

        debug = configuration.getBoolean("isDebug", MODID, debug, "Enable isDebug mode", "d3.core.config.isDebug");
        silliness = configuration.getBoolean("silliness", MODID, silliness, "Enable silliness\nBut seriously, you can disable name changes, drops and block helmets with this setting.", "d3.core.config.silliness");
        EventHandler.I.norain = configuration.getBoolean("norain", MODID, EventHandler.I.norain, "No more rain if set to true.", "d3.core.config.norain");
        EventHandler.I.insomnia = configuration.getBoolean("insomnia", MODID, EventHandler.I.insomnia, "No more daytime when players sleep if set to true.", "d3.core.config.insomnia");
        EventHandler.I.lilypad = configuration.getBoolean("lilypad", MODID, EventHandler.I.lilypad, "Spawn the player on a lilypad when in or above water.", "d3.core.config.lilypad");
        EventHandler.I.achievementFireworks = configuration.getBoolean("achievementFireworks", MODID, EventHandler.I.achievementFireworks, "Achievement = Fireworks", "d3.core.config.achievementFireworks");
        EventHandler.I.nosleep = configuration.getBoolean("nosleep", MODID, EventHandler.I.nosleep, "No sleep at all", "d3.core.config.nosleep");
        EventHandler.I.printDeathCoords = configuration.getBoolean("printDeathCoords", MODID, EventHandler.I.printDeathCoords, "Print your death coordinates in chat (client side)", "d3.core.config.printDeathCoords");
        EventHandler.I.claysTortureMode = configuration.getBoolean("claysTortureMode", MODID, EventHandler.I.claysTortureMode, "Deletes all drops on death.", "d3.core.config.claystorturemode");
        aprilFools = configuration.getBoolean("aprilFools", MODID, aprilFools, "What would this do...");
        getDevPerks().update(silliness);

        final String catTooltips = MODID + ".tooltips";
        configuration.setCategoryLanguageKey(catTooltips, "d3.core.config.tooltips").addCustomCategoryComment(catTooltips, "d3.core.config.tooltips");

        EventHandler.I.enableStringID = configuration.getBoolean("enableStringID", catTooltips, true, "Example: minecraft:gold_ore", "d3.core.config.tooltips.enableStringID");
        EventHandler.I.enableUnlocalizedName = configuration.getBoolean("enableUnlocalizedName", catTooltips, true, "Example: tile.oreGold", "d3.core.config.tooltips.enableUnlocalizedName");
        EventHandler.I.enableOreDictionary = configuration.getBoolean("enableOreDictionary", catTooltips, true, "Example: oreGold", "d3.core.config.tooltips.enableOreDictionary");
        EventHandler.I.enableBurnTime = configuration.getBoolean("enableBurnTime", catTooltips, true, "Example: 300 ticks", "d3.core.config.tooltips.enableBurnTime");

        {
            final String catEnderGriefing = MODID + ".EndermanGriefing";
            configuration.setCategoryLanguageKey(catEnderGriefing, "d3.core.config.EndermanGriefing");

            EndermanGriefing.undo();

            EndermanGriefing.disable = configuration.getBoolean("disable", catEnderGriefing, false, "Disable Enderman griefing completely.", "d3.core.config.EndermanGriefing.disable");
            EndermanGriefing.dropCarrying = configuration.getBoolean("dropCarrying", catEnderGriefing, false, "Made Enderman drop there carrying block on death.", "d3.core.config.EndermanGriefing.dropCarrying");

            Property property = configuration.get(catEnderGriefing, "blacklist", new String[0], "List of blocks (minecraft:stone) that will never be allowed to be picked up.");
            property.setLanguageKey("d3.core.config.EndermanGriefing.blacklist");
            EndermanGriefing.blacklist = property.getStringList();

            property = configuration.get(catEnderGriefing, "addlist", new String[0], "List of blocks (minecraft:stone) that will be added to the list of blocks Enderman pick up.");
            property.setLanguageKey("d3.core.config.EndermanGriefing.addlist");
            EndermanGriefing.addList = property.getStringList();

            if (pastPost) EndermanGriefing.init();
        }

        VOID_REFUNDS.config(configuration);

        if (configuration.hasChanged()) configuration.save();
    }

    public static Logger getLogger()
    {
        return instance.logger;
    }

    public static boolean isDebug()
    {
        return instance.debug;
    }

    public static boolean isAprilFools()
    {
        //noinspection MagicConstant
        return instance.aprilFools && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static Configuration getConfig()
    {
        return instance.configuration;
    }

    private static DevPerks getDevPerks()
    {
        if (instance.devPerks == null) instance.devPerks = new DevPerks();
        return instance.devPerks;
    }

    public static File getFolder()
    {
        return instance.folder;
    }
}
