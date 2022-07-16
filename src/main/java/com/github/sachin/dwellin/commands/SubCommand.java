package com.github.sachin.dwellin.commands;

import com.github.sachin.dwellin.Dwellin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SubCommand {
    
    protected final Dwellin plugin = Dwellin.getPlugin();
    public final String description;
    public final String name;
    public final String permission;
    public final String syntax;
    private final Map<Integer,List<String>> completions = new HashMap<>();



    public SubCommand(String description, String name,String permission,String syntax) {
        this.description = description;
        this.name = name;
        this.permission = permission;
        this.syntax = syntax;
    }


    public void addCompletion(int index,List<String> completion){
        completions.put(index, completion);
    }

    public Map<Integer, List<String>> getCompletions() {
        return completions;
    }

    public void execute(Player player,String[] args){}

    public void execute(CommandSender sender,String[] args){}

}
