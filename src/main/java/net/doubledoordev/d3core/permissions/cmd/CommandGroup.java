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

import net.doubledoordev.d3core.permissions.Node;
import net.doubledoordev.d3core.permissions.PermConstants;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;

import static net.doubledoordev.d3core.permissions.PermConstants.PERMISSIONS_PREFIX;

/**
 * @author Dries007
 */
public class CommandGroup extends CommandPermissionBase
{
    @Override
    public String getCommandName()
    {
        return "d3group";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "commands.d3group.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0) throw new WrongUsageException(getCommandUsage(sender));
        switch (args[0].toLowerCase())
        {
            case "help":
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.new"));
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.remove"));
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.node.add"));
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.node.remove"));
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.parent.set"));
                sender.addChatMessage(new ChatComponentTranslation("commands.d3group.help.parent.clear"));
                break;
            default:
                throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public Node getBasePermission()
    {
        return new Node(PERMISSIONS_PREFIX.concat(".group"));
    }
}
