package com.deserialize.transpirepets.events;

import com.deserialize.transpirepets.TranspirePets;
import com.saicone.rtag.RtagItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDrop implements Listener {

    TranspirePets main;
    public PlayerDrop(TranspirePets pets){
        main = pets;
    }
    private static FileConfiguration config = TranspirePets.getInstance().getConfig();

    @EventHandler(
            ignoreCancelled = true
    )
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();
        RtagItem item = new RtagItem(itemStack);
        if (item.hasTag("Droppable")) {
            Boolean b = Boolean.parseBoolean(item.get("Droppable"));
            if (!b) {
                event.setCancelled(true);
                player.sendMessage(main.getColor().format(config.getString("messages.cannotdrop-this-item")));
                if (main.getDropEvent().containsKey(player.getUniqueId())){
                }else{
                    main.getDropEvent().put(player.getUniqueId(), "");
                }}
        }

    }
}