package com.github.sachin.dwellin.utils;

import com.github.sachin.dwellin.Dwellin;
import com.github.sachin.prilib.utils.AbstractPermManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

//        dwellin.betterirongolem:
//        dwellin.villagerfollowemerald:
//        dwellin.wavedisplay:
//        dwellin.villagerdeathmessage:
//        dwellin.controlledbadomen:
//        dwellin.emeraldshard:
public class PermManager extends AbstractPermManager {

    public static final Permission BETTERIRONGOLEM  = get("dwellin.betterirongolem");
    public static final Permission FOLLOWEMERALD = get("dwellin.villagerfollowemerald");
    public static final Permission WAVEDISPLAY = get("dwellin.wavedisplay");
    public static final Permission VILDEATHMSG = get("dwellin.villagerdeathmessage");
    public static final Permission CONTROLLEDBADOMEN = get("dwellin.controlledbadomen");
    public static final Permission EMERALD_SHARD = get("dwellin.emeraldshard");

}
