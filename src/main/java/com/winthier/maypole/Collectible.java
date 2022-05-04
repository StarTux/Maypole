package com.winthier.maypole;

import com.cavetale.core.command.CommandWarn;
import com.cavetale.mytems.Mytems;

public enum Collectible {
    LUCID_LILY(Mytems.LUCID_LILY),
    PINE_CONE(Mytems.PINE_CONE),
    ORANGE_ONION(Mytems.ORANGE_ONION),
    MISTY_MOREL(Mytems.MISTY_MOREL),
    RED_ROSE(Mytems.RED_ROSE),
    FROST_FLOWER(Mytems.FROST_FLOWER),
    HEAT_ROOT(Mytems.HEAT_ROOT),
    CACTUS_BLOSSOM(Mytems.CACTUS_BLOSSOM),
    PIPE_WEED(Mytems.PIPE_WEED),
    KINGS_PUMPKIN(Mytems.KINGS_PUMPKIN),
    SPARK_SEED(Mytems.SPARK_SEED),
    OASIS_WATER(Mytems.OASIS_WATER),
    CLAMSHELL(Mytems.CLAMSHELL),
    FROZEN_AMBER(Mytems.FROZEN_AMBER),
    CLUMP_OF_MOSS(Mytems.CLUMP_OF_MOSS),
    FIRE_AMANITA(Mytems.FIRE_AMANITA);

    public final String key;
    public final String nice;
    public final Mytems mytems;

    Collectible(final Mytems mytems) {
        this.key = name().toLowerCase();
        String[] toks = name().split("_");
        for (int i = 0; i < toks.length; i += 1) {
            toks[i] = toks[i].substring(0, 1) + toks[i].substring(1).toLowerCase();
        }
        this.nice = String.join(" ", toks);
        this.mytems = mytems;
    }

    public static Collectible of(String in) {
        try {
            return Collectible.valueOf(in.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public static Collectible require(String in) {
        try {
            return valueOf(in.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Collectible not found: " + in);
        }
    }
}
