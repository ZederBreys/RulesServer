package org.zb.rulesServer.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Console {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String PREFIX = "[RulesServer]";

    private Console() { }

    @SafeVarargs
    public static <T> void log(T... messages) {
        SENDER.sendMessage(colored(compressArgs(messages) ) );
    }

    private static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @SafeVarargs
    private static <T> String compressArgs(T... args) {
        StringBuilder builder = new StringBuilder(PREFIX);

        for (T arg : args)
            builder.append(" ").append(arg);

        return builder.toString().trim();
    }
}