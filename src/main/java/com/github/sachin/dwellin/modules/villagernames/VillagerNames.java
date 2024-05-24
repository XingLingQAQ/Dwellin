package com.github.sachin.dwellin.modules.villagernames;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.DConstants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Module(name = "villager-names")
public class VillagerNames extends BaseModule implements Listener{
    
    private List<String> maleNames = new ArrayList<>();
    private List<String> femaleNames = new ArrayList<>();
    private OpenWindowPacket packetListener;
    private EntityJoinWorldListener listener;
    


    @Override
    public void onLoad() {
        if(plugin.isProtocolLibEnabled){
            this.packetListener = new OpenWindowPacket(this);
        }
        if(plugin.isRunningPaper){
            listener = new EntityJoinWorldListener(this);
        }
        
    }

    @Override
    public void register() {
        super.register();
        if(packetListener != null){
            packetListener.register();
        }
        if(listener != null) registerEvents(listener);
    }

    @Override
    public void unregister() {
        super.unregister();
        if(packetListener != null){
            packetListener.unregister();
        }
        if(listener != null) unregisterEvents(listener);
    }

    @Override
    public void reload() {
        super.reload();
        FileConfiguration config = plugin.getConfigFromFile("names.yml");
        maleNames = config.getStringList("male-names");
        femaleNames = config.getStringList("female-names");
    }

    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent e){
        Entity entity = e.getEntity();
        if(isBlackListWorld(entity.getWorld())) return;
        if(entity instanceof Villager){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!entity.isDead() && entity.getCustomName() == null){
                        nameVillager(entity);
                    }
                }
            }.runTaskLater(plugin,1);

        }
    }

    public void nameVillager(Entity entity){
        Random rand = new Random();
        double i = rand.nextDouble();
        if(i > 0.5 && !maleNames.isEmpty()){
            String name = maleNames.get(rand.nextInt(maleNames.size()));
            entity.setCustomName(name);
            entity.getPersistentDataContainer().set(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER,1);
        }
        else if(!femaleNames.isEmpty()){
            String name = femaleNames.get(rand.nextInt(femaleNames.size()));
            entity.setCustomName(name);
            entity.getPersistentDataContainer().set(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER,1);
        }
    }


    public String getVillagerName(Villager villager){
        if(villager.getPersistentDataContainer().has(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER)){
            String name = villager.getCustomName();
            
            String profession = villager.getProfession().toString();
            if(profession.equals("NONE") || profession.equals("NITWIT")){
                return name;
            }
            profession = profession.toString().substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
            return getMessageManager().getMessageWithoutPrefix("villager-trade-display")
            .replace("%name%", name)
            .replace("%profession%", profession);
        }
        return "Villager";
    }
}
