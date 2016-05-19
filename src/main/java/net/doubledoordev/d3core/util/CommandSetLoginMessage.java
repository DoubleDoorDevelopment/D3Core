package net.doubledoordev.d3core.util;

import com.google.gson.JsonParseException;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Dries007
 */
public class CommandSetLoginMessage extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "setloginmessage";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/setloginmessage <raw json message> OR /setloginmessage <text> OR /setloginmessage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            File file = new File(D3Core.getFolder(), "loginmessage.txt");
            if (file.exists()) file.delete();
            sender.addChatMessage(new TextComponentTranslation("d3.core.cmd.setloginmessage.removed"));
        }
        else
        {
            String txt = buildString(args, 0);
            try
            {
                FileUtils.writeStringToFile(new File(D3Core.getFolder(), "loginmessage.txt"), txt);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            sender.addChatMessage(new TextComponentTranslation("d3.core.cmd.setloginmessage.success"));
            try
            {
                sender.addChatMessage(ITextComponent.Serializer.jsonToComponent(txt));
            }
            catch (JsonParseException jsonparseexception)
            {
                sender.addChatMessage(new TextComponentString(txt));
            }
        }
    }
}
