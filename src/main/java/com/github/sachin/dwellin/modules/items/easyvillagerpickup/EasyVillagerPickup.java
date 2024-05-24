package com.github.sachin.dwellin.modules.items.easyvillagerpickup;

import com.github.sachin.dwellin.BaseItem;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.prilib.utils.FastItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Module(name = "easy-villager-pickup")
public class EasyVillagerPickup extends BaseItem implements Listener {

    private final Pattern PROF_PATTERN = Pattern.compile("(%villager_profession/(\\w+)%)");


    @EventHandler
    public void onEggDispense(BlockDispenseEvent e){
        if(isSimilar(e.getItem()) && e.getBlock().getType()==Material.DISPENSER){
            e.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVillagerPickup(PlayerInteractEntityEvent e){
        if(e.isCancelled()) return;
        Player player = e.getPlayer();


        if(!(e.getRightClicked() instanceof Villager)) return;
        if(isBlackListWorld(player.getWorld())) return;
        if(!hasPermission(player, PermManager.VILLAGER_PICKUP)) return;
        Villager villager = (Villager) e.getRightClicked();
        if(player.getCooldown(getItem().getType()) != 0) return;
        ItemStack iteminHand = player.getEquipment().getItem(e.getHand());
        if(isSimilar(iteminHand)){e.setCancelled(true);}
        if(!player.isSneaking() || iteminHand.getType() != Material.AIR) return;
        ItemStack egg = plugin.getPrilib().getNmsHandler().setVillagerEgg(getItem(),villager);

        FastItemStack fastItem = new FastItemStack(egg);
        Matcher displayMatcher = PROF_PATTERN.matcher(fastItem.getDisplay());
        String profession = villager.getProfession().toString();
        profession = profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
        List<String> newLore = new ArrayList<>();
        if(displayMatcher.find()){
            fastItem.replaceInDisplay(displayMatcher.group(1), villager.getProfession()!= Villager.Profession.NONE ? profession : displayMatcher.group(2));
        }
        for(String s : fastItem.getLore()){
            Matcher loreMatcher = PROF_PATTERN.matcher(s);
            if(loreMatcher.find()){
                newLore.add(s.replace(loreMatcher.group(1), villager.getProfession()!= Villager.Profession.NONE ? profession : loreMatcher.group(2)));
            }else{
                newLore.add(s);
            }
        }
        fastItem.setLore(newLore);
        fastItem.replaceInDisplay("%villager_name%",villager.getCustomName() == null ? "" : villager.getCustomName());
        fastItem.replaceInLore("%villager_name%",villager.getCustomName() == null ? "" : villager.getCustomName());

        player.getInventory().addItem(fastItem.get());
        e.getRightClicked().remove();
        e.setCancelled(true);


    }

    @EventHandler
    public void onItemPlace(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(isSimilar(e.getItem()) && e.getAction()==Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);

            if(isBlackListWorld(player.getWorld())) return;
            if(!player.isSneaking()) return;
            if(player.getCooldown(getItem().getType()) != 0) return;
            if(!hasPermission(player,PermManager.VILLAGER_PLACE)) return;
            Block relativeBlock = e.getClickedBlock().getRelative(e.getBlockFace());
            if(relativeBlock.isEmpty()){
                player.getInventory().setItem(e.getHand(),null);
                player.setCooldown(getItem().getType(),20);
                plugin.getPrilib().getNmsHandler().getVillager(e.getItem(),player.getWorld(),relativeBlock.getLocation().add(0.5,0,0.5));

            }
        }
    }
}
