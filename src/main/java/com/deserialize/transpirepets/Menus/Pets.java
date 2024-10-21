package com.deserialize.transpirepets.menus;

import com.deserialize.transpirepets.TranspirePets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Pets implements Listener {

    public TranspirePets main;
    public Pets(TranspirePets pets){
        main = pets;
    }
    private static FileConfiguration config = TranspirePets.getInstance().getConfig();
    private Player player;

    public String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public Inventory pets(Player player) {

        this.player = player;
         Inventory inv = Bukkit.createInventory((InventoryHolder) null, config.getInt("petsmenu.size"), this.translate(config.getString("petsmenu.name")));
         ItemStack filler = new ItemStack(Material.getMaterial(config.getString("petsmenu.filler.material")), 1, (short) ((byte) config.getInt("petsmenu.filler.data")));

         ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (String s : this.main.allPets.keySet()) {
            ItemStack item = (ItemStack) this.main.allPets.get(s);
            inv.setItem(inv.firstEmpty(), item);
        }

        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inv.getSize(); ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }

        this.player.openInventory(inv);
        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains(main.getColor().format(config.getString("petsmenu.name")))) {
            event.setCancelled(true);
        }

    }

    public Inventory pets() {
        return null;
    }
}

