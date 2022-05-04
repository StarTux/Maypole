package com.winthier.maypole.sql;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data @Table(name = "players")
public final class SQLPlayer {
    @Id
    protected Integer id;
    @Column(nullable = false, unique = true)
    protected UUID uuid;
    @Column(nullable = false)
    protected int collectibles;
    @Column(nullable = false)
    protected int completions;
    @Column(nullable = false)
    protected boolean hasBook;

    public SQLPlayer() { }

    public SQLPlayer(final UUID uuid) {
        this.uuid = uuid;
    }

    public boolean doesHaveBook() {
        return hasBook;
    }
}
