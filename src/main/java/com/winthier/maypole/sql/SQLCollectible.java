package com.winthier.maypole.sql;

import com.winthier.maypole.Collectible;
import com.winthier.maypole.MaypoleAction;
import com.winthier.sql.SQLRow;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Table(name = "collections",
       uniqueConstraints = @UniqueConstraint(name = "player_item", columnNames = { "player", "item" }))
public final class SQLCollectible implements SQLRow {
    @Id
    protected Integer id;
    @Column(nullable = false)
    protected UUID player;
    @Column(nullable = false, length = 31)
    protected String item;
    @Column(nullable = false, length = 255)
    protected String action;
    @Column(nullable = false)
    protected boolean has;
    private transient MaypoleAction maypoleAction;

    public SQLCollectible() { }

    public SQLCollectible(final UUID player, final Collectible collectible) {
        this.player = player;
        this.item = collectible.name().toLowerCase();
        this.action = MaypoleAction.NONE.name().toLowerCase();
    }

    public Collectible getCollectible() {
        try {
            return Collectible.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public MaypoleAction getMaypoleAction() {
        if (maypoleAction == null) {
            try {
                maypoleAction = MaypoleAction.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException iae) {
                maypoleAction = MaypoleAction.NONE;
            }
        }
        return maypoleAction;
    }

    public void setMaypoleAction(MaypoleAction value) {
        this.maypoleAction = value;
        this.action = value.name().toLowerCase();
    }

    public boolean doesHave() {
        return has;
    }
}
