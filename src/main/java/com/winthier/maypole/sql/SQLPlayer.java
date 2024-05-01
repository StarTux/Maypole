package com.winthier.maypole.sql;

import com.winthier.sql.SQLRow;
import com.winthier.sql.SQLRow.Name;
import com.winthier.sql.SQLRow.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
@NotNull
@Name("players")
public final class SQLPlayer implements SQLRow {
    @Id private Integer id;
    @Unique private UUID uuid;
    private int collectibles;
    private int completions;
    private boolean hasBook;

    public SQLPlayer() { }

    public SQLPlayer(final UUID uuid) {
        this.uuid = uuid;
    }

    public boolean doesHaveBook() {
        return hasBook;
    }
}
