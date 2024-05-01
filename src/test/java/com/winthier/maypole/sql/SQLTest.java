package com.winthier.maypole.sql;

import com.winthier.sql.SQLDatabase;
import org.junit.Test;

public final class SQLTest {
    @Test
    public void test() {
        for (var tableClass : Database.getDatabaseClasses()) {
            System.out.println(SQLDatabase.testTableCreation(tableClass));
        }
    }
}
