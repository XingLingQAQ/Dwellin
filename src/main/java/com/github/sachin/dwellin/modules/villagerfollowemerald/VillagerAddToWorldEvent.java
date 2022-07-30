package com.github.sachin.dwellin.modules.villagerfollowemerald;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.github.sachin.dwellin.BaseListener;
import com.github.sachin.dwellin.utils.DConstants;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.dwellin.utils.TweakinCompat;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class VillagerAddToWorldEvent extends BaseListener {

    private final VillagerFollowEmerald instance;

    public VillagerAddToWorldEvent(VillagerFollowEmerald instance){
        this.instance = instance;
    }

    @EventHandler
    public void onVillagerJoinWorld(EntityAddToWorldEvent e){
        if(!(e.getEntity() instanceof Villager)) return;
        if(TweakinCompat.isTweakinVillager(e.getEntity())) return;
        Villager vil = (Villager) e.getEntity();
        if(!vil.getPersistentDataContainer().has(DConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER)){
            plugin.getNmsHandler().addFollowGoal(vil,instance.getTemptItems().toArray(new ItemStack[0]),instance.getSpeed(), PermManager.FOLLOWEMERALD,instance.checkPermissions(),false);
            vil.getPersistentDataContainer().set(DConstants.VILLAGER_FOLLOW_KEY,PersistentDataType.INTEGER,1);

        }
        else{
            plugin.getNmsHandler().addFollowGoal(vil,instance.getTemptItems().toArray(new ItemStack[0]),instance.getSpeed(), PermManager.FOLLOWEMERALD,instance.checkPermissions(),true);

        }
    }
}
