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

import com.google.gson.JsonParseException;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Dries007
 */
@SuppressWarnings("NullableProblems")
public class CommandSetLoginMessage extends CommandBase
{
    @Override
    public String getName()
    {
        return "setloginmessage";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "/setloginmessage <raw json message> OR /setloginmessage <text> OR /setloginmessage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        File file = new File(D3Core.getFolder(), CoreConstants.LOGIN_MESSAGE_FILE);
        if (args.length == 0)
        {

            if (file.exists()) //noinspection ResultOfMethodCallIgnored
                file.delete();
            sender.sendMessage(new TextComponentTranslation("d3.core.cmd.setloginmessage.removed"));
        }
        else
        {
            String txt = buildString(args, 0);
            try
            {
                FileUtils.writeStringToFile(file, txt, Charset.defaultCharset());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new CommandException(e.getMessage());
            }
            sender.sendMessage(new TextComponentTranslation("d3.core.cmd.setloginmessage.success"));
            try
            {
                sender.sendMessage(ITextComponent.Serializer.jsonToComponent(txt));
            }
            catch (JsonParseException jsonparseexception)
            {
                sender.sendMessage(new TextComponentString(txt));
            }
        }
    }
}
