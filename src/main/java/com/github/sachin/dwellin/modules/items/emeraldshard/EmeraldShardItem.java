package com.github.sachin.dwellin.modules.items.emeraldshard;

import com.github.sachin.dwellin.BaseItem;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.PermManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

// Permission: dwellin.emeraldshard.use
@Module(name = "emerald-shard")
public class EmeraldShardItem extends BaseItem implements Listener{



    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getHand());
        if(isSimilar(item) && e.getRightClicked() instanceof Villager && player.isSneaking() && hasPermission(player, PermManager.EMERALD_SHARD)){
            Villager vil = (Villager) e.getRightClicked();
            Location jobSite = vil.getMemory(MemoryKey.JOB_SITE);
            if(getConfig().getBoolean("require-job-site") && jobSite == null) return;
            if(player.getGameMode()==GameMode.SURVIVAL){
                item.setAmount(item.getAmount()-1);
            }
            plugin.getNmsHandler().restock(vil);
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, vil.getLocation().add(0,1.5,0), 16,0.4,0.4,0.4);
            e.setCancelled(true);
        }
    }
    
}
