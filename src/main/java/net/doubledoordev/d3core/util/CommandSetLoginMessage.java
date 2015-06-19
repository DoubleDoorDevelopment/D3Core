package net.doubledoordev.d3core.util;

import net.doubledoordev.d3core.D3Core;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
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
        }
        else
        {
            try
            {
                FileUtils.writeStringToFile(new File(D3Core.getFolder(), "loginmessage.txt"), func_82360_a(sender, args, 0));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        sender.addChatMessage(new ChatComponentTranslation("d3.core.cmd.setloginmessage.success"));
    }
}
