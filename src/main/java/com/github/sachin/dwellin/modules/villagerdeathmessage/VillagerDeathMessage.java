package com.github.sachin.dwellin.modules.villagerdeathmessage;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.modules.villagernames.VillagerNames;
import com.github.sachin.dwellin.utils.PermManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@Module(name="villager-death-message")
public class VillagerDeathMessage extends BaseModule implements Listener{
    
    @EventHandler
    public void onVillagerDeath(EntityDeathEvent e){
        
        if(e.getEntity() instanceof Villager){
            Villager villager = (Villager) e.getEntity();
            Location loc = villager.getLocation();
            String world;
            switch (villager.getWorld().getEnvironment()) {
                case NETHER:
                    world = getMessageManager().getMessageWithoutPrefix("villager-death-message.world-nether");
                    break;
                case THE_END:
                    world = getMessageManager().getMessageWithoutPrefix("villager-death-message.world-end");
                default:
                    world = getMessageManager().getMessageWithoutPrefix("villager-death-message.world-overworld");
                    break;
            }
            String name = getMessageManager().getMessageWithoutPrefix("villager-death-message.default-name");
            VillagerNames module = (VillagerNames) getModuleManager().getModuleFromName("villager-names");
            if(module.registered ){
                name = module.getVillagerName(villager);
            }
            String message = getMessageManager().getMessageWithoutPrefix("villager-death-message.message")
            .replace("%x%", String.valueOf(loc.getBlockX()))
            .replace("%y%", String.valueOf(loc.getBlockY()))
            .replace("%z%", String.valueOf(loc.getBlockZ()))
            .replace("%world-type%", world)
            .replace("%villager%", name);
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!hasPermission(player, PermManager.VILDEATHMSG)) continue;

                player.sendMessage(message);
            }
        }
    }

}
