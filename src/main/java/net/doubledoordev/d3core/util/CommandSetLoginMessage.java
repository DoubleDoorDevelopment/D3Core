package net.doubledoordev.d3core.util;

import com.google.gson.JsonParseException;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
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
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            File file = new File(D3Core.getFolder(), "loginmessage.txt");
            if (file.exists()) file.delete();
            sender.addChatMessage(new ChatComponentTranslation("d3.core.cmd.setloginmessage.removed"));
        }
        else
        {
            String txt = func_82360_a(sender, args, 0);
            try
            {
                FileUtils.writeStringToFile(new File(D3Core.getFolder(), "loginmessage.txt"), txt);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            sender.addChatMessage(new ChatComponentTranslation("d3.core.cmd.setloginmessage.success"));
            try
            {
                sender.addChatMessage(IChatComponent.Serializer.func_150699_a(txt));
            }
            catch (JsonParseException jsonparseexception)
            {
                sender.addChatMessage(new ChatComponentText(txt));
            }
        }
    }
}
