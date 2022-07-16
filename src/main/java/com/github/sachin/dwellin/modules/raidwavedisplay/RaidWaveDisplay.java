package com.github.sachin.dwellin.modules.raidwavedisplay;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.CustomBlockData;
import com.github.sachin.dwellin.utils.DConstants;
import com.github.sachin.dwellin.utils.PermManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.persistence.PersistentDataType;

// Permission: dwellin.wavedisplay
@Module(name = "raid-wave-display")
public class RaidWaveDisplay extends BaseModule implements Listener{
    

    @EventHandler
    public void onRaidWaveSpawn(RaidSpawnWaveEvent e){
        Raid raid = e.getRaid();
        Location center = raid.getLocation();
        World world = center.getWorld();
        if(isBlackListWorld(world)) return;
        CustomBlockData data = new CustomBlockData(center);
        int wavecount = 1;
        if(data.has(DConstants.RAID_WAVE_KEY, PersistentDataType.INTEGER)){
            wavecount = data.get(DConstants.RAID_WAVE_KEY, PersistentDataType.INTEGER)+1;
        }
        data.set(DConstants.RAID_WAVE_KEY, PersistentDataType.INTEGER, wavecount);
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!hasPermission(player, PermManager.WAVEDISPLAY)) continue;
            Raid nearestRaid = player.getWorld().locateNearestRaid(player.getLocation(), getConfig().getInt("raid-check-radius",20));
            if(nearestRaid == null) continue;
            Location loc = nearestRaid.getLocation();
            if(!loc.equals(center)) continue;
            int totalwaves = 0;
            switch (world.getDifficulty()) {
                case EASY:
                    totalwaves = 3;
                    break;
                case NORMAL:
                    totalwaves = 5;
                    break;    
                case HARD:
                    totalwaves = 7;
                    break; 
                default:
                    break;       
            }
            if(raid.getBadOmenLevel() >= 2){
                totalwaves++;
            }
            player.sendTitle("  ", getMessageManager().getMessageWithoutPrefix("wave-display").replace("%wave%", String.valueOf(wavecount)).replace("%totalwaves%", String.valueOf(totalwaves)), 20, 40, 20);
            
        }
    }


    @EventHandler
    public void onRaidStop(RaidStopEvent e){
        Raid raid = e.getRaid();
        removeRaidKey(raid);
    }


    @EventHandler
    public void onRaidFinish(RaidFinishEvent e){
        removeRaidKey(e.getRaid());
        
    }
    
    private void removeRaidKey(Raid raid){
        
        Location center = raid.getLocation();
        CustomBlockData data = new CustomBlockData(center);
        if(data.has(DConstants.RAID_WAVE_KEY, PersistentDataType.INTEGER)){
            data.remove(DConstants.RAID_WAVE_KEY);
        }
    }
    

}