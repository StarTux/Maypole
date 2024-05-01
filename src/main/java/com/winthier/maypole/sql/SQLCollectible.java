package com.winthier.maypole.sql;

import com.winthier.maypole.Collectible;
import com.winthier.maypole.MaypoleAction;
import com.winthier.sql.SQLRow;
import com.winthier.sql.SQLRow.Name;
import com.winthier.sql.SQLRow.NotNull;
import com.winthier.sql.SQLRow.UniqueKey;
import java.util.UUID;
import lombok.Data;

@Data
@NotNull
@Name("collections")
@UniqueKey({"player", "item"})
public final class SQLCollectible implements SQLRow {
    @Id private Integer id;
    private UUID player;
    @VarChar(31) private String item;
    @VarChar(255) private String action;
    private boolean has;
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
