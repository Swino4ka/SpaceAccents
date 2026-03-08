package org.space.spaceAccents;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AccentManager {
    private final JavaPlugin plugin;
    private final Map<String, Accent> accents = new HashMap<>();

    public AccentManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadDefaults() {
        // если нет файла в папке плагина — кладём дефолтный из ресурсов
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File file = new File(dataFolder, "accents.yml");
        if (!file.exists()) {
            try (InputStream in = plugin.getResource("accents.yml")) {
                if (in != null) {
                    java.nio.file.Files.copy(in, file.toPath());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось создать accents.yml: " + e.getMessage());
            }
        }
        reload();
    }

    public void reload() {
        accents.clear();
        try {
            File file = new File(plugin.getDataFolder(), "accents.yml");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection root = cfg.getConfigurationSection("accents");
            if (root == null) return;
            for (String key : root.getKeys(false)) {
                ConfigurationSection ac = root.getConfigurationSection(key);
                String display = ac.getString("display", key);
                List<Replacement> reps = new ArrayList<>();
                List<?> list = ac.getList("replacements", Collections.emptyList());
                if (list != null) {
                    for (Object o : list) {
                        if (!(o instanceof Map)) continue;
                        @SuppressWarnings("unchecked")
                        Map<String, Object> m = (Map<String, Object>) o;
                        String from = Objects.toString(m.get("from"), "").trim();
                        String to = Objects.toString(m.get("to"), "");
                        if (!from.isEmpty()) reps.add(new Replacement(from, to));
                    }
                }
                Accent a = new Accent(key.toLowerCase(Locale.ROOT), display, reps);
                accents.put(key.toLowerCase(Locale.ROOT), a);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при загрузке accents.yml: " + e.getMessage());
        }
    }

    public Optional<Accent> getAccent(String id) {
        return Optional.ofNullable(accents.get(id == null ? null : id.toLowerCase(Locale.ROOT)));
    }

    public Collection<Accent> listAccents() {
        return accents.values().stream().collect(Collectors.toList());
    }

    public Set<String> listAccentIds() {
        return new HashSet<>(accents.keySet());
    }
}