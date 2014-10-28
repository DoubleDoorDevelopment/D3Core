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

package net.doubledoordev.d3core.permissions;

import com.mojang.authlib.GameProfile;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import sun.security.krb5.internal.crypto.Des3;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static net.doubledoordev.d3core.util.CoreConstants.GSON;

/**
 * Permission system stuff
 *
 * @author Dries007
 */
public class PermissionsDB
{
    public static final PermissionsDB   INSTANCE = new PermissionsDB();

    private HashMap<UUID, Player> playerDB = new HashMap<>();
    private HashMap<String, Group>  groupDB  = new HashMap<>();

    private PermissionsDB()
    {

    }

    @SuppressWarnings("unchecked")
    public static void load()
    {
        try
        {
            INSTANCE.playerDB.clear();
            if (getPlayersFile().exists()) INSTANCE.playerDB.putAll(GSON.fromJson(FileUtils.readFileToString(getPlayersFile()), HashMap.class));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            INSTANCE.groupDB.clear();
            if (getGroupsFile().exists()) INSTANCE.groupDB.putAll(GSON.fromJson(FileUtils.readFileToString(getGroupsFile()), HashMap.class));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void save()
    {
        try
        {
            FileUtils.writeStringToFile(getPlayersFile(), GSON.toJson(INSTANCE.playerDB));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            FileUtils.writeStringToFile(getGroupsFile(), GSON.toJson(INSTANCE.groupDB));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static File getGroupsFile()
    {
        return new File(D3Core.getFolder(), "Groups.json");
    }

    public static File getPlayersFile()
    {
        return new File(D3Core.getFolder(), "Players.json");
    }

    public Group getGroup(String parent)
    {
        parent = parent.toLowerCase();
        if (groupDB.containsKey(parent)) groupDB.put(parent, new Group(parent));
        return groupDB.get(parent);
    }

    public Player getPlayer(UUID uuid)
    {
        if (!playerDB.containsKey(uuid)) playerDB.put(uuid, new Player(uuid));
        return playerDB.get(uuid);
    }

    public Collection<Group> getGroups()
    {
        return groupDB.values();
    }

    public Collection<Player> getPlayers()
    {
        return playerDB.values();
    }

    public boolean checkPermissions(ICommandSender sender, Node node)
    {
        if (sender.getCommandSenderName().equals(MinecraftServer.getServer().getServerOwner())) return true;
        else if (sender == MinecraftServer.getServer()) return true;
        else if (sender == RConConsoleSource.instance) return true;
        else if (sender instanceof EntityPlayer) return checkPermissions(((EntityPlayer) sender).getGameProfile().getId(), node);

        D3Core.getLogger().warn("checkPermissions: " + sender.getCommandSenderName()); // TODO: when does this happends.
        return false;
    }

    public boolean checkPermissions(UUID uuid, Node node)
    {
        Player player = getPlayer(uuid);
        for (Node hadNode : player.getNodes()) if (hadNode.matches(node)) return true;
        for (String groupName : player.getGroups()) if (getGroup(groupName).hasPermissionFor(node)) return true;
        return false;
    }
}
