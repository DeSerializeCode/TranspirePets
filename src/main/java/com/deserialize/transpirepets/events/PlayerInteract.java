package com.deserialize.transpirepets.events;

import com.deserialize.transpirepets.TranspirePets;
import com.deserialize.transpirepets.Utils.RandomCollection;
import com.google.common.collect.ImmutableSet;
import com.saicone.rtag.Rtag;
import com.saicone.rtag.RtagItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerInteract implements Listener {

    public TranspirePets main;
    public PlayerInteract(TranspirePets pets){
        main = pets;
    }

    @EventHandler
    public void onRightClickEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.AIR) {
            ItemStack itemStack = player.getItemInHand();
            RtagItem item = new RtagItem(itemStack);
            if (item.hasTag("Pet")) {
                String petType = item.get("Pet");
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    player.sendMessage(main.getColor().format(main.getConfig().getString("messages.failed-blockplace").replace("%petType%", petType)));
                    event.setCancelled(true);
                    return;
                }else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)return;

                if (main.getDropEvent().containsKey(player.getUniqueId())){
                    event.setCancelled(true);
                    main.getDropEvent().remove(player.getUniqueId());
                    return;
                }
                if (item.get("Type").equals("POTION_EFFECT")) {
                    this.runPotionEffectPet(petType, item, player);
                } else if (item.get("Type").equals("COMMAND")) {
                    this.runCommandPet(petType, item, player);
                } else if (item.get("Type").equals("CHICKEN")) {
                   String fl = item.get("Flight");
                   boolean b = Boolean.parseBoolean(fl);
                    if (b) {
                        item.set("false","Flight");
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        item.load();
                        player.setItemInHand(item.getItem());
                        player.sendMessage(main.getColor().format(main.getConfig().getString("messages.flight-toggle-on")));

                    } else {
                        item.set("true", "Flight");
                        item.load();
                        player.setItemInHand(item.getItem());
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage(main.getColor().format(main.getConfig().getString("messages.flight-toggle-off")));
                    }

                } else if (item.get("Type").equals("CHANCE_COMMAND")) {
                    this.runChanceCommandPet(item, petType, player);
                }
            }
        }

    }

    public void runChanceCommandPet(RtagItem item, String type, Player player) {
        if (!this.inCooldown(item, player, type)) {
            String c = ((RandomCollection)this.main.getPetRewards().get(type)).next().toString();
            String command = c.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void runCommandPet(String type, RtagItem item, Player player) {
        if (!this.inCooldown(item, player, type)) {
            Iterator var4 = this.main.getConfig().getStringList("pets." + type + ".item.commands").iterator();

            while(var4.hasNext()) {
                String s = (String)var4.next();
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
                item.set(System.currentTimeMillis(), "lastUsed");
                item.load();
                player.setItemInHand(item.getItem());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("pets." + type + ".activate-message"))));
                return false;
            } else {
                int timeLeft = (int)(((long)item.get("lastUsed") + (long)this.getCooldownTime(type) - System.currentTimeMillis()) / 1000L + 1L);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("pets." + type + ".cooldown-message"))).replace("%time%", this.getDurationString(timeLeft)));
                return true;
            }
        } else {
            item.set(System.currentTimeMillis(), "lastUsed");
            item.load();
            player.setItemInHand(item.getItem());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("pets." + type + ".activate-message")));
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
