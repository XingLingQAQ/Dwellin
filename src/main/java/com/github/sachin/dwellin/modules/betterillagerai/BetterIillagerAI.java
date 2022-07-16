package com.github.sachin.dwellin.modules.betterillagerai;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.ItemBuilder;
import com.github.sachin.prilib.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

@Module(name = "better-illager-ai")
public class BetterIillagerAI extends BaseModule implements Listener{

    private boolean holdbackcrossbow;
    private boolean useFirework;
    private int minModifier;
    private int maxModifier;
    private double spawnChance;
    private List<Color> colors;

    private int maxAmount;

    @Override
    public void reload() {
        super.reload();
        this.holdbackcrossbow = config.getBoolean("hold-back-crossbow",false);
        this.useFirework = config.getBoolean("use-fireworks.enabled",false);
        this.minModifier = config.getInt("use-fireworks.fireworkstar-modifier.min",3);
        this.maxModifier = config.getInt("use-fireworks.fireworkstar-modifier.max",6);
        this.maxAmount = config.getInt("use-fireworks.max-amount",10);
        this.spawnChance = config.getDouble("use-fireworks.chance",0.5);
        this.colors = new ArrayList<>();
        for(String col:config.getStringList("use-fireworks.colors")){
            colors.add(ItemBuilder.getColorFromString(col));
        }

    }

    @EventHandler
    public void onPillagerSpawn(EntitySpawnEvent e){
        if(e.getEntity() instanceof Pillager){
            Pillager pil = (Pillager) e.getEntity();
            if(holdbackcrossbow){
                getPlugin().getNmsHandler().addHoldBackCrossBowGoal(pil);
            }
            if(RandomUtils.getChance(spawnChance) && useFirework){
                ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.with(FireworkEffect.Type.BALL);
                int count = RandomUtils.getInt(minModifier,maxModifier);
                for(int i = 0;i<count;i++){
                    builder.withColor(colors.get(RandomUtils.getInt(0,colors.size())));
                }
                FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
                meta.addEffect(builder.build());
                firework.setItemMeta(meta);
                firework.setAmount(RandomUtils.getInt(1,maxAmount));
                pil.getEquipment().setItemInOffHand(firework);
            }

        }
    }

}
