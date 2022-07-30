package com.github.sachin.dwellin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand{


    public HelpCommand() {
        super("help command", "help", "dwellin.command.help", "/dwellin help");
    }

    @Override
    public void execute(Player player, String[] args) {
        sendHelp(player);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sendHelp(sender);
    }

    public void sendHelp(CommandSender sender){
        List<SubCommand> list = plugin.getCommandManager().getSubCommands();
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n");
        buffer.append(ChatColor.translateAlternateColorCodes('&', "&7--------&eDwellin&7--------\n"));

        for (SubCommand subCommands : list) {
            buffer.append(ChatColor.translateAlternateColorCodes('&', subCommands.syntax)+"\n");
            buffer.append(ChatColor.AQUA+"["+subCommands.permission+"]\n");
            buffer.append(ChatColor.GRAY+""+ChatColor.ITALIC+""+subCommands.description+"\n");
            buffer.append(ChatColor.translateAlternateColorCodes('&', "  \n"));
        }
        buffer.append(ChatColor.GRAY+"----------------------\n");
        sender.sendMessage(buffer.toString());
    }
}
