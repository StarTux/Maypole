package com.winthier.maypole;

import com.cavetale.core.command.CommandWarn;
import com.cavetale.mytems.Mytems;
import java.util.EnumSet;
import java.util.List;
import static com.winthier.maypole.MaypoleAction.*;

@SuppressWarnings("LineLength")
public enum Collectible {
    LUCID_LILY(Mytems.LUCID_LILY, PICK_SWAMP_SEAGRASS, PICK_SWAMP_LILY) {
        @Override public List<String> getBookPages() {
            return List.of("Most commonly associated with witches who grow them in the ponds around their huts. Nobody knows what draws them to this flower, but I bet it's the olfactory appeal, however... repulsive you and I may perceive it.",
                           "Its potential for an effective deodorant must be enormous. However, we only care about its visual appeal, which is just perfect for the Maypole!");
        }
    },
    PINE_CONE(Mytems.PINE_CONE, PICK_TAIGA_SPRUCE_LEAVES, PICK_PODZOL, PICK_BERRY_BUSH) {
        @Override public List<String> getBookPages() {
            return List.of("This funny looking cone falls of the fir tree to carry its seed far away. Some people love to dry them and build small figurines with them. You may see where this is going.");
        }
    },
    ORANGE_ONION(Mytems.ORANGE_ONION, PICK_ORANGE_TULIP, PICK_RED_TULIP, PICK_WHITE_TULIP) {
        @Override public List<String> getBookPages() {
            return List.of("There are not many things in the world that can bring me to tears. Cut onions surely are one of them, and the orange kind is no exception.");
        }
    },
    MISTY_MOREL(Mytems.MISTY_MOREL, PICK_SWAMP_BROWN_MUSHROOM, PICK_ISLAND_BROWN_MUSHROOM) {
        @Override public List<String> getBookPages() {
            return List.of("Large crowds comb through the woods every year to find this delicacy. They are usually easy to find in shady places.");
        }
    },
    RED_ROSE(Mytems.RED_ROSE, PICK_ROSE_BUSH, PICK_POPPY) {
        @Override public List<String> getBookPages() {
            return List.of("It is a common misconception that they removed the red rose from the game in favor of the common poppy.");
        }
    },
    FROST_FLOWER(Mytems.FROST_FLOWER, PICK_COLD_TALL_GRASS, PICK_SNOW) {
        @Override public List<String> getBookPages() {
            return List.of("Oh boy, finally we are getting to the good stuff. The frost flower is incredibly tricky to cultivate in your garden.");
        }
    },
    HEAT_ROOT(Mytems.HEAT_ROOT, PICK_DESERT_DEAD_BUSH, PICK_MESA_DEAD_BUSH, PICK_NETHER_SHROOMLIGHT) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pan, into the fire! This subterranean growth is searing to the touch and should only be harvested while wearing gloves.");
        }
    },
    CACTUS_BLOSSOM(Mytems.CACTUS_BLOSSOM, PICK_DESERT_CACTUS, PICK_MESA_CACTUS) {
        @Override public List<String> getBookPages() {
            return List.of("If it seems at this point that we are sending you out just to get your hands burned and prickled, you are mistaken.");
        }
    },
    PIPE_WEED(Mytems.PIPE_WEED, PICK_JUNGLE_FERN, PICK_JUNGLE_LEAVES, PICK_JUNGLE_VINES) {
        @Override public List<String> getBookPages() {
            return List.of("Oh, the blissful nights I spent in front of a hot chimney, thanks to Old Toby! I promise you that I will not smoke most of it. It's important for the Maypole festivities.");
        }
    },
    KINGS_PUMPKIN(Mytems.KINGS_PUMPKIN, PICK_PUMPKIN, PICK_NETHER_WEEPING_VINE) {
        @Override public List<String> getBookPages() {
            return List.of("There is a special kind of distinguished pumpkin, identifiable by its crown-shaped leaves on top, hence the name.");
        }
    },
    SPARK_SEED(Mytems.SPARK_SEED, BUCKET_SURFACE_LAVA, BUCKET_NETHER_LAVA) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pan, into the... I made that joke already, didn't I? Anyway, this seed is equally hot, if not moreso than the Heat Root (see above). Getting your hands dirty (or burned) is something you may not be able to avoid this time around...");
        }
    },
    OASIS_WATER(Mytems.OASIS_WATER, BUCKET_DESERT_WATER, PICK_DRIPSTONE) {
        @Override public List<String> getBookPages() {
            return List.of("Time for a break! We need that water to keep the more delicate exhibits moist, and it doesn't stay fresh for very long. so someone will have to go and fetch some.");
        }
    },
    CLAMSHELL(Mytems.CLAMSHELL, PICK_CORAL, BUCKET_BEACH_WATER) {
        @Override public List<String> getBookPages() {
            return List.of("Susie sells seashells by the seashore. These clams are the remains of shellfish and once they're abandoned, they sink to the ground.");
        }
    },
    FROZEN_AMBER(Mytems.FROZEN_AMBER, PICK_BLUE_OR_PACKED_ICE, PICK_COLD_COARSE_DIRT) {
        @Override public List<String> getBookPages() {
            return List.of("You may have heard that mosquitoes sometimes get frozen in Amber, to be conserved for millions of years.");
        }
    },
    CLUMP_OF_MOSS(Mytems.CLUMP_OF_MOSS, PICK_MOSSY_STONE, PICK_GLOW_LICHEN, PICK_MOSS) {
        @Override public List<String> getBookPages() {
            return List.of("Moss loves dark and moist spaces, so the woods are bound to be lousy with them.");
        }
    },
    FIRE_AMANITA(Mytems.FIRE_AMANITA, KILL_NETHER_PIGLIN, PICK_NETHER_RED_MUSHROOM) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pa-... never mind. This is by far the most dangerous place we are sending you to, so do be careful.");
        }
    };

    public final String key;
    public final String nice;
    public final Mytems mytems;
    public final EnumSet<MaypoleAction> actions;

    Collectible(final Mytems mytems, final MaypoleAction action, final MaypoleAction... actions) {
        this.key = name().toLowerCase();
        String[] toks = name().split("_");
        for (int i = 0; i < toks.length; i += 1) {
            toks[i] = toks[i].substring(0, 1) + toks[i].substring(1).toLowerCase();
        }
        this.nice = String.join(" ", toks);
        this.mytems = mytems;
        this.actions = EnumSet.of(action, actions);
    }

    public abstract List<String> getBookPages();

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

    protected static void validate(MaypolePlugin plugin) {
        EnumSet<MaypoleAction> unusedActions = EnumSet.allOf(MaypoleAction.class);
        unusedActions.remove(NONE);
        for (Collectible it : Collectible.values()) {
            if (it.actions.size() < 2) {
                plugin.getLogger().warning("[Collectible] " + it + ": Small action count: " + it.actions.size());
            }
            unusedActions.removeAll(it.actions);
        }
        if (!unusedActions.isEmpty()) {
            plugin.getLogger().warning("[Collectible] Unused MaypoleActions: " + unusedActions);
        }
    }
}
