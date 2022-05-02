package com.winthier.maypole;

import com.cavetale.core.command.AbstractCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class MaypoleCommand extends AbstractCommand<MaypolePlugin> {
    protected MaypoleCommand(final MaypolePlugin plugin) {
        super(plugin, "maypole");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("hi").denyTabCompletion()
            .description("Maypole highscore")
            .senderCaller(this::highscore);
    }

    protected void highscore(CommandSender sender) {
        Map<String, Integer> hi = new HashMap<>();
        List<String> ls = new ArrayList<>();
        for (String key : plugin.getPlayerProgress().getKeys(false)) {
            ConfigurationSection section = plugin.getPlayerProgress().getConfigurationSection(key);
            int score = section.getInt("Completions", 0) * 16;
            for (Collectible collectible : Collectible.values()) {
                if (section.getBoolean(collectible.key)) score += 1;
            }
            hi.put(key, score);
            ls.add(key);
        }
        Collections.sort(ls, (a, b) -> Integer.compare(hi.get(b), hi.get(a)));
        sender.sendMessage(""
                           + ChatColor.BLUE + " * * * "
                           + ChatColor.WHITE + "Maypole"
                           + ChatColor.GOLD + " Highscore"
                           + ChatColor.BLUE + " * * * ");
        for (int i = 0; i < 10; i += 1) {
            String key = ls.get(i);
            int score = hi.get(key);
            if (score == 0) break;
            ConfigurationSection section = plugin.getPlayerProgress().getConfigurationSection(key);
            String name = section.getString("Name", "N/A");
            int completions = section.getInt("Completions", 0);
            sender.sendMessage(ChatColor.GRAY + "#" + String.format("%02d", i + 1)
                               + ChatColor.GOLD + " " + score
                               + ChatColor.WHITE + " " + name
                               + ChatColor.GRAY + ChatColor.ITALIC + " " + completions + " completions");
        }
        if (sender instanceof Player) {
            int yourRank = ls.indexOf(((Player) sender).getUniqueId().toString());
            if (yourRank >= 0) {
                sender.sendMessage(ChatColor.GRAY + "Your rank: " + ChatColor.GOLD + "#" + yourRank);
            }
        }
    }
}
