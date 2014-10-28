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

package net.doubledoordev.d3core.permissions.cmd;

import net.doubledoordev.d3core.permissions.PermissionsDB;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @author Dries007
 */
public class CommandPermissionWrapper extends CommandPermissionBase
{
    CommandBase  commandBase;

    public CommandPermissionWrapper(CommandBase commandBase)
    {
        this.commandBase = commandBase;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return commandBase.getRequiredPermissionLevel();
    }

    @Override
    public String getCommandName()
    {
        return commandBase.getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return commandBase.getCommandUsage(p_71518_1_);
    }

    @Override
    public List getCommandAliases()
    {
        return commandBase.getCommandAliases();
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
    {
        commandBase.processCommand(p_71515_1_, p_71515_2_);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer) return PermissionsDB.INSTANCE.checkPermissions(sender, getBasePermission());
        else return commandBase.canCommandSenderUseCommand(sender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_)
    {
        return commandBase.addTabCompletionOptions(p_71516_1_, p_71516_2_);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_)
    {
        return commandBase.isUsernameIndex(p_82358_1_, p_82358_2_);
    }

    @Override
    public int compareTo(ICommand p_compareTo_1_)
    {
        return commandBase.compareTo(p_compareTo_1_);
    }

    @Override
    public int compareTo(Object p_compareTo_1_)
    {
        return commandBase.compareTo(p_compareTo_1_);
    }
}
