package com.github.sachin.dwellin.utils;

import com.github.sachin.dwellin.Dwellin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Items {

    public final FileConfiguration CONFIG;

    public Items(Dwellin plugin){
        File file = new File(plugin.getDataFolder(),"items.yml");
        if(!file.exists()){
            plugin.saveResource("items.yml", false);
        }
        ConfigUpdater.updateWithoutComments(plugin, "items.yml", file);
        this.CONFIG = YamlConfiguration.loadConfiguration(file);
    }
    
}
