package com.github.sachin.dwellin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand{

    public ReloadCommand() {
        super("reloads the config files", "reload", "dwellin.command.reload", "/dwellin reload");
        
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reload(sender);
    }

    @Override
    public void execute(Player player, String[] args) {
        plugin.reload(player);
    }
    
}
