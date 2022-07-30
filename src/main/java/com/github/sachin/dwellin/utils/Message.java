package com.github.sachin.dwellin.utils;

import com.github.sachin.dwellin.Dwellin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Message {


    public final FileConfiguration CONFIG;
    private final Dwellin plugin = Dwellin.getPlugin();

    public Message(){
        CONFIG = plugin.getConfigFromFile("messages.yml");
    }


    public String getMessage(String key){
        return ChatColor.translateAlternateColorCodes('&', CONFIG.getString("prefix","[Dwellin]")+CONFIG.getString(key,key));
    }

    public String getMessageWithoutPrefix(String key){
        return ChatColor.translateAlternateColorCodes('&', CONFIG.getString(key,key));
    }
    
}
