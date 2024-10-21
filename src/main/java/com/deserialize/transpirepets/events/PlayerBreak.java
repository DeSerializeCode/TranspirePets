package com.deserialize.transpirepets.events;

import com.deserialize.transpirepets.TranspirePets;
import com.saicone.rtag.RtagItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerBreak implements Listener {

    TranspirePets main;

    public PlayerBreak(TranspirePets pets) {
        main = pets;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (hasPet(event.getPlayer())) {
            if (!main.getLumberBlocks().contains(event.getBlock().getType().toString())) return;
            Block block = event.getBlock();
            List<Block> relativeBlocks = getRelativeBlocks(block);
                    for (Block block1 : relativeBlocks) {
                            block1.breakNaturally();
            }
            relativeBlocks.clear();
        }
    }

    public List<Block> getRelativeBlocks(Block block) {
        List<Block> relativeBlocks = new ArrayList<>();
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN};
        relativeBlocks.add(block);
        for (BlockFace face1 : faces) {
            Block relative1 = block.getRelative(face1);
            while (relative1.getType() == block.getType()) {
                relativeBlocks.add(relative1);
                relative1 = relative1.getRelative(face1);
            }
        }

        return relativeBlocks;
    }

    public boolean hasPet(Player player) {
        boolean hasPet = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                RtagItem ritem = new RtagItem(item);
                if (ritem.hasTag("Type")) {
                    if (ritem.get("Type").equals("LUMBER")) {
                        hasPet = true;
                    }
                }
            }
        }
        return hasPet;
    }
}
