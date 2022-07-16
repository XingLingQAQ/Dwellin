package com.github.sachin.dwellin.utils;

import com.github.sachin.dwellin.Dwellin;
import com.github.sachin.prilib.utils.FastItemStack;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ItemBuilder {



    public static ItemStack itemFromFile(ConfigurationSection section,String itemType){
        Preconditions.checkNotNull(section, "item cant be null");
        Preconditions.checkArgument(section.contains("id"), "item should atleast contain id");
        ItemStack item = new ItemStack(Enums.getIfPresent(Material.class, section.getString("id").toUpperCase()).or(Material.RED_STAINED_GLASS_PANE));
        // check if section has amount
        if(section.contains("amount")){
            item.setAmount(section.getInt("amount",1));
        }

        // this should only happen if material type is air
        ItemMeta meta = item.getItemMeta();
        if(meta == null){
            return item;
        }

        // check for display name
        if(section.contains("display")){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',section.getString("display")));
        }

        // check for lore
        if(section.contains("lore")){
            List<String> lore = new ArrayList<>();
            section.getStringList("lore").forEach(s -> {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            });
            meta.setLore(lore);
        }

        // check for damage, can be usefull versions below 1.14 for resource pack
        if(section.contains("damage")){
            if (meta instanceof Damageable) {
                int damage = section.getInt("damage");
                if (damage > 0) ((Damageable) meta).damage(damage);
            }
        }

        // check for extra options on item
        if(section.contains("options")){
            ConfigurationSection options = section.getConfigurationSection("options");
            if(options.getBoolean("enchanted",false)){
                meta.addEnchant(Enchantment.MENDING, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(options.contains("patterns")){
                if(item.getType() == Material.SHIELD){
                    BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                    BlockState state = blockStateMeta.getBlockState();
                    Banner bannerState = (Banner) state;
                    bannerState.setPatterns(getBannerPatterns(options.getConfigurationSection("patterns")));
                    bannerState.update();
                    blockStateMeta.setBlockState(bannerState);
                }
                else if(meta instanceof BannerMeta){
                    BannerMeta banner = (BannerMeta) meta;
                    banner.setPatterns(getBannerPatterns(options.getConfigurationSection("patterns")));
                }
            }
            else if(meta instanceof LeatherArmorMeta){
                LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                String colorStr = options.getString("color");
                if (colorStr != null) {
                    leather.setColor(parseColor(colorStr));
                }
            }
            else if ((meta instanceof PotionMeta) && options.contains("color")){
                PotionMeta potion = (PotionMeta) meta;
                potion.setColor(Color.fromRGB(options.getInt("color",0)));

            }
            else if((meta instanceof SkullMeta) && options.contains("texture")){
                SkullMeta skullMeta = (SkullMeta) meta;
                Dwellin.getPlugin().getNmsHandler().applyHeadTexture(skullMeta, options.getString("texture"));
            }
            if(options.contains("model")){
                meta.setCustomModelData(options.getInt("model", 0));
            }
        }

        // enchants
        if (section.contains("enchants")) {
            for (String string : section.getStringList("enchants")) {
                String[] l = string.split(" ");
                if(l.length != 2) continue;
                String name = l[0].toLowerCase();
                int level = Integer.parseInt(l[1]);
                Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(name));
                if(enchant == null) continue;
                meta.addEnchant(enchant, level, true);
            }
        }

        // item flags
        if(section.contains("flags")){
            List<String> itemFlags = section.getStringList("flags").stream().map(m -> m.toUpperCase()).collect(Collectors.toList());
            for(String str : itemFlags){
                if(str.equals("ALL")){
                    meta.addItemFlags(ItemFlag.values());
                    break;
                }
                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, str).orNull();
                if(itemFlag != null){
                    meta.addItemFlags(itemFlag);
                }
            }
        }
        if((meta instanceof FireworkEffectMeta) && section.contains("firework")) {
            FireworkEffectMeta firework = (FireworkEffectMeta) meta;
            ConfigurationSection fireworkConfig = section.getConfigurationSection("firework");
            if(fireworkConfig != null){

                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.with(Type.STAR);
                List<String> strColors = fireworkConfig.getStringList("colors");
                List<Color> colors = new ArrayList<>(strColors.size());
                for (String str: strColors) {
                    colors.add(parseColor(str));
                }
                builder.withColor(colors);
                firework.setEffect(builder.build());

            }
        }

        if(itemType != null){
            meta.getPersistentDataContainer().set(DConstants.DWELLIN_ITEM, PersistentDataType.STRING, itemType);

        }
        item.setItemMeta(meta);

        return item;
    }

    private static Color parseColor(String str) {
        if (Strings.isNullOrEmpty(str)) return Color.BLACK;
        String[] rgb = StringUtils.split(StringUtils.deleteWhitespace(str), ',');
        if (rgb.length < 3){
            return getColorFromString(str);
        }
        return Color.fromRGB(NumberUtils.toInt(rgb[0], 0), NumberUtils.toInt(rgb[1], 0), NumberUtils.toInt(rgb[2], 0));
    }

    public static boolean hasKey(FastItemStack fastItem, String value){
        return !fastItem.isAir() && fastItem.hasKey(DConstants.DWELLIN_ITEM, PersistentDataType.STRING) && fastItem.get(DConstants.DWELLIN_ITEM, PersistentDataType.STRING).equals(value);
    }





    public static Color getColorFromString(String str){
        switch (str) {
            case "RED":
                return Color.RED;
            case "BLUE":
                return Color.BLUE;
            case "GREEN":
                return Color.GREEN;
            case "AQUA":
                return Color.AQUA;
            case "BLACK":
                return Color.BLACK;
            case "SILVER":
                return Color.SILVER;
            case "MAROON":
                return Color.MAROON;
            case "YELLOW":
                return Color.YELLOW;
            case "OLIVE":
                return Color.OLIVE;
            case "ORANGE":
                return Color.ORANGE;
            case "PURPLE":
                return Color.PURPLE;
            case "TEAL":
                return Color.TEAL;
            default:
                return Color.WHITE;
        }
    }


    private static List<Pattern> getBannerPatterns(ConfigurationSection patterns){
        List<Pattern> list = new ArrayList<>();
        if (patterns != null) {
            for (String pattern : patterns.getKeys(false)) {
                PatternType type = PatternType.getByIdentifier(pattern);
                if (type == null) type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);
                list.add(new Pattern(color, type));

            }
        }
        return list;
    }




    public static RecipeChoice getMaterials(String str) {
        if (str == null)
            return new MaterialChoice(Material.AIR);
        if (str.contains("|")) {
            List<String> l = Arrays.asList(str.split("\\|"));
            List<Material> mats = new ArrayList<>();
            for (String s : l) {
                Optional<Material> opMat2 = Enums.getIfPresent(Material.class, s.toUpperCase());
                if (opMat2.isPresent()) {
                    mats.add(opMat2.get());
                }
            }
            return new MaterialChoice(mats);
        }
        Optional<Material> opMat = Enums.getIfPresent(Material.class, str.toUpperCase());
        if (opMat.isPresent()) {
            return new MaterialChoice(opMat.get());
        }
        Tag<Material> tag = Bukkit.getTag("blocks", NamespacedKey.minecraft(str.toLowerCase()), Material.class);
        if (tag != null) {
            List<ItemStack> items = new ArrayList<>();
            List<Material> mats = new ArrayList<>();
            for (Material m : tag.getValues()) {
                items.add(new ItemStack(m));
                mats.add(m);
            }
            return new MaterialChoice(mats);
        }

        return new MaterialChoice(Material.AIR);
    }


}