package com.winthier.maypole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public final class CollectibleTest {
    @Test
    public void test() {
        final Map<Collectible, Integer> counts = new EnumMap<>(Collectible.class);
        for (Collectible collectible : Collectible.values()) {
            counts.put(collectible, collectible.actions.size());
        }
        List<Collectible> list = new ArrayList<>(counts.keySet());
        Collections.sort(list, (a, b) -> Integer.compare(counts.get(a), counts.get(b)));
        for (Collectible collectible : list) {
            System.out.println("" + counts.get(collectible) + " " + collectible);
        }
    }
}
