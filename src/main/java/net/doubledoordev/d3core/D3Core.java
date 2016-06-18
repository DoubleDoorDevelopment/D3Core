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

package net.doubledoordev.d3core;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.doubledoordev.d3core.permissions.PermissionsDB;
import net.doubledoordev.d3core.util.*;
import net.doubledoordev.d3core.util.libs.org.mcstats.Metrics;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static net.doubledoordev.d3core.util.CoreConstants.*;
import static net.doubledoordev.d3core.util.FMLEventHandler.FML_EVENT_HANDLER;
import static net.doubledoordev.d3core.util.ForgeEventHandler.FORGE_EVENT_HANDLER;
import static net.doubledoordev.d3core.util.VoidRefunds.VOID_REFUNDS;

/**
 * @author Dries007
 */
@Mod(modid = MODID, name = NAME, canBeDeactivated = false, guiFactory = MOD_GUI_FACTORY)
public class D3Core implements ID3Mod
{
    @Mod.Instance(MODID)
    public static D3Core instance;
    public static boolean aprilFools = true;
    private File folder;

    @Mod.Metadata
    private ModMetadata metadata;

    private Logger        logger;
    private DevPerks      devPerks;
    private Configuration configuration;

    private boolean debug         = false;
    private boolean sillyness     = true;
    private boolean updateWarning = true;

    private List<ModContainer>             d3Mods         = new ArrayList<>();
    private List<CoreHelper.ModUpdateDate> updateDateList = new ArrayList<>();
    private boolean pastPost;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(FML_EVENT_HANDLER);
        FMLCommonHandler.instance().bus().register(VOID_REFUNDS);
        MinecraftForge.EVENT_BUS.register(FORGE_EVENT_HANDLER);
        MinecraftForge.EVENT_BUS.register(VOID_REFUNDS);

        folder = new File(event.getModConfigurationDirectory(), MODID);
        folder.mkdir();

        File configFile = new File(folder, event.getSuggestedConfigurationFile().getName());
        if (event.getSuggestedConfigurationFile().exists())
        {
            try
            {
                FileUtils.copyFile(event.getSuggestedConfigurationFile(), configFile);
                event.getSuggestedConfigurationFile().delete();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        configuration = new Configuration(configFile);
        syncConfig();

        PermissionsDB.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException
    {
        Materials.load();

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        for (final ModContainer modContainer : Loader.instance().getActiveModList())
        {
            if (modContainer instanceof FMLModContainer && modContainer.getMod() instanceof ID3Mod)
            {
                if (debug()) logger.info(String.format("[%s] Found a D3 Mod!", modContainer.getModId()));
                d3Mods.add(modContainer);
                if (!updateWarning) continue;

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            TreeSet<ArtifactVersion> availableVersions = new TreeSet<>();

                            String group = modContainer.getMod().getClass().getPackage().getName();
                            String artifactId = modContainer.getName();
                            if (debug()) logger.info(String.format("[%s] Group: %s ArtifactId: %s", modContainer.getModId(), group, artifactId));

                            URL url = new URL(MAVENURL + group.replace('.', '/') + '/' + artifactId + "/maven-metadata.xml");
                            if (debug()) logger.info(String.format("[%s] Maven URL: %s", modContainer.getModId(), url));

                            DocumentBuilder builder = dbf.newDocumentBuilder();
                            Document document = builder.parse(url.toURI().toString());
                            NodeList list = document.getDocumentElement().getElementsByTagName("version");
                            for (int i = 0; i < list.getLength(); i++)
                            {
                                String version = list.item(i).getFirstChild().getNodeValue();
                                if (version.startsWith(Loader.MC_VERSION + "-"))
                                {
                                    availableVersions.add(new DefaultArtifactVersion(version.replace(Loader.MC_VERSION + "-", "")));
                                }
                            }
                            DefaultArtifactVersion current = new DefaultArtifactVersion(modContainer.getVersion().replace(Loader.MC_VERSION + "-", ""));

                            if (debug()) logger.info(String.format("[%s] Current: %s Latest: %s All versions for MC %s: %s", modContainer.getModId(), current, availableVersions.last(), Loader.MC_VERSION, availableVersions));

                            if (current.compareTo(availableVersions.last()) < 0)
                            {
                                updateDateList.add(new CoreHelper.ModUpdateDate(modContainer.getName(), modContainer.getModId(), current.toString(), availableVersions.last().toString()));
                            }
                        }
                        catch (Exception e)
                        {
                            logger.info("D3 Mod " + modContainer.getModId() + " Version check FAILED. Error: " + e.toString());
                        }
                    }
                }).start();
            }
        }

        try
        {
            Metrics metrics = new Metrics(MODID, metadata.version);

            Metrics.Graph submods = metrics.createGraph("Submods");
            for (ModContainer modContainer : d3Mods)
            {
                submods.addPlotter(new Metrics.Plotter(modContainer.getModId()) {
                    @Override
                    public int getValue()
                    {
                        return 1;
                    }
                });
            }

            for (ModContainer modContainer : d3Mods)
            {
                metrics.createGraph(modContainer.getModId()).addPlotter(new Metrics.Plotter(modContainer.getDisplayVersion()) {
                    @Override
                    public int getValue()
                    {
                        return 1;
                    }
                });
            }

            metrics.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        for (CoreHelper.ModUpdateDate updateDate : updateDateList)
        {
            logger.warn(String.format("Update available for %s (%s)! Current version: %s New version: %s. Please update ASAP!", updateDate.getName(), updateDate.getModId(), updateDate.getCurrentVersion(), updateDate.getLatestVersion()));
        }

        EndermanGriefing.init();
        pastPost = true;

        PermissionsDB.save();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //event.registerServerCommand(new CommandGroup());
        event.registerServerCommand(new CommandSetLoginMessage());
    }

    @SubscribeEvent
    public void nameFormatEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!updateWarning || updateDateList.isEmpty()) return;

        event.player.addChatComponentMessage(IChatComponent.Serializer.func_150699_a("{\"text\":\"\",\"extra\":[{\"text\":\"Updates available for these mods:\",\"color\":\"gold\"}]}"));
        for (CoreHelper.ModUpdateDate updateDate : updateDateList)
        {
            event.player.addChatComponentMessage(IChatComponent.Serializer.func_150699_a(String.format("{\"text\":\"\",\"extra\":[{\"text\":\"%s: %s -> %s\"}]}", updateDate.getName(), updateDate.getCurrentVersion(), updateDate.getLatestVersion())));
        }
        event.player.addChatComponentMessage(IChatComponent.Serializer.func_150699_a("{\"text\":\"\",\"extra\":[{\"text\":\"Download here!\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://doubledoordev.net\"}},{\"text\":\" <- That is a link btw :p\"}]}"));
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
    {
        for (ModContainer modContainer : Loader.instance().getActiveModList())
        {
            if (modContainer.getMod() instanceof ID3Mod)
            {
                ((ID3Mod) modContainer.getMod()).syncConfig();
            }
        }
    }

    @Override
    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.core.config.core").setCategoryComment(MODID, LanguageRegistry.instance().getStringLocalization("d3.core.config.core"));

        debug = configuration.getBoolean("debug", MODID, debug, "Enable debug mode", "d3.core.config.debug");
        sillyness = configuration.getBoolean("sillyness", MODID, sillyness, "Enable sillyness\nBut seriously, you can disable name changes, drops and block helmets with this setting.", "d3.core.config.sillyness");
        updateWarning = configuration.getBoolean("updateWarning", MODID, updateWarning, "Allow update warnings on login", "d3.core.config.updateWarning");
        FML_EVENT_HANDLER.norain = configuration.getBoolean("norain", MODID, FML_EVENT_HANDLER.norain, "No more rain if set to true.", "d3.core.config.norain");
        FML_EVENT_HANDLER.insomnia = configuration.getBoolean("insomnia", MODID, FML_EVENT_HANDLER.insomnia, "No more daytime when players sleep if set to true.", "d3.core.config.insomnia");
        FML_EVENT_HANDLER.lilypad = configuration.getBoolean("lilypad", MODID, FML_EVENT_HANDLER.lilypad, "Spawn the player on a lilypad when in or above water.", "d3.core.config.lilypad");
        FORGE_EVENT_HANDLER.nosleep = configuration.getBoolean("nosleep", MODID, FORGE_EVENT_HANDLER.nosleep, "No sleep at all", "d3.core.config.nosleep");
        FORGE_EVENT_HANDLER.printDeathCoords = configuration.getBoolean("printDeathCoords", MODID, FORGE_EVENT_HANDLER.printDeathCoords, "Print your death coordinates in chat (client side)", "d3.core.config.printDeathCoords");
        FORGE_EVENT_HANDLER.claysTortureMode = configuration.getBoolean("claysTortureMode", MODID, FORGE_EVENT_HANDLER.claysTortureMode, "Deletes all drops on death.", "d3.core.config.claystorturemode");
        aprilFools = configuration.getBoolean("aprilFools", MODID, aprilFools, "What would this do...");
        getDevPerks().update(sillyness);

        final String catTooltips = MODID + ".tooltips";
        configuration.setCategoryLanguageKey(catTooltips, "d3.core.config.tooltips").addCustomCategoryComment(catTooltips, LanguageRegistry.instance().getStringLocalization("d3.core.config.tooltips"));

        FORGE_EVENT_HANDLER.enableStringID = configuration.getBoolean("enableStringID", catTooltips, true, "Example: minecraft:gold_ore", "d3.core.config.tooltips.enableStringID");
        FORGE_EVENT_HANDLER.enableUnlocalizedName = configuration.getBoolean("enableUnlocalizedName", catTooltips, true, "Example: tile.oreGold", "d3.core.config.tooltips.enableUnlocalizedName");
        FORGE_EVENT_HANDLER.enableOreDictionary = configuration.getBoolean("enableOreDictionary", catTooltips, true, "Example: oreGold", "d3.core.config.tooltips.enableOreDictionary");
        FORGE_EVENT_HANDLER.enableBurnTime = configuration.getBoolean("enableBurnTime", catTooltips, true, "Example: 300 ticks", "d3.core.config.tooltips.enableBurnTime");

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

    @Override
    public void addConfigElements(List<IConfigElement> list)
    {
        list.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }

    public static Logger getLogger()
    {
        return instance.logger;
    }

    public static boolean debug()
    {
        return instance.debug;
    }

    public static Configuration getConfiguration()
    {
        return instance.configuration;
    }

    public static DevPerks getDevPerks()
    {
        if (instance.devPerks == null) instance.devPerks = new DevPerks();
        return instance.devPerks;
    }

    public static File getFolder()
    {
        return instance.folder;
    }
}
