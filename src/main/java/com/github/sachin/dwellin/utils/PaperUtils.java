package com.github.sachin.dwellin.utils;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PaperUtils {


//    @SuppressWarnings("deprecation")
    public static void removeHurtByTarget(IronGolem golem){
        Method method;
        try {
            
            method = Class.forName("org.bukkit.Bukkit").getMethod("getMobGoals");
            ((MobGoals)method.invoke(null)).removeGoal(((Creature)golem), VanillaGoal.HURT_BY);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
    }

    
}
