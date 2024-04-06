package com.deserialize.transpirepets.events;

import com.saicone.rtag.RtagItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class EntityDamage implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() != DamageCause.FALL) {
                return;
            }

            Player player = (Player)event.getEntity();
            player.getItemInUse();
            if (player.getItemInHand().getType() == Material.AIR) {
                return;
            }

            ItemStack itemStack = player.getItemInHand();
            RtagItem item = new RtagItem(itemStack);
            if (item.hasTag("Pet") && item.get("Type").equals("SHEEP")) {
                event.getEntity().sendMessage("yes");
                event.setCancelled(true);
            }
        }

    }
}

