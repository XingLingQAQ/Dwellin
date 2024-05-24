package com.github.sachin.dwellin.manager;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.Dwellin;
import com.github.sachin.dwellin.modules.betterirongolem.BetterIronGolemModule;
import com.github.sachin.dwellin.modules.betterillagerai.BetterIillagerAI;
import com.github.sachin.dwellin.modules.controlledbadomen.ControlledBadOmen;
import com.github.sachin.dwellin.modules.items.easyvillagerpickup.EasyVillagerPickup;
import com.github.sachin.dwellin.modules.items.emeraldshard.EmeraldShardItem;
import com.github.sachin.dwellin.modules.raidwavedisplay.RaidWaveDisplay;
import com.github.sachin.dwellin.modules.villagerdeathmessage.VillagerDeathMessage;
import com.github.sachin.dwellin.modules.villagerfollowemerald.VillagerFollowEmerald;
import com.github.sachin.dwellin.modules.villagernames.VillagerNames;
import com.github.sachin.dwellin.utils.ConfigUpdater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private Dwellin plugin;

    private List<BaseModule> moduleList = new ArrayList<>();

    public int registeredModules;


    public ModuleManager(Dwellin plugin){
        this.plugin = plugin;
    }

    public void load(){
        reload(false);
    }


    public void reload(){
        reload(true);
    }

    private void reload(boolean unregister){
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(),"config.yml");
        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, new ArrayList<>(),unregister);
        } catch (IOException e) {
            plugin.getLogger().warning("Error occured while updating config.yml");
            e.printStackTrace();
        }
        plugin.reloadConfig();
        int registered = 0;
        for(BaseModule module : getModuleList()){
            try {
                module.reload();
                if(unregister){
    
                    if(module.registered){
                        module.unregister();
                    }
                }
                if(module.shouldEnable()){
                    module.register();
                    
                    registered++;
                }
            } catch (Exception e) {
                plugin.getLogger().info("Error occured while registering "+module.getName()+" tweak..");
                plugin.getLogger().info("Report this error on discord or at spigot page in discussion section.");
                e.printStackTrace();
                
            }
        }
        this.registeredModules = registered;
        plugin.getLogger().info("Registered "+registeredModules+" modules successfully");
        if(unregister){
            plugin.getLogger().info("Dwellin reloaded successfully");
        }
        else{
            plugin.getLogger().info("Dwellin loaded successfully");
        }
    }



    public List<BaseModule> getModuleList() {
        if(moduleList.isEmpty()){
            moduleList.add(new BetterIronGolemModule());
            moduleList.add(new VillagerNames());
            moduleList.add(new RaidWaveDisplay());
            moduleList.add(new VillagerDeathMessage());
            moduleList.add(new BetterIillagerAI());
            moduleList.add(new VillagerFollowEmerald());
            moduleList.add(new ControlledBadOmen());
            moduleList.add(new EmeraldShardItem());
            moduleList.add(new EasyVillagerPickup());
//            moduleList.add(new HelpWantedSignModule());
            // moduleList.add(new RideableRavager());
        }
        return moduleList;
    }


    public BaseModule getModuleFromName(String name){
        for(BaseModule m : getModuleList()){
            if(m.getName().equalsIgnoreCase(name)){
                return m;
            }
        }
        return null;
    }


    public boolean isModuleEnabled(String name){
        BaseModule module = getModuleFromName(name);
        if(module != null){
            return module.registered;
        }
        return false;
    }
    
}
