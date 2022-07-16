package com.github.sachin.dwellin.commands;


import com.github.sachin.dwellin.BaseItem;
import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.modules.controlledbadomen.ControlledBadOmen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends SubCommand{

    public GiveCommand() {
        super("Gives a specificed item to player", "give", "dwellin.command.give", "/dwellin give [player-name] [item-name] [amount]");
        List<String> itemNames = new ArrayList<>();
        itemNames.add("ominous_banner");
        for(BaseModule module : plugin.getModuleManager().getModuleList()){
            if(module instanceof BaseItem){
                itemNames.add(module.getName());
            }
        }
        addCompletion(2, null);
        addCompletion(3, itemNames);
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length < 2) return;
        int amount = 1;
        if(args.length > 3){
            amount = Integer.valueOf(args[3]);
        }
        Player target = Bukkit.getPlayer(args[1]);
        String itemName = args[2];
        if(target != null){

            for(BaseModule module : plugin.getModuleManager().getModuleList()){
                if(module instanceof BaseItem && module.getName().equals(itemName)){
                    ItemStack item = ((BaseItem)module).getItem();
                    item.setAmount(amount);
                    target.getInventory().addItem(item);
                    break;
                }
            }
            if(itemName.equals("ominous_banner")){
                ItemStack item = ControlledBadOmen.getOmoniousBanner();
                item.setAmount(amount);
                target.getInventory().addItem(item);
            }
        }
    }
    
}
