package com.deserialize.transpirepets.Utils;

import org.bukkit.ChatColor;

public class Color {
    public String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String getDurationBreakdown(long seconds) {
        String old = String.valueOf(seconds / 60L);
        String time = "";
        Long remainder = seconds % 60L;
        if (old.contains(".")) {
            String[] values = old.split(".");
            time = values[0] + ":" + remainder;
        } else {
            time = old + ":00";
        }

        return time;
    }
}