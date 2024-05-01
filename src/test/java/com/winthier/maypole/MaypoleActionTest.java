package com.winthier.maypole;

import java.util.EnumSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class MaypoleActionTest {
    @Test
    public void test() {
        final Set<MaypoleAction> unusedActions = EnumSet.allOf(MaypoleAction.class);
        unusedActions.remove(MaypoleAction.NONE);
        for (Collectible collectible : Collectible.values()) {
            unusedActions.removeAll(collectible.actions);
        }
        Assert.assertTrue("Unused MaypoleActions: " + unusedActions, unusedActions.isEmpty());
    }
}
