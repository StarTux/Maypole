package com.winthier.maypole.session;

import com.winthier.maypole.Collectible;
import com.winthier.maypole.sql.SQLCollectible;
import com.winthier.maypole.sql.SQLPlayer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static com.winthier.maypole.sql.Database.database;

@Getter @RequiredArgsConstructor
public final class Session {
    public final UUID uuid;
    protected boolean enabled;
    protected List<Consumer<Session>> enableCallbacks = new ArrayList<>();
    protected SQLPlayer playerRow;
    protected final Map<Collectible, SQLCollectible> collectibleRows = new EnumMap<>(Collectible.class);

    public void enable() {
        database().insertIgnoreAsync(new SQLPlayer(uuid), null);
        List<SQLCollectible> list = new ArrayList<>();
        for (Collectible it : Collectible.values()) {
            list.add(new SQLCollectible(uuid, it));
        }
        database().insertIgnoreAsync(list, null);
        database().find(SQLPlayer.class).findUniqueAsync(row -> playerRow = row);
        database().find(SQLCollectible.class).findListAsync(rows -> {
                for (SQLCollectible row : rows) {
                    collectibleRows.put(row.getCollectible(), row);
                }
                onEnable();
            });
    }

    private void onEnable() {
        enabled = true;
        for (Consumer<Session> it : enableCallbacks) {
            it.accept(this);
        }
        enableCallbacks.clear();
        enableCallbacks = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public int getCollectibles() {
        return playerRow.getCollectibles();
    }

    public int getCompletions() {
        return playerRow.getCompletions();
    }

    public void setCompletions(int value) {
        if (playerRow.getCompletions() == value) return;
        playerRow.setCompletions(value);
        database().updateAsync(playerRow, null, "completions");
    }

    public boolean has(Collectible collectible) {
        return collectibleRows.get(collectible).doesHave();
    }

    public void give(Collectible collectible) {
        SQLCollectible row = collectibleRows.get(collectible);
        if (row.doesHave()) return;
        row.setHas(true);
        database().updateAsync(row, null, "has");
        playerRow.setCollectibles(playerRow.getCollectibles() + 1);
        database().updateAsync(playerRow, null, "collectibles");
    }

    public void clearCollection() {
        for (Collectible it : Collectible.values()) {
            SQLCollectible row = collectibleRows.get(it);
            if (!row.doesHave()) continue;
            row.setHas(false);
            database().updateAsync(row, null, "has");
        }
    }

    public boolean hasBook() {
        return playerRow.doesHaveBook();
    }

    public void setHasBook(boolean value) {
        if (playerRow.doesHaveBook() == value) return;
        playerRow.setHasBook(value);
        database().updateAsync(playerRow, null, "hasBook");
    }
}
