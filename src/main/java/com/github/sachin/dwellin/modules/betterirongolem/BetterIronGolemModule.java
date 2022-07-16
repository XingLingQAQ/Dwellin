package com.github.sachin.dwellin.modules.betterirongolem;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.dwellin.utils.PaperUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

// Permission: dwellin.betterirongolem.use
@Module(name="better-iron-golem")
public class BetterIronGolemModule extends BaseModule implements Listener{


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIronGolemHeal(PlayerInteractEntityEvent e){
        if(e.isCancelled()) return;
        if(isBlackListWorld(e.getRightClicked().getWorld())) return;
        if((e.getRightClicked() instanceof IronGolem) && e.getHand()==EquipmentSlot.HAND){
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if(item != null && item.getType()==Material.IRON_INGOT){
                IronGolem golem = (IronGolem) e.getRightClicked();
                Player player = e.getPlayer();
                if(!golem.isPlayerCreated() && golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > golem.getHealth() && hasPermission(player, PermManager.BETTERIRONGOLEM)){
                    golem.setPlayerCreated(true);
                    if(plugin.isRunningPaper){
                        PaperUtils.removeHurtByTarget(golem);
                    }
                }
            }
        }
    }

    
}
