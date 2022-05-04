package com.winthier.maypole.sql;

import com.winthier.maypole.Collectible;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Table(name = "collections",
       uniqueConstraints = @UniqueConstraint(name = "player_item", columnNames = { "player", "item" }))
public final class SQLCollectible {
    @Id
    protected Integer id;
    @Column(nullable = false)
    protected UUID player;
    @Column(nullable = false, length = 31)
    protected String item;
    @Column(nullable = false)
    protected boolean has;

    public SQLCollectible() { }

    public SQLCollectible(final UUID player, final Collectible collectible) {
        this.player = player;
        this.item = collectible.name().toLowerCase();
    }

    public Collectible getCollectible() {
        try {
            return Collectible.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public boolean doesHave() {
        return has;
    }
}
