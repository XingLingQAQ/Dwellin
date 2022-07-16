package com.github.sachin.dwellin.utils;

import com.github.sachin.dwellin.Dwellin;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class TweakinCompat {


    public static final boolean isEnabled;

    static{

        isEnabled = Dwellin.getPlugin().getServer().getPluginManager().isPluginEnabled("Tweakin");
    }


    public static boolean isTweakinVillager(Entity entity){
        return isEnabled && entity.getPersistentDataContainer().has(com.github.sachin.tweakin.utils.TConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER);
    }


    public static boolean isTweakEnabled(String name){
        if(isEnabled){
            return com.github.sachin.tweakin.Tweakin.getPlugin().getTweakManager().getTweakFromName(name).registered;
        }
        return false;
    }
    
}
