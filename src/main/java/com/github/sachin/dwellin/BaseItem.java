package com.github.sachin.dwellin;

import com.github.sachin.dwellin.utils.ItemBuilder;
import com.github.sachin.prilib.utils.FastItemStack;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class BaseItem extends BaseModule{

    protected ItemStack item;
    protected final List<NamespacedKey> recipes = new ArrayList<>();

    @Override
    public void reload() {
        super.reload();
        this.item = ItemBuilder.itemFromFile(plugin.getItemFolder().CONFIG.getConfigurationSection(getName()),getName());
    }

    @Override
    public void register() {
        super.register();
        registerRecipes();
    }

    @Override
    public void unregister() {
        super.unregister();
        unregisterRecipes();
    }

    public boolean isSimilar(ItemStack item){
        return ItemBuilder.hasKey(new FastItemStack(item),getName());
    }
    

    public void registerRecipes(){
        ConfigurationSection recipeSection = plugin.getRecipeFile().getConfigurationSection(name);
        if(recipeSection != null){
            for(String key : recipeSection.getKeys(false)){
                ConfigurationSection recipe = recipeSection.getConfigurationSection(key);
                if(!recipe.contains("type")) continue;
                String type = recipe.getString("type","none");
                NamespacedKey recipeKey = Dwellin.getKey(type+"_"+name+"_"+key);
                Recipe bukkitRecipe = null;
                if(type.equals("stonecutter")){
                    ExactChoice choice = getIngredient(recipe.getString("input"));
                    if(choice == null) continue;
                    int outputAmount = recipe.getInt("output-quantity",1);
                    ItemStack output = item.clone();
                    output.setAmount(outputAmount);
                    StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(recipeKey, output, choice);
                    bukkitRecipe = stonecuttingRecipe;

                }
                if(type.equals("shaped")){
                    if(recipe.contains("pattern") && recipe.contains("keys")){
                        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, item.clone());
                        
                        List<String> patternList = recipe.getStringList("pattern");
                        boolean invalidPattern = false;
                        if(patternList.size()>3) invalidPattern = true;
                        for(String s : patternList){
                            if(s.length()>3){
                                invalidPattern = true;
                            }
                        }
                        if(invalidPattern) continue;
                        shapedRecipe.shape(patternList.toArray(new String[0]));
                        for(String ing : recipe.getConfigurationSection("keys").getKeys(false)){
                            ExactChoice choice = getIngredient(recipe.getString("keys."+ing));
                            if(choice != null){
                                shapedRecipe.setIngredient(ing.charAt(0), choice);
                            }
                        }
                        bukkitRecipe = shapedRecipe;
                    }
                }
                if(bukkitRecipe != null){
                    Bukkit.addRecipe(bukkitRecipe);
                    recipes.add(((Keyed)bukkitRecipe).getKey());
                }
            }
        }
    }

    public void unregisterRecipes(){
        for(NamespacedKey key : recipes){
            Bukkit.removeRecipe(key);
        }
    }

    private ExactChoice getIngredient(String str){
        if(str == null) return null;
        Optional<Material> opMat = Enums.getIfPresent(Material.class, str);
        if(opMat.isPresent()){
            return new ExactChoice(new ItemStack(opMat.get()));
        }
        Tag<Material> tag = Bukkit.getTag("blocks", NamespacedKey.minecraft(str.toLowerCase()), Material.class);
        if(tag != null){
            List<ItemStack> items = new ArrayList<>();
            for(Material m : tag.getValues()){
                items.add(new ItemStack(m));
            }
            return new ExactChoice(items);
        }
        return null;
    }
    
    public ItemStack getItem() {
        return item;
    }
}
