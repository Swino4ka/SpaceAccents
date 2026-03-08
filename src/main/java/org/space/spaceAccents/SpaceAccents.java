package org.space.spaceAccents;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpaceAccents extends JavaPlugin {
    private AccentManager accentManager;
    private final Map<UUID, String> playerAccent = new ConcurrentHashMap<>();
    private ChatListener chatListener;
    private File playersFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        accentManager = new AccentManager(this);
        accentManager.loadDefaults();

        // load players' selected accents
        playersFile = new File(getDataFolder(), "players.yml");
        if (playersFile.exists()) {
            try {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(playersFile);
                for (String key : cfg.getKeys(false)) {
                    try {
                        UUID id = UUID.fromString(key);
                        String accent = cfg.getString(key, null);
                        if (accent != null) playerAccent.put(id, accent);
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                getLogger().warning("Ошибка при загрузке players.yml: " + e.getMessage());
            }
        }

        chatListener = new ChatListener(accentManager, playerAccent, playersFile);
        getServer().getPluginManager().registerEvents(chatListener, this);

        PluginCommand cmd = getCommand("accent");
        if (cmd != null) {
            AccentCommand executor = new AccentCommand(accentManager, playerAccent, chatListener);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("AccentChat включён.");
    }

    @Override
    public void onDisable() {
        // сохранение
        try {
            chatListener.save();
        } catch (Exception ignored) {}
        HandlerList.unregisterAll(this);
        getLogger().info("AccentChat выключён.");
    }
}