package com.github.sachin.dwellin.modules.villagernames;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.github.sachin.dwellin.utils.DConstants;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityJoinWorldListener implements Listener{

    private VillagerNames instance;

    public EntityJoinWorldListener(VillagerNames instance){
        this.instance = instance;
    }

    @EventHandler
    public void onEntityJoin(EntityAddToWorldEvent e){
        Entity entity = e.getEntity();
        if(!(entity instanceof Villager)) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!entity.isDead()){
                    if(!entity.getPersistentDataContainer().has(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER)){
                        if(entity.getCustomName() == null){
                            instance.nameVillager(entity);
                        }
                        else{
                            entity.getPersistentDataContainer().set(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER, 1);
                        }
                    }
                }

            }
        }.runTaskLater(instance.getPlugin(), 1);
    }
    
}
