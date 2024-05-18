package com.winthier.maypole;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandNode;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.core.connect.Connect;
import com.cavetale.core.playercache.PlayerCache;
import com.cavetale.fam.trophy.Highscore;
import com.cavetale.mytems.item.trophy.TrophyCategory;
import com.winthier.maypole.sql.Database;
import com.winthier.maypole.sql.SQLPlayer;
import com.winthier.maypole.sql.SQLSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.winthier.maypole.sql.Database.database;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class MaypoleAdminCommand extends AbstractCommand<MaypolePlugin> {
    protected MaypoleAdminCommand(final MaypolePlugin plugin) {
        super(plugin, "maypoleadmin");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("reload").denyTabCompletion()
            .description("Reload runtime data")
            .senderCaller(this::reload);
        rootNode.addChild("interact").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Trigger player interaction")
            .senderCaller(this::interact);
        rootNode.addChild("testReturn").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Test complete ingredient return")
            .senderCaller(this::testReturn);
        rootNode.addChild("enabled").arguments("[true|false]")
            .completers(CommandArgCompleter.BOOLEAN)
            .description("Enable or disable Maypole")
            .senderCaller(this::enabled);
        rootNode.addChild("reward").denyTabCompletion()
            .description("Give rewards to all players")
            .senderCaller(this::reward);
        CommandNode collectibles = rootNode.addChild("collectibles")
            .description("Collectible subcommands");
        collectibles.addChild("list").arguments("<player>")
            .completers(PlayerCache.NAME_COMPLETER)
            .description("List collectibles")
            .senderCaller(this::collectiblesList);
        collectibles.addChild("give").arguments("<player> <collectible>")
            .completers(PlayerCache.NAME_COMPLETER,
                        CommandArgCompleter.enumLowerList(Collectible.class))
            .description("Unlock collectible")
            .senderCaller(this::collectiblesGive);
        collectibles.addChild("all").arguments("<player>")
            .completers(PlayerCache.NAME_COMPLETER)
            .description("Unlock all collectibles")
            .senderCaller(this::collectiblesAll);
        collectibles.addChild("clear").arguments("<player>")
            .completers(PlayerCache.NAME_COMPLETER)
            .description("Clear collectibles")
            .senderCaller(this::collectiblesClear);
        collectibles.addChild("reset").arguments("<player>")
            .completers(PlayerCache.NAME_COMPLETER)
            .description("Reset collectibles")
            .senderCaller(this::collectiblesReset);
        collectibles.addChild("randomize").arguments("<player>")
            .completers(PlayerCache.NAME_COMPLETER)
            .description("Randomize collectibles")
            .senderCaller(this::collectiblesRandomize);
        collectibles.addChild("message").arguments("<player> <collectible>")
            .completers(CommandArgCompleter.NULL,
                        CommandArgCompleter.enumLowerList(Collectible.class))
            .description("Send collectible message")
            .senderCaller(this::collectiblesMessage);
        CommandNode book = rootNode.addChild("book")
            .description("Book related subcommands");
        book.addChild("give").arguments("<player>")
            .description("Give the Maypole book")
            .completers(CommandArgCompleter.NULL)
            .senderCaller(this::bookGive);
        CommandNode pole = rootNode.addChild("pole")
            .description("Pole related subcommands");
        pole.addChild("set").denyTabCompletion()
            .description("Set pole at current location")
            .playerCaller(this::poleSet);
        pole.addChild("build").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Add a skull to the pole")
            .senderCaller(this::poleBuild);
    }

    private void reload(CommandSender sender) {
        plugin.loadTag();
        plugin.loadHighscore();
        plugin.sessions.reload();
        sender.sendMessage("Runtime data reloaded");
    }

    private boolean interact(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        plugin.interact(target);
        sender.sendMessage(text("Interaction triggered for " + target.getName(), AQUA));
        return true;
    }

    private boolean testReturn(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        sender.sendMessage(text("Triggering complete return for " + target.getName(), AQUA));
        plugin.playerReturns(target);
        return true;
    }

    private boolean enabled(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (plugin.isMaypoleEnabled()) {
                sender.sendMessage(text("Maypole is enabled", GREEN));
            } else {
                sender.sendMessage(text("Maypole is disabled", RED));
            }
            return true;
        }
        if (args.length != 1) return false;
        final boolean value = CommandArgCompleter.requireBoolean(args[0]);
        final SQLSetting newRow = new SQLSetting("enabled", "" + value);
        Database.getDatabase().saveAsync(newRow, result -> {
                if (result == 0) {
                    sender.sendMessage(text("Something went wrong while saving to database, see console: " + result, RED));
                    return;
                }
                Connect.get().broadcastMessage("Maypole", "ReloadSettings");
                plugin.loadSettings();
                sender.sendMessage(textOfChildren(text("Maypole enabled: ", AQUA),
                                                  text("" + value, value ? GREEN : RED)));
            });
        return true;
    }

    private void reward(CommandSender sender) {
        Map<UUID, Integer> collectibles = new HashMap<>();
        Map<UUID, Integer> completions = new HashMap<>();
        List<SQLPlayer> list = database().find(SQLPlayer.class).findList();
        for (SQLPlayer row : list) {
            if (row.getCollectibles() == 0) continue;
            collectibles.put(row.getUuid(), row.getCollectibles());
            completions.put(row.getUuid(), row.getCompletions());
        }
        int trophies = Highscore.reward(collectibles,
                                        "maypole",
                                        TrophyCategory.MAYPOLE,
                                        textOfChildren(plugin.TITLE,
                                                       text(" " + MaypolePlugin.YEAR, MaypolePlugin.MAYPOLE_BLUE)),
                                        hi -> {
                                            int collected = collectibles.get(hi.uuid);
                                            int completed = completions.get(hi.uuid);
                                            return "You collected "
                                                + collected + " Maypole ingredient" + (collected == 1 ? "" : "s")
                                                + (completed > 0
                                                   ? (" and completed "
                                                      + completed + " collection" + (completed == 1 ? "" : "s"))
                                                   : "");
                                        });
        sender.sendMessage(text("Rewarded " + trophies + " players with trophies", AQUA));
        com.winthier.maypole.sql.Highscore.list(highscores -> {
                int maybirds = 0;
                List<String> names = new ArrayList<>();
                for (int i = 0; i < 10; i += 1) {
                    if (i >= highscores.size()) break;
                    var hi = highscores.get(i);
                    if (hi.getRow().getCollectibles() == 0) break;
                    String name = hi.name();
                    plugin.serverCommand("titles unlockset " + name + " Maybird");
                    names.add(name);
                    maybirds += 1;
                }
                sender.sendMessage(text("Rewarded " + maybirds + " players with Maybird: " + names, AQUA));
            });
    }

    private boolean collectiblesList(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        plugin.sessions.apply(target.uuid, session -> {
                sender.sendMessage(text(target.name + " Maypole progress", YELLOW));
                sender.sendMessage(textOfChildren(text("Completions: ", GRAY), text(session.getCompletions(), AQUA)));
                sender.sendMessage(textOfChildren(text("Collectibles: ", GRAY), text(session.getCollectibles(), AQUA)));
                for (Collectible it : Collectible.values()) {
                    boolean has = session.has(it);
                    sender.sendMessage(textOfChildren(text(it + ": ", GRAY),
                                                      text(has, has ? GREEN : RED),
                                                      text(", ", DARK_GRAY),
                                                      text("" + session.getAction(it), AQUA)));
                }
            });
        return true;
    }

    private boolean collectiblesGive(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        Collectible collectible = Collectible.require(args[1]);
        plugin.sessions.apply(target.uuid, session -> {
                session.give(collectible);
                sender.sendMessage(text(collectible + " unlocked for " + target.name, AQUA));
                plugin.loadHighscore();
            });
        return true;
    }

    private boolean collectiblesAll(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        plugin.sessions.apply(target.uuid, session -> {
                int count = 0;
                for (Collectible it : Collectible.values()) {
                    if (!session.has(it)) {
                        session.give(it);
                        count += 1;
                    }
                }
                sender.sendMessage(text(count + " collectibles unlocked for " + target.name, AQUA));
                plugin.loadHighscore();
            });
        return true;
    }

    private boolean collectiblesClear(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        plugin.sessions.apply(target.uuid, session -> {
                session.clearCollection();
                sender.sendMessage(text("Collection of " + target.name + " cleared", AQUA));
            });
        return true;
    }

    private boolean collectiblesReset(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        plugin.sessions.apply(target.uuid, session -> {
                session.resetCollection();
                sender.sendMessage(text("Collection of " + target.name + " reset", AQUA));
            });
        return true;
    }

    private boolean collectiblesRandomize(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        plugin.sessions.apply(target.uuid, session -> {
                session.randomizeCollection();
                sender.sendMessage(text("Collection of " + target.name + " randomized", AQUA));
            });
        return true;
    }

    private boolean collectiblesMessage(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        Collectible collectible;
        try {
            collectible = Collectible.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Collectible not found: " + args[1]);
        }
        sender.sendMessage(text("Sending unlock message to " + target.getName(), AQUA));
        plugin.sendUnlockMessage(target, collectible);
        return true;
    }

    private boolean bookGive(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        plugin.giveBook(target);
        sender.sendMessage(text("Book given to " + target.getName(), AQUA));
        return true;
    }

    private void poleSet(Player player) {
        var loc = player.getLocation();
        plugin.tag.pole.world = loc.getWorld().getName();
        plugin.tag.pole.x = loc.getBlockX();
        plugin.tag.pole.y = loc.getBlockY();
        plugin.tag.pole.z = loc.getBlockZ();
        plugin.saveTag();
        player.sendMessage(text("Pole is now at " + plugin.tag.pole, AQUA));
    }

    private boolean poleBuild(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        plugin.buildMaypole(target);
        sender.sendMessage(text("Head of " + target.getName() + " added to Maypole", AQUA));
        return true;
    }
}
