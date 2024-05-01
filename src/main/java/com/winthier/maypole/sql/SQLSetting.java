package com.winthier.maypole.sql;

import com.winthier.sql.SQLRow;
import com.winthier.sql.SQLRow.Name;
import com.winthier.sql.SQLRow.NotNull;
import java.util.Date;
import lombok.Data;

@Data
@NotNull
@Name("settings")
public final class SQLSetting implements SQLRow {
    @Id private Integer id;
    @Unique @VarChar(31) String name;
    @VarChar(255) String value;
    private Date updated;

    public SQLSetting() { }

    public SQLSetting(final String name, final String value) {
        this.name = name;
        this.value = value;
        this.updated = new Date();
    }
}
