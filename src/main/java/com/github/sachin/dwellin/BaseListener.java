package com.github.sachin.dwellin;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class BaseListener implements Listener {

    protected final Dwellin plugin = Dwellin.getPlugin();

    public void registerEvent(){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void unregisterEvent(){
        HandlerList.unregisterAll(this);
    }
}
