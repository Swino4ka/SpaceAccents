package org.space.spaceAccents;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {
    private final AccentManager accentManager;
    // локальная мапа: UUID -> accentId
    private final Map<UUID, String> playerAccent;
    private final File playersFile;

    public ChatListener(AccentManager accentManager, Map<UUID, String> playerAccent, File playersFile) {
        this.accentManager = accentManager;
        this.playerAccent = playerAccent;
        this.playersFile = playersFile;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String accentId = playerAccent.get(e.getPlayer().getUniqueId());
        if (accentId == null) return;
        accentManager.getAccent(accentId).ifPresent(accent -> {
            String transformed = accent.apply(e.getMessage());
            e.setMessage(transformed);
        });
    }

    // helper: сохранение мапы в playersFile
    public void save() {
        try {
            YamlConfiguration cfg = new YamlConfiguration();
            for (Map.Entry<UUID, String> en : playerAccent.entrySet()) {
                cfg.set(en.getKey().toString(), en.getValue());
            }
            cfg.save(playersFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}