package com.winthier.maypole.sql;

import com.winthier.maypole.MaypolePlugin;
import com.winthier.sql.SQLDatabase;
import com.winthier.sql.SQLRow;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;

public final class Database {
    @Getter protected static SQLDatabase database;

    public static List<Class<? extends SQLRow>> getDatabaseClasses() {
        return List.of(SQLPlayer.class,
                       SQLCollectible.class,
                       SQLSetting.class);
    }

    public static void enable(MaypolePlugin plugin) {
        database = new SQLDatabase(plugin);
        database.registerTables(getDatabaseClasses());
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
