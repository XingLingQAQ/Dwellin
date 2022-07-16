package com.github.sachin.dwellin.modules.items.helpwantedsign;

import com.github.sachin.dwellin.BaseItem;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.CustomBlockData;
import com.github.sachin.dwellin.utils.DConstants;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Module(name = "help-wanted-sign")
public class HelpWantedSignModule extends BaseItem implements Listener{
    

    @EventHandler
    public void onInteract(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if(isSimilar(e.getItemInHand())){
            e.setCancelled(true);
            if(e.getBlock().getType().toString().endsWith("WALL_SIGN")) return;
            new BukkitRunnable(){
                public void run() {
                    
                    Block block = e.getBlockPlaced();
                    block.setType(e.getItemInHand().getType());
                    if(Tag.SIGNS.isTagged(e.getItemInHand().getType())){
                        Sign sign = (Sign) block.getState();
                        for(int i=0;i<4;i++){
                            sign.setLine(i, ChatColor.translateAlternateColorCodes('&', plugin.getMessageManager().CONFIG.getStringList("help-wanted-sign-text").get(i)));
                        }
                        Rotatable rotatable = (Rotatable) sign.getBlockData();
                        BlockFace facing = getDirection(player.getEyeLocation().getYaw()).getOppositeFace();
                        rotatable.setRotation(facing);
                        sign.setBlockData(rotatable);
                        sign.update(true);
                        Entity armorstand = plugin.getNmsHandler().spawnHelpWantedArmorstand(block.getLocation().add(0.5,-0.6,0.5), getConfig(),getFacing(facing));
                        CustomBlockData data = new CustomBlockData(block.getLocation());
                        data.set(DConstants.HELP_WANTED_AS_KEY,PersistentDataType.STRING,armorstand.getUniqueId().toString());
                    }
                };
            }.runTaskLater(plugin, 1);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e){
       ItemStack result = e.getCurrentItem();
       if(e.getRecipe() != null && recipes.contains(((Keyed)e.getRecipe()).getKey()) && isSimilar(result)){
           Material plankType = null;
           CraftingInventory inv = e.getInventory();
           for(ItemStack item : inv.getMatrix()){
               if(item==null) continue;
               if(Tag.PLANKS.isTagged(item.getType())){
                   plankType = item.getType();
               }
           }
           if(plankType != null){
               e.getCurrentItem().setType(Material.valueOf(plankType.toString().replace("PLANKS", "SIGN")));
           }
       }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignEdit(SignChangeEvent e){
        CustomBlockData data = new CustomBlockData(e.getBlock().getLocation());
        if(data.has(DConstants.HELP_WANTED_AS_KEY, PersistentDataType.STRING)){
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onBreak(ItemSpawnEvent e){
        if(e.getEntity().getItemStack().getType().toString().endsWith("SIGN")){
            removeSign(e.getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if(player.getGameMode()==GameMode.CREATIVE && e.getBlock().getType().toString().endsWith("SIGN")){
            removeSign(e.getBlock().getLocation());
        }
    }
    
    public void removeSign(Location loc){
        CustomBlockData data = new CustomBlockData(loc);
        if(data.has(DConstants.HELP_WANTED_AS_KEY, PersistentDataType.STRING)){
            UUID uuid = UUID.fromString(data.get(DConstants.HELP_WANTED_AS_KEY, PersistentDataType.STRING));
            if(Bukkit.getEntity(uuid) != null){
                Bukkit.getEntity(uuid).remove();
                data.remove(DConstants.HELP_WANTED_AS_KEY);
            }
        }
    
    }

    public BlockFace getDirection(float yaw){
        double degrees = yaw < 0 ? (yaw % -360.0) + 360 : yaw % 360.0;
        if (degrees <= 22.5) return BlockFace.SOUTH;
        if (degrees <= 67.5) return BlockFace.SOUTH_WEST;
        if (degrees <= 112.5) return BlockFace.WEST;
        if (degrees <= 157.5) return BlockFace.NORTH_WEST;
        if (degrees <= 202.5) return BlockFace.NORTH;
        if (degrees <= 247.5) return BlockFace.NORTH_EAST;
        if (degrees <= 292.5) return BlockFace.EAST;
        if (degrees <= 337.5) return BlockFace.SOUTH_EAST;
        return BlockFace.SOUTH;
    }

    public float getFacing(BlockFace face){
        switch (face) {
            case SOUTH:
                return 0;
            case SOUTH_WEST:
                return 45;    
            case WEST:
                return 90;
            case NORTH_WEST:
                return 135;    
            case NORTH:
                return 180;
            case NORTH_EAST:
                return 225;    
            case EAST:
                return 270;
            case SOUTH_EAST:
                return 315;                
            default:
                return 0;
        }
    }
}
