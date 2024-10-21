package com.deserialize.transpirepets.utils;

import com.deserialize.transpirepets.TranspirePets;
import com.saicone.rtag.RtagItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Utils {
    public TranspirePets main;
    public Utils(TranspirePets pets) {
        main = pets;
    }
    public void runChanceCommandPet(RtagItem item, String type, Player player) {
        if (!this.inCooldown(item, player, type)) {
            String c = ((RandomCollection<?>)main.getPetRewards().get(type)).next().toString();
            String command = c.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void runCommandPet(String type, RtagItem item, Player player) {
        if (!this.inCooldown(item, player, type)) {

            for (String s : this.main.getConfig().getStringList("pets." + type + ".item.commands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
            }

        }
    }

    public void runPotionEffectPet(String type, RtagItem item, Player player) {
        if (!this.inCooldown(item, player, type)) {
            ArrayList<PotionEffect> effects = new ArrayList();
            Iterator var5 = this.main.getConfig().getStringList("pets." + type + ".item.effects").iterator();

            while(var5.hasNext()) {
                String s = (String)var5.next();
                String[] split = s.split(",");
                String potionType = split[0];
                int level = Integer.parseInt(split[1]);
                int duration = Integer.parseInt(split[2]);
                PotionEffectType potionEffectType = PotionEffectType.getByName(potionType);
                if (potionEffectType == null) {
                    player.sendMessage(potionType + " is not a potion effect, please report to an admin");
                    return;
                }

                PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, level);
                effects.add(potionEffect);
            }

            var5 = effects.iterator();

            while(var5.hasNext()) {
                PotionEffect ef = (PotionEffect)var5.next();
                player.addPotionEffect(ef);
            }

        }
    }

    public boolean inCooldown(RtagItem item, Player player, String type) {
        if (item.hasTag("lastUsed")) {
            if (System.currentTimeMillis() >= (long)item.get("lastUsed") + (long)this.getCooldownTime(type)) {
                item.remove("lastUsed");
                item.set("lastUsed", (long)System.currentTimeMillis());
                item.update();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("pets." + type + ".activate-message"))));
                return false;
            } else {
                int timeLeft = (int)(((long)item.get("lastUsed") + (long)this.getCooldownTime(type) - System.currentTimeMillis()) / 1000L + 1L);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("pets." + type + ".cooldown-message"))).replace("%time%", this.getDurationString(timeLeft)));
                return true;
            }
        } else {
            item.set("lastUsed", System.currentTimeMillis());
            item.update();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("pets." + type + ".activate-message"))));
            return false;
        }
    }

    public int getCooldownTime(String type) {
        return this.main.getConfig().getInt("pets." + type + ".item.cooldown") * 1000;
    }

    private String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        seconds %= 60;
        return this.twoDigitString(hours) + " : " + this.twoDigitString(minutes) + " : " + this.twoDigitString(seconds);
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        } else {
            return number / 10 == 0 ? "0" + number : String.valueOf(number);
        }
    }
}
