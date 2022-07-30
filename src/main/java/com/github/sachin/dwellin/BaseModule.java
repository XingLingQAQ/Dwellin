package com.github.sachin.dwellin;

import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.manager.ModuleManager;
import com.github.sachin.dwellin.utils.Message;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseModule {

    protected Dwellin plugin = Dwellin.getPlugin();
    protected final Logger LOGGER = Dwellin.getPlugin().getLogger();
    protected String name;
    protected ConfigurationSection config;
    public boolean registered=false;
    private boolean shouldEnable=false;

    public BaseModule(Dwellin plugin,String name){
        this.name = name;
        this.plugin = plugin;
        this.onLoad();
        this.reload();
    }

    public BaseModule(){
        this.name = this.getClass().getAnnotation(Module.class).name();
        this.onLoad();
        this.reload();
    }


    public void reload(){
        this.config = plugin.getConfig().getConfigurationSection(name);
        this.shouldEnable = config.getBoolean("enabled");
    }


    public List<String> getBlackListWorlds(){
        if(config.contains("black-list-worlds")){
            List<String> list = config.getStringList("black-list-worlds");
            if(list != null){
                return list;
            }
        }
        return new ArrayList<>();
    }

    public boolean hasPermission(Player player, Permission perm){
        if(checkPermissions()){
            return player.hasPermission(perm);
        }
        return true;
    }

    public boolean checkPermissions(){
        return plugin.getConfig().getBoolean("check-permissions",true);
    }

    public boolean isBlackListWorld(World world){
        return getBlackListWorlds().contains(world.getName());
    }

    public Message getMessageManager() {return plugin.getMessageManager();}

    public ModuleManager getModuleManager() {return plugin.getModuleManager();}

    public boolean shouldEnable() {return shouldEnable;}

    public BaseModule getInstance(){return this;}


    public <T extends Enum<T>> List<T> getEnumList(String key,Class<T> enumClass){
        List<T> list = new ArrayList<>();
        for(String str : getConfig().getStringList(key)){
            Optional<T> optional = Enums.getIfPresent(enumClass, str);
            if(optional.isPresent()){
                list.add(optional.get());
            }
        }
        return list;
    }


    protected void registerEvents(Listener listener) {
        this.plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void register(){
        if(this instanceof Listener){
            registerEvents((Listener)this);
        }
        registered = true;
    }

    public String getName() {
        return name;
    }
    public ConfigurationSection getConfig() {
        return config;
    }
    public Dwellin getPlugin() {
        return plugin;
    }


    public void unregister(){
        if(this instanceof Listener){
            unregisterEvents((Listener)this);
        }
        registered = false;
    }


    public void onDisable(){}
    public void onLoad(){}
    
    
}

