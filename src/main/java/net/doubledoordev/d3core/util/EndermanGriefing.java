package net.doubledoordev.d3core.util;

import cpw.mods.fml.common.registry.GameData;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;

import java.util.HashMap;

/**
 * @author Dries007
 */
public class EndermanGriefing
{
    public static boolean disable;
    public static String[] blacklist;
    public static String[] addList;

    private static HashMap<String, Boolean> reverseMap = new HashMap<>();

    public static void init()
    {
        if (disable)
        {
            for (Object key : GameData.getBlockRegistry().getKeys())
            {
                Block block = (Block) GameData.getBlockRegistry().getObject(key);
                reverseMap.put(GameData.getBlockRegistry().getNameForObject(block), EntityEnderman.getCarriable(block));
                EntityEnderman.setCarriable(block, false);
            }
        }
        else
        {
            for (String item : addList)
            {
                if (!GameData.getBlockRegistry().containsKey(item))
                {
                    D3Core.getLogger().warn("Block %s does not exist...", item);
                    continue;
                }
                reverseMap.put(item, EntityEnderman.getCarriable(GameData.getBlockRegistry().getObject(item)));
                EntityEnderman.setCarriable(GameData.getBlockRegistry().getObject(item), true);
            }
            for (String item : blacklist)
            {
                if (!GameData.getBlockRegistry().containsKey(item))
                {
                    D3Core.getLogger().warn("Block %s does not exist...", item);
                    continue;
                }
                reverseMap.put(item, EntityEnderman.getCarriable(GameData.getBlockRegistry().getObject(item)));
                EntityEnderman.setCarriable(GameData.getBlockRegistry().getObject(item), false);
            }
        }
    }

    public static void undo()
    {
        for (String entry : reverseMap.keySet())
        {
            EntityEnderman.setCarriable(GameData.getBlockRegistry().getObject(entry), reverseMap.get(entry));
        }
    }
}
