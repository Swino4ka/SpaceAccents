package org.space.spaceAccents;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AccentCommand implements CommandExecutor, TabCompleter {
    private final AccentManager accentManager;
    private final Map<UUID, String> playerAccent;
    private final ChatListener chatListener;

    public AccentCommand(AccentManager accentManager, Map<UUID, String> playerAccent, ChatListener chatListener) {
        this.accentManager = accentManager;
        this.playerAccent = playerAccent;
        this.chatListener = chatListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Команду может использовать только игрок.");
            return true;
        }
        if (args.length == 0) {
            // показать список акцентов и текущий
            String cur = playerAccent.getOrDefault(p.getUniqueId(), "off");
            sender.sendMessage("Доступные акценты: " + String.join(", ", accentManager.listAccentIds()));
            sender.sendMessage("Текущий: " + cur);
            sender.sendMessage("Использование: /accent <name|off>");
            return true;
        }
        String arg = args[0].toLowerCase(Locale.ROOT);
        if (arg.equals("off")) {
            playerAccent.remove(p.getUniqueId());
            sender.sendMessage("Акцент отключён.");
            chatListener.save();
            return true;
        }
        if (accentManager.getAccent(arg).isPresent()) {
            playerAccent.put(p.getUniqueId(), arg);
            sender.sendMessage("Выбран акцент: " + arg);
            chatListener.save();
            return true;
        } else {
            sender.sendMessage("Акцент не найден: " + arg);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(accentManager.listAccentIds());
            list.add("off");
            String pref = args[0].toLowerCase(Locale.ROOT);
            return list.stream().filter(s -> s.startsWith(pref)).sorted().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}