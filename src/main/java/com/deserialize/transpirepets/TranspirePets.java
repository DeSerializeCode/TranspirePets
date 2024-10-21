package com.deserialize.transpirepets;


import com.deserialize.transpirepets.menus.Pets;
import com.deserialize.transpirepets.utils.Color;
import com.deserialize.transpirepets.utils.RandomCollection;
import com.deserialize.transpirepets.utils.UpdateChecker;
import com.deserialize.transpirepets.events.EntityDamage;
import com.deserialize.transpirepets.events.PlayerBreak;
import com.deserialize.transpirepets.events.PlayerDrop;
import com.deserialize.transpirepets.events.PlayerInteract;
import com.saicone.rtag.RtagItem;
import com.saicone.rtag.util.SkullTexture;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class TranspirePets extends JavaPlugin {

    private static TranspirePets instance;
    public Pets petsMenu;
    public HashMap<UUID, String> dropEvent = new HashMap<UUID, String>();
    public HashMap<String, ItemStack> allPets = new HashMap<>();
    public Color color;
    private final HashMap<String, RandomCollection> petRewards = new HashMap<>();
    private ArrayList<String> lumberBlocks;

    public static TranspirePets getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        this.lumberBlocks = new ArrayList<>();
        this.loadConfig();
        this.loadCommands();
        this.loadListeners();
        this.createAllPets();
        this.petsMenu = new Pets(this);
        Logger logger = this.getLogger();
        this.color = new Color();
        (new UpdateChecker(this, 82199)).getVersion((version) -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version) || Double.parseDouble(this.getDescription().getVersion()) > Double.parseDouble(version)){
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available.");
                logger.info(version);
                logger.info(this.getDescription().getVersion());
            }

        });
    }

    private void loadRewards(String petType) {
        RandomCollection collection = new RandomCollection();

        for (String string : this.getConfig().getStringList("pets." + petType + ".rewards")) {
            String[] split = string.split(",");
            String command = split[0];
            double chance = Double.parseDouble(split[1]);
            collection.add(chance, String.valueOf(command));
        }

        this.petRewards.put(petType, collection);
    }

    public HashMap<UUID, String> getDropEvent() {
        return dropEvent;
    }

    public HashMap<String, RandomCollection> getPetRewards() {
        return this.petRewards;
    }

    public void onDisable() {
        instance = null;
    }

    private void loadConfig() {
        this.saveDefaultConfig();
    }

    private void loadCommands() {
        this.getCommand("pets").setExecutor(new PetsGiveCommands(this));
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerDrop(this), this);
        this.getServer().getPluginManager().registerEvents(new Pets(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBreak(this), this);
    }

    public Color getColor() {
        return this.color;
    }

    public void createAllPets() {
        Iterator var1 = this.getConfig().getConfigurationSection("pets").getKeys(false).iterator();

        while (var1.hasNext()) {
            String petName = (String) var1.next();
            String skin = this.getConfig().getString("pets." + petName + ".skin");
            String petType = this.getConfig().getString("pets." + petName + ".type");
            ItemStack item = SkullTexture.getTexturedHead(skin);
            boolean droppable = this.getConfig().getBoolean("pets." + petName + ".item.droppable");

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(this.translate(this.getConfig().getString("pets." + petName + ".item.name")));

            List<String> list = new ArrayList<>();


            for (String string : this.getConfig().getStringList("pets." + petName + ".item.lore")) {
                list.add(this.translate(string));
            }
            itemMeta.setLore(list);
            item.setItemMeta(itemMeta);

            RtagItem rtagItem = RtagItem.of(item);
            rtagItem.set(petName, "Pet");
            rtagItem.set(String.valueOf(droppable), "Droppable");
            rtagItem.set(petType, "Type");

            if (petType.equalsIgnoreCase("LUMBER")) {
                List<String> blocks = this.getConfig().getStringList("pets." + petName + ".blocks");
                for (String block : blocks) {
                    this.lumberBlocks.add(block);
                }
            }
            if (petType.equalsIgnoreCase("CHICKEN")) {
                rtagItem.set("true", "Flight");
            }
            rtagItem.load();
            if (petType.equalsIgnoreCase("CHANCE_COMMAND")) {
                this.loadRewards(petName);
            }
            this.allPets.put(petName, rtagItem.getItem());
        }
    }

    public ArrayList<String> getLumberBlocks() {
        return lumberBlocks;
    }

    public String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
