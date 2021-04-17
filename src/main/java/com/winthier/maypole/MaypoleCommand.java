package com.winthier.maypole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class MaypoleCommand implements CommandExecutor {
    private final MaypolePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals("hi")) {
            highscore(sender);
            return true;
        }
        if (args.length == 0) return false;
        switch (args[0]) {
        case "reload":
            plugin.reloadAll();
            sender.sendMessage("Maypole configurations reloaded");
            break;
        case "interact":
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) plugin.interact(target);
                sender.sendMessage("Interaction triggered for " + target.getName());
            }
            break;
        case "test":
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                sender.sendMessage("Triggering complete return for " + target.getName() + "...");
                plugin.playerReturns(target);
            }
            break;
        case "all":
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    for (Collectible collectible: Collectible.values()) {
                        plugin.getPlayerProgress(target).set(collectible.key, true);
                    }
                    plugin.savePlayerProgress();
                    sender.sendMessage(target.getName() + " was given all collectibles");
                }
            }
            break;
        case "unlock":
            if (args.length == 3) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) return false;
                Collectible collectible = Collectible.valueOf(args[2].toUpperCase());
                plugin.getPlayerProgress(target).set(collectible.key, true);
                plugin.savePlayerProgress();
                sender.sendMessage("" + collectible + " unlocked for " + target.getName() + ".");
            }
            return true;
        case "none":
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    for (Collectible collectible: Collectible.values()) {
                        plugin.getPlayerProgress(target).set(collectible.key, false);
                    }
                    plugin.savePlayerProgress();
                    sender.sendMessage(target.getName() + " was cleared of all collectibles");
                }
            }
            break;
        case "info":
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    ConfigurationSection prog = plugin.getPlayerProgress(target);
                    sender.sendMessage(target.getName() + " Maypole Porgress");
                    for (String key: prog.getKeys(false)) {
                        sender.sendMessage(key + ": " + prog.get(key));
                    }
                }
            }
            break;
        case "book": {
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    plugin.giveBook(target);
                }
                sender.sendMessage("Book given to " + target.getName());
            }
            break;
        }
        case "highscore": {
            Map<String, Integer> hi = new HashMap<>();
            List<String> ls = new ArrayList<>();
            for (String key: plugin.getPlayerProgress().getKeys(false)) {
                hi.put(key, plugin.getPlayerProgress().getConfigurationSection(key)
                       .getInt("Completions", 0));
                ls.add(key);
            }
            Collections.sort(ls, (a, b) -> Integer.compare(hi.get(b), hi.get(a)));
            int i = 0;
            for (String key: ls) {
                sender.sendMessage("#" + (++i) + " " + hi.get(key) + " "
                                   + plugin.getPlayerProgress().getConfigurationSection(key)
                                   .getString("Name", "N/A"));
            }
            break;
        }
        case "build": {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Player expected.");
                return true;
            }
            Player player = (Player) sender;
            plugin.buildMaypole(player);
            sender.sendMessage(player.getName() + " head added to Maypole.");
            return true;
        }
        case "message": {
            if (args.length != 2) return false;
            if (!(sender instanceof Player)) {
                sender.sendMessage("Player expected.");
                return true;
            }
            Player player = (Player) sender;
            Collectible collectible;
            try {
                collectible = Collectible.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException iae) {
                player.sendMessage("Collectible expected: " + args[1]);
                return true;
            }
            plugin.sendUnlockMessage(player, collectible);
            return true;
        }
        default:
            return false;
        }
        return true;
    }

    protected void highscore(CommandSender sender) {
        Map<String, Integer> hi = new HashMap<>();
        List<String> ls = new ArrayList<>();
        for (String key: plugin.getPlayerProgress().getKeys(false)) {
            ConfigurationSection section = plugin.getPlayerProgress().getConfigurationSection(key);
            int score = section.getInt("Completions", 0) * 16;
            for (Collectible collectible : Collectible.values()) {
                if (section.getBoolean(collectible.key)) score += 1;
            }
            hi.put(key, score);
            ls.add(key);
        }
        Collections.sort(ls, (a, b) -> Integer.compare(hi.get(b), hi.get(a)));
        int rank = 0;
        sender.sendMessage(""
                           + ChatColor.BLUE + " * * * "
                           + ChatColor.WHITE + "Maypole"
                           + ChatColor.GOLD + " Highscore"
                           + ChatColor.BLUE + " * * * ");
        for (String key: ls) {
            int score = hi.get(key);
            if (score == 0) break;
            sender.sendMessage(ChatColor.GRAY + "#"
                               + ChatColor.BLUE + (++rank)
                               + ChatColor.GOLD + " " + score
                               + ChatColor.BLUE + " " + plugin.getPlayerProgress()
                               .getConfigurationSection(key).getString("Name", "N/A"));
            if (rank >= 20) break;
        }
    }
}
