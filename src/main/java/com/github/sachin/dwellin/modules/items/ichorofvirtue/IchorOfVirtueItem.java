package com.github.sachin.dwellin.modules.items.ichorofvirtue;

import com.github.sachin.dwellin.BaseItem;
import com.github.sachin.dwellin.manager.Module;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

@Module(name = "ichor-of-virtue")
public class IchorOfVirtueItem extends BaseItem implements Listener{



    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getHand());
        if(isSimilar(item) && e.getRightClicked() instanceof Villager){
            
        }
    }
    
}
