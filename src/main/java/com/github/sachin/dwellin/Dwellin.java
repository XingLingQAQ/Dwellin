package com.github.sachin.dwellin;

import com.github.sachin.dwellin.commands.CommandManager;
import com.github.sachin.dwellin.manager.ModuleManager;
import com.github.sachin.dwellin.utils.ConfigUpdater;
import com.github.sachin.dwellin.utils.Items;
import com.github.sachin.dwellin.utils.Message;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.prilib.Prilib;
import com.github.sachin.prilib.nms.AbstractNMSHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Dwellin extends JavaPlugin {

    private static Dwellin plugin;
    public boolean isRunningPaper;
    public boolean isProtocolLibEnabled;

    public boolean isEnabled;
    private Items items;
    private ModuleManager moduleManager;
    private ConfigurationSection recipeFile;
    private Message messageManager;


    private Prilib prilib;
    private AbstractNMSHandler nmsHandler;
    private String mcVersion;

    private List<Permission> perms;


    @Override
    public void onEnable() {
        isEnabled = true;
        plugin = this;

        if(getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            isProtocolLibEnabled = true;
            getLogger().info("Running ProtocolLib..");
        }
        else isProtocolLibEnabled = false;
        try {
            Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger");
            this.isRunningPaper = true;
            getLogger().info("Running papermc..");
        } catch (ClassNotFoundException e) {
            this.isRunningPaper = false;
        }
        this.mcVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        this.prilib = new Prilib(this);
        this.prilib.initialize();
        if(!prilib.isNMSEnabled()){
            getLogger().warning("Running incompataible version, stopping dwellin");
            this.isEnabled = false;
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.saveDefaultConfig();
        this.reloadConfig();
        PermManager.reload();
        this.perms = Arrays.asList(PermManager.BETTERIRONGOLEM,PermManager.CONTROLLEDBADOMEN,PermManager.EMERALD_SHARD,PermManager.FOLLOWEMERALD,PermManager.VILDEATHMSG,PermManager.WAVEDISPLAY);
        this.messageManager = new Message();
        this.items = new Items(this);
        this.nmsHandler = prilib.getNmsHandler();
        File file = new File(getDataFolder(), "names.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateRecipesFile();
        // FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        // List<String> maleNames = new ArrayList<>();
        // List<String> femaleNames = new ArrayList<>(); 
        // for(String name : generator.generateNames(500, Gender.MALE).stream().map(n -> n.getFirstName()).collect(Collectors.toSet())){
        //     maleNames.add(name);
        // }
        // for(String name : generator.generateNames(500, Gender.FEMALE).stream().map(n -> n.getFirstName()).collect(Collectors.toSet())){
        //     femaleNames.add(name);
        // }
        // config.set("male-names", maleNames);
        // config.set("female-names", femaleNames);
        // try {
        //     config.save(file);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        this.moduleManager = new ModuleManager(plugin);
        moduleManager.load();
        CommandManager commandManager = new CommandManager(plugin); 
        getCommand("dwellin").setExecutor(commandManager);
        getCommand("dwellin").setTabCompleter(commandManager);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(isEnabled){
            for(BaseModule module : getModuleManager().getModuleList()){
                if(module.registered){
                    module.onDisable();
                }
            }
        }
    }


    public void reload(){
        this.items = new Items(this);
        updateRecipesFile();
        this.moduleManager.reload();
        this.messageManager = new Message();


    }

    public void updateRecipesFile(){
        File file = new File(getDataFolder(),"recipes.yml");
        if(recipeFile == null){
            if(!file.exists()){
                saveResource("recipes.yml", true);
            }
            recipeFile = YamlConfiguration.loadConfiguration(file);
        }
        ConfigUpdater.updateWithoutComments(plugin, "recipes.yml", file);
    }

    public FileConfiguration getConfigFromFile(String fileName){
        File file = new File(getDataFolder(),fileName);
        if(!file.exists()){
            saveResource(fileName, true);
        }
        else{
            updateFile(fileName, file);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void updateFile(String fileName,File toUpdate){
        try {
            ConfigUpdater.update(plugin, fileName, toUpdate, new ArrayList<>(),true);
        } catch (IOException e) {
            getLogger().warning("Error occured while updating "+fileName+"...");
            e.printStackTrace();
        }
    }


    public static Dwellin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getKey(String key){
        return new NamespacedKey(plugin, key);
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public AbstractNMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public Prilib getPrilib() {
        return prilib;
    }

    public Message getMessageManager() {
        return messageManager;
    }

    public Items getItemFolder() {
        return items;
    }

    public ConfigurationSection getRecipeFile() {
        return recipeFile;
    }

}
