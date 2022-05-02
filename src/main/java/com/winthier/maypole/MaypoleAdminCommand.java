package com.winthier.maypole;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandNode;
import com.cavetale.core.command.CommandWarn;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class MaypoleAdminCommand extends AbstractCommand<MaypolePlugin> {
    protected MaypoleAdminCommand(final MaypolePlugin plugin) {
        super(plugin, "maypoleadmin");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("reload").denyTabCompletion()
            .description("Reload configurations")
            .senderCaller(this::reload);
        rootNode.addChild("interact").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Trigger player interaction")
            .senderCaller(this::interact);
        rootNode.addChild("testReturn").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Test complete ingredient return")
            .senderCaller(this::testReturn);
        CommandNode collectibles = rootNode.addChild("collectibles")
            .description("Collectible subcommands");
        collectibles.addChild("list").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("List collectibles")
            .senderCaller(this::collectiblesList);
        collectibles.addChild("list").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("List collectibles")
            .senderCaller(this::collectiblesList);
        collectibles.addChild("unlock").arguments("<player> <collectible>")
            .completers(CommandArgCompleter.NULL,
                        CommandArgCompleter.enumLowerList(Collectible.class))
            .description("Unlock collectible")
            .senderCaller(this::collectiblesUnlock);
        collectibles.addChild("all").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Unlock all collectibles")
            .senderCaller(this::collectiblesAll);
        collectibles.addChild("clear").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Clear collectibles")
            .senderCaller(this::collectiblesClear);
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
        pole.addChild("build").arguments("<player>")
            .completers(CommandArgCompleter.NULL)
            .description("Add a skull to the pole")
            .senderCaller(this::poleBuild);
    }

    private void reload(CommandSender sender) {
        plugin.reloadAll();
        sender.sendMessage("Maypole configurations reloaded");
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

    private boolean collectiblesList(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        ConfigurationSection prog = plugin.getPlayerProgress(target);
        sender.sendMessage(target.getName() + " Maypole progress");
        for (String key: prog.getKeys(false)) {
            sender.sendMessage(text(key + ": " + prog.get(key), AQUA));
        }
        return true;
    }

    private boolean collectiblesUnlock(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        Collectible collectible;
        try {
            collectible = Collectible.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Collectible not found: " + args[1]);
        }
        plugin.getPlayerProgress(target).set(collectible.key, true);
        plugin.savePlayerProgress();
        sender.sendMessage(text(collectible + " unlocked for " + target.getName(), AQUA));
        return true;
    }

    private boolean collectiblesAll(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        for (Collectible collectible: Collectible.values()) {
            plugin.getPlayerProgress(target).set(collectible.key, true);
        }
        plugin.savePlayerProgress();
        sender.sendMessage(text(target.getName() + " was given all collectibles", AQUA));
        return true;
    }

    private boolean collectiblesClear(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        for (Collectible collectible: Collectible.values()) {
            plugin.getPlayerProgress(target).set(collectible.key, false);
        }
        plugin.savePlayerProgress();
        sender.sendMessage(text(target.getName() + " was cleared of all collectibles", AQUA));
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

    private boolean poleBuild(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        plugin.buildMaypole(target);
        sender.sendMessage(text("Head of " + target.getName() + " added to Maypole", AQUA));
        return true;
    }
}
