package com.github.sachin.dwellin.modules.villagernames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.ComponentParser;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.github.sachin.dwellin.Dwellin;
import com.github.sachin.dwellin.utils.DConstants;
import com.google.gson.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;

import java.util.UUID;

public class OpenWindowPacket extends PacketAdapter{

    private final Gson GSON = new Gson();
    private ProtocolManager manager;
    private VillagerNames instance;

    public OpenWindowPacket(VillagerNames instance) {
        super(Dwellin.getPlugin(),PacketType.Play.Server.OPEN_WINDOW);
        this.manager = ProtocolLibrary.getProtocolManager();
        this.instance = instance;
        
    }


    public void register(){
        manager.addPacketListener(this);
    }

    public void unregister(){
        manager.removePacketListener(this);
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrappedChatComponent chat = packet.getChatComponents().read(0).deepClone();
        String jsonString = chat.getJson();
        if(jsonString != null && jsonString.contains("{") && jsonString.contains("}")){
            JsonObject obj = GSON.fromJson(jsonString, JsonObject.class);
            if(obj.has("insertion")){
                UUID uuid = UUID.fromString(obj.get("insertion").getAsString());
                Entity en = Bukkit.getEntity(uuid);
                if(en != null && en instanceof Villager && en.getCustomName() != null && en.getPersistentDataContainer().has(DConstants.VILLAGER_NAMED_KEY, PersistentDataType.INTEGER)){
                    Villager vil = (Villager) en;
                    String name = vil.getCustomName();
                    String profession = vil.getProfession().toString();

                    profession = profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
                    String finalName = instance.getMessageManager().getMessageWithoutPrefix("villager-trade-display")
                            .replace("%name%", name)
                            .replace("%profession%", profession);
                    boolean replaced = false;
                    if(obj.has("extra") && obj.get("extra").isJsonArray()){
                        JsonArray newExtra = new JsonArray();
                        newExtra.add(new JsonPrimitive(finalName));
                        obj.add("extra",newExtra);
                        obj.addProperty("text", "");
                        replaced = true;
                    }
                    else if(obj.has("text") && !obj.has("extra")){
                        obj.addProperty("text",finalName);
                        replaced = true;
                    }
                    if(!replaced) return;
                    packet.getChatComponents().write(0, WrappedChatComponent.fromJson(obj.toString()));
                }
            }

        }
    }
    


}
