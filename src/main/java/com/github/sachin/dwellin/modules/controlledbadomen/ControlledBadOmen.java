package com.github.sachin.dwellin.modules.controlledbadomen;

import com.github.sachin.dwellin.BaseModule;
import com.github.sachin.dwellin.manager.Module;
import com.github.sachin.dwellin.utils.PermManager;
import com.github.sachin.prilib.utils.FastItemStack;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Module(name = "controlled-bad-omen")
public class ControlledBadOmen extends BaseModule implements Listener{


    private final Map<Location,BannerRunnable> runnables = new HashMap<>();
    private final Random random = new Random();
    private static final List<Pattern> OMINOUS_PATTERN = Arrays.asList(new Pattern(DyeColor.CYAN,PatternType.RHOMBUS_MIDDLE),
                                                                       new Pattern(DyeColor.LIGHT_GRAY,PatternType.STRIPE_BOTTOM),
                                                                       new Pattern(DyeColor.GRAY,PatternType.STRIPE_CENTER),
                                                                       new Pattern(DyeColor.LIGHT_GRAY,PatternType.BORDER),
                                                                       new Pattern(DyeColor.BLACK,PatternType.STRIPE_MIDDLE),
                                                                       new Pattern(DyeColor.LIGHT_GRAY,PatternType.HALF_HORIZONTAL),
                                                                       new Pattern(DyeColor.LIGHT_GRAY,PatternType.CIRCLE_MIDDLE),
                                                                       new Pattern(DyeColor.BLACK,PatternType.BORDER));

    @EventHandler
    public void onPotionEffectChange(EntityPotionEffectEvent e){
        if(isBlackListWorld(e.getEntity().getWorld()) || !getConfig().getBoolean("prevent-bad-omen-from-captain")) return;
        if((e.getEntity() instanceof Player) && e.getCause() == Cause.PATROL_CAPTAIN){
            e.setCancelled(true);
        }
    }                                                                   


    @EventHandler
    public void onBannerBurn(PlayerInteractEvent e){
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        if(!hasPermission(player, PermManager.CONTROLLEDBADOMEN) || isBlackListWorld(player.getWorld()) || player.getWorld().getGameRuleValue(GameRule.DISABLE_RAIDS).booleanValue()) return;
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null && e.getItem().getType()==Material.FLINT_AND_STEEL && isOmoniousBanner(block) && !runnables.containsKey(block.getLocation())){
            e.setCancelled(true);
            
            BannerRunnable runnable = new BannerRunnable(block.getLocation(),player);
            runnable.runTaskTimer(plugin, 0, 1);
            runnables.put(block.getLocation(), runnable);
            if(block.getType() == Material.WHITE_WALL_BANNER){
                if(e.getHand()==EquipmentSlot.HAND){
                    player.swingMainHand();
                }
                else{
                    player.swingOffHand();
                }
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 2F, random.nextFloat() * 0.4F +0.8F);
            }
        }
    }

    private boolean isOmoniousBanner(Block block){
        return (block.getType()==Material.WHITE_BANNER || block.getType() == Material.WHITE_WALL_BANNER) && ((Banner) block.getState()).getPatterns().equals(OMINOUS_PATTERN);
    }

    public static ItemStack getOmoniousBanner(){
        return new FastItemStack(Material.WHITE_BANNER).setBannerPatterns(OMINOUS_PATTERN).get();
    }



    private class BannerRunnable extends BukkitRunnable{


        public final Player burner;
        public final Location loc;
        public final Location orignalLoc; 
        public final Material type;
        private int tick=0;

        public BannerRunnable(Location loc,Player burner){   
            this.burner = burner;
            this.orignalLoc = loc.clone();
            this.type = loc.getBlock().getType();
            if(type == Material.WHITE_BANNER){
                this.loc = loc.add(0.5, 0.2, 0.5);
            }
            else {
                this.loc = getLoc(((Directional)loc.getBlock().getBlockData()).getFacing(), orignalLoc.clone());
            }
            
            
        }

        private Location getLoc(BlockFace face,Location orignalLoc){
            switch (face) {
                case EAST:
                    return orignalLoc.add(0.15, 0.9, 0.5);
                case WEST:
                    return orignalLoc.add(0.8, 0.9, 0.5);
                case NORTH:
                    return orignalLoc.add(0.5,0.9, 0.8);
                case SOUTH:
                    return orignalLoc.add(0.5, 0.9, 0.2);    
                default:
                    return orignalLoc;
            }
        }


        public void cancelRunnable(){
            runnables.remove(orignalLoc);
            this.cancel();
        }

        @Override
        public void run() {
            if(!burner.isOnline() || !isOmoniousBanner(orignalLoc.getBlock())){ 
                cancelRunnable();
                return;
            }
            tick++;
            if(tick == getConfig().getInt("burn-time")){
                loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 2F, random.nextFloat() * 0.4F + 0.8F);
                loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, type == Material.WHITE_BANNER ? loc.clone().add(0, 1, 0) : loc.clone().subtract(0, 1, 0), 70, 0.25, 0.4, 0.25,0);
                loc.getBlock().setType(Material.AIR);
                PotionEffect effect = burner.getPotionEffect(PotionEffectType.BAD_OMEN);
                if(effect != null && effect.getAmplifier() < getConfig().getInt("max-bad-omen-level",5)){
                    burner.removePotionEffect(PotionEffectType.BAD_OMEN);
                    burner.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, effect.getAmplifier()+1,false,false,true));
                }
                else{
                    burner.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, 0,false,false,true));
                }
                cancelRunnable();
                return;
            }
            loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 2F, random.nextFloat() * 0.4F + 0.8F);
            for(int i =0;i<5;i++){
                double randPosX,randPosY,randPosZ;
                double rx,ry,rz;
                if(type == Material.WHITE_BANNER){
                    rx = random.nextDouble() * 0.48;
                    ry = random.nextDouble() * 1.65;
                    rz = random.nextDouble() * 0.48;
                }
                else{
                    rx = random.nextDouble() * -0.46;
                    ry = random.nextDouble() * -1.65;
                    rz = random.nextDouble() * -0.46;
                }
                randPosY = loc.getY() + ry;
                if(random.nextInt(2) == 1){
                    randPosX = loc.getX() + rx;
                    randPosZ = loc.getZ() + rz;
                }
                else {
                    randPosX = loc.getX() - rx;
                    randPosZ = loc.getZ() - rz;
                }
                if(random.nextInt(4) == 2){
                    loc.getWorld().spawnParticle(Particle.FLAME, randPosX, randPosY, randPosZ, 1,0,0,0,0);
                }
                else if(random.nextInt(5) == 3){
                    loc.getWorld().spawnParticle(Particle.LAVA, randPosX, randPosY, randPosZ, 1,0,0,0,0);
                }
                
            }
        }
    }
    
}
