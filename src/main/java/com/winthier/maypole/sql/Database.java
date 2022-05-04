package com.winthier.maypole.sql;

import com.winthier.maypole.MaypolePlugin;
import com.winthier.sql.SQLDatabase;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class Database {
    protected static SQLDatabase database;

    public static void enable(MaypolePlugin plugin) {
        database = new SQLDatabase(plugin);
        database.registerTables(SQLPlayer.class, SQLCollectible.class);
        if (!database.createAllTables()) {
            throw new IllegalArgumentException("Database creation failed");
        }
    }

    public static void disable(MaypolePlugin plugin) {
        database.waitForAsyncTask();
        database.close();
        database = null;
    }

    public static void collectibles(UUID player, Consumer<List<SQLCollectible>> callback) {
        database.find(SQLCollectible.class)
            .eq("player", player)
            .findListAsync(callback);
    }

    public static SQLDatabase database() {
        return database;
    }

    private Database() { }
}
