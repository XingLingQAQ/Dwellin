package com.github.sachin.dwellin.modules.villagerfollowemerald;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.DConstants;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.dwellin.utils.TweakinCompat;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Module(name = "villager-follow-emerald")
public class VillagerFollowEmerald extends BaseModule{
    
    private List<ItemStack> temptItems;
    private double speed;

    private VillagerAddToWorldEvent villagerAddToWorldEvent;

    public VillagerFollowEmerald(){
        super();
        this.villagerAddToWorldEvent = new VillagerAddToWorldEvent(this);
    }

    @Override
    public void register() {
        super.register();
        villagerAddToWorldEvent.registerEvent();
    }

    @Override
    public void unregister() {
        super.unregister();
        villagerAddToWorldEvent.unregisterEvent();
    }

    @Override
    public void reload() {
        super.reload();
        this.speed = getConfig().getDouble("speed",0.6);
        List<Material> temtMaterials = getEnumList("temptation-materials", Material.class);

        if(temptItems == null) temptItems = Lists.newArrayList();
        if(!temptItems.isEmpty()) temptItems.clear();
        for(Material mat : temtMaterials){
            temptItems.add(new ItemStack(mat));
        }
    }


//    @EventHandler
//    public void onVillagerSpawn(EntitySpawnEvent e){
//        Entity entity = e.getEntity();
//        if(isBlackListWorld(entity.getWorld())) return;
//        if(entity instanceof Villager && !entity.getPersistentDataContainer().has(DConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER) && !temptItems.isEmpty() && !TweakinCompat.isTweakEnabled("villager-follow-emerald")){
//            entity.getPersistentDataContainer().set(DConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER, 1);
//            plugin.getNmsHandler().addFollowGoal((Villager)entity,temptItems.toArray(new ItemStack[0]),speed, PermManager.FOLLOWEMERALD,checkPermissions(),false);
//        }
//    }

    public List<ItemStack> getTemptItems() {
        return temptItems;
    }

    public double getSpeed() {
        return speed;
    }

    //    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent e){
//        if(isBlackListWorld(e.getWorld()) || temptItems.isEmpty()) return;
//        new BukkitRunnable(){
//            public void run() {
//                if(e.getChunk().isLoaded()){
//                    for(Entity en : e.getChunk().getEntities()){
//                        if(en instanceof Villager && !en.getPersistentDataContainer().has(DConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER) && !TweakinCompat.isTweakinVillager(en)){
//                            en.getPersistentDataContainer().set(DConstants.VILLAGER_FOLLOW_KEY, PersistentDataType.INTEGER, 1);
//                            plugin.getNmsHandler().addFollowGoal((Villager)en,temptItems.toArray(new ItemStack[0]),getConfig().getDouble("speed",0.6), PermManager.FOLLOWEMERALD);
//                        }
//                    }
//                }
//            };
//        }.runTaskLater(plugin, 7);
//    }


}
