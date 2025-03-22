package org.zb.rulesServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.zb.rulesServer.utils.Console;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public final class RulesServer extends JavaPlugin implements Listener {

    private List<Map<String, Object>> messages;
    private String TextRules;
    private boolean AlertsSwitch;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onEnable() {
        // Создаем папку плагина, если она отсутствует
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists() && pluginFolder.mkdirs()) {
            getLogger().info("Created plugin folder: " + pluginFolder.getPath());
        }

        // Создаем файл alerts.yml, если он отсутствует
        File rulesFile = new File(pluginFolder, "alerts.yml");
        if (!rulesFile.exists()) {
            saveResource("alerts.yml", false);
            getLogger().info("Created default alerts.yml file.");
        }

        saveDefaultConfig(); // Создаем config.yml, если он отсутствует
        loadConfigs(); // Загружаем все конфиги

        // Регистрируем события
        Bukkit.getPluginManager().registerEvents(this, this);

        Console.log("&2" + new Date() + " Rules Server start plugin!");
        getLogger().info("RulesServer plugin enabled!");
    }

    @Override
    public void onDisable() {
        Console.log("&4" + new Date() + " Rules Server stop plugin!");
        getLogger().info("RulesServer plugin disabled!");
    }

    private void loadConfigs() { //load all config
        reloadConfig(); // load/reload config.yml
        loadAlerts(); //load Alert

        TextRules = getConfig().getString("rules-text", "Simple Rules...");
        AlertsSwitch = getConfig().getBoolean("alerts", false);

        if (TextRules == null || TextRules.isEmpty()) {
            getLogger().warning("The server rules message is not defined in the config.yml file or is empty.");
        }
    }

    private void loadAlerts() {
        File alertsFile = new File(getDataFolder(), "alerts.yml");
        if (!alertsFile.exists()) {
            saveResource("alerts.yml", false); // Создаем файл, если он отсутствует
        }

        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(alertsFile.toPath())) {
            messages = yaml.load(inputStream); // Загружаем сразу список
            if (messages == null || messages.isEmpty()) {
                getLogger().warning("No messages found in alerts.yml!");
            }

        } catch (IOException e) {
            getLogger().severe("Failed to load alerts.yml: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Date currentDate = new Date();

        if (AlertsSwitch) {
            if (messages != null && !messages.isEmpty()) {
                for (Map<String, Object> messageData : messages) {

                    Integer delay = (Integer) messageData.get("delay");
                    String text = (String) messageData.get("text");
                    String dateString = (String) messageData.get("data");

                    if (delay == null || text == null || text.isBlank()) {
                        continue;
                    }

                    if (dateString != null) {
                        try {
                            Date messageDate = DATE_FORMAT.parse(dateString);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(messageDate);
                            calendar.add(Calendar.DAY_OF_MONTH, 1); // Добавляем 1 день
                            messageDate = calendar.getTime();

                            if (messageDate.before(currentDate)) {
                                continue;
                            }
                        } catch (ParseException e) {
                            continue;
                        }
                    }

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        if (player.isOnline()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                        }
                    }, delay * 20L); // Переводим секунды в тики
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rules")) {
            if (sender instanceof Player) {
                sendRules((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("rs")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("rules.reload")) {
                    loadConfigs(); // Перезагрузка настроек
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload.");
                }
                return true;
            }
        }
        return false;
    }

    private void sendRules(Player player) {
        if (TextRules == null || TextRules.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No rules are loaded.");
            return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', TextRules));
    }

}