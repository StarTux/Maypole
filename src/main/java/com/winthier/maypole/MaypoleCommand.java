package com.winthier.maypole;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.core.font.Unicode;
import com.cavetale.mytems.item.font.Glyph;
import com.winthier.maypole.session.Session;
import com.winthier.maypole.sql.Highscore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class MaypoleCommand extends AbstractCommand<MaypolePlugin> {
    protected MaypoleCommand(final MaypolePlugin plugin) {
        super(plugin, "maypole");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("book").denyTabCompletion()
            .description("Open book")
            .playerCaller(this::book);
        rootNode.addChild("hi").denyTabCompletion()
            .description("View highscore")
            .senderCaller(this::highscore);
    }

    private void book(Player player) {
        if (!plugin.tag.enabled) throw new CommandWarn("It is not Maypole season yet!");
        if (!plugin.openBook(player)) {
            throw new CommandWarn("Please try again later");
        }
    }

    protected void highscore(CommandSender sender) {
        sender.sendMessage(join(separator(space()), plugin.TITLE, text("Highscore", plugin.MAYPOLE_BLUE)));
        if (sender instanceof Player player) {
            Session session = plugin.sessions.get(player);
            if (session != null && session.isEnabled()) {
            sender.sendMessage(join(noSeparators(),
                                    text("Your progress", GRAY),
                                    space(),
                                    text(Unicode.tiny("collected"), GRAY),
                                    text(session.getCollectibles(), AQUA),
                                    space(),
                                    text(Unicode.tiny("completed"), GRAY),
                                    text(session.getCompletions(), AQUA)));
            }
        }
        for (int i = 0; i < 10; i += 1) {
            if (i >= plugin.highscore.size()) break;
            Highscore hi = plugin.highscore.get(i);
            sender.sendMessage(join(noSeparators(),
                                    Glyph.toComponent("" + hi.placement),
                                    space(),
                                    text(Unicode.tiny("collected")),
                                    text(hi.row.getCollectibles(), GOLD),
                                    space(),
                                    text(Unicode.tiny("completed")),
                                    text(hi.row.getCompletions(), GOLD),
                                    space(),
                                    hi.displayName()));
        }
    }
}
