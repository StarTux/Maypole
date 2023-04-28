package com.winthier.maypole.sql;

import com.cavetale.core.playercache.PlayerCache;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Value;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;

@Value
public final class Highscore {
    public final int placement;
    public final SQLPlayer row;

    public String name() {
        return PlayerCache.nameForUuid(row.uuid);
    }

    public Component displayName() {
        Player player = Bukkit.getPlayer(row.uuid);
        return player != null
            ? player.displayName()
            : text(name());
    }

    public static void list(Consumer<List<Highscore>> callback) {
        Database.database.find(SQLPlayer.class)
            .orderByDescending("collectibles")
            .findListAsync(ls -> {
                    List<Highscore> list = new ArrayList<>(ls.size());
                    int lastScore = -1;
                    int nextPlacement = 0;
                    for (SQLPlayer row : ls) {
                        if (lastScore != row.getCollectibles()) {
                            lastScore = row.getCollectibles();
                            nextPlacement += 1;
                        }
                        list.add(new Highscore(nextPlacement, row));
                    }
                    callback.accept(list);
                });
    }
}
