package com.deserialize.transpirepets;

import java.util.Iterator;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetsGiveCommands implements CommandExecutor {

    public TranspirePets main;
    public PetsGiveCommands(TranspirePets pets){
        main = pets;
    }
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equals("pets")) {
            Player target;
            switch(args.length) {
                case 0:
                    for (String message : main.getConfig().getStringList("message.help")) {
                        sender.sendMessage(this.translate(message));
                    }

                    return false;
                case 1:
                    if (sender instanceof Player) {
                        target = (Player)sender;
                        if (args[0].equalsIgnoreCase("view")) {
                            target.openInventory(main.petsMenu.pets(target));
                        } else if (args[0].equalsIgnoreCase("help")) {
                            this.sendHelpMessage(target);
                        } else {
                            sender.sendMessage(this.translate(main.getConfig().getString("messages.invalid-args")));
                        }
                    } else {
                        sender.sendMessage(this.translate("&cThis command is player only!"));
                    }
                    break;
                case 2:
                default:
                    sender.sendMessage(this.translate(main.getConfig().getString("messages.wrong-usage")));
                    break;
                case 3:
                    if (!sender.hasPermission("pets.admin")) {
                        sender.sendMessage(this.translate(main.getConfig().getString("messages.no_permission")));
                        return false;
                    }

                    if (!args[0].equalsIgnoreCase("give")) {
                        sender.sendMessage(this.translate(main.getConfig().getString("messages.usage")));
                        return false;
                    }

                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(this.translate(Objects.requireNonNull(main.getConfig().getString("messages.no_player")).replaceAll("%target%", args[1])));
                        return false;
                    }

                    if (this.main.allPets.containsKey(args[2])) {
                        target.getInventory().addItem(new ItemStack[]{(ItemStack)this.main.allPets.get(args[2])});
                    } else {
                        sender.sendMessage(this.translate(main.getConfig().getString("messages.invalid-pet")));
                    }
            }
        }

        return false;
    }

    public void sendHelpMessage(Player player) {
        Iterator var2 = this.main.getConfig().getStringList("messages.help").iterator();

        while(var2.hasNext()) {
            String string = (String)var2.next();
            player.sendMessage(this.translate(string));
        }

    }

    public String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    }
