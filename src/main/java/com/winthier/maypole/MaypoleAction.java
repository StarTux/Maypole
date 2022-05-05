package com.winthier.maypole;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

@RequiredArgsConstructor @SuppressWarnings("LineLength")
public enum MaypoleAction {
    NONE(Type.INVALID, Material.AIR) {
        @Override public List<String> getBookPages() {
            return List.of();
        }
    },
    PICK_SWAMP_SEAGRASS(Type.BLOCK_BREAK, Material.SEAGRASS) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("SWAMP");
        }

        @Override public List<String> getBookPages() {
            return List.of("It should bloom around this time of year in moist climates. If someone were to inspect some seagrass in the swamplands, they would find a specimen in no time, guaranteed!");
        }
    },
    PICK_ORANGE_TULIP(Type.BLOCK_BREAK, Material.ORANGE_TULIP) {
        @Override public List<String> getBookPages() {
            return List.of("What makes this one so special is that it grows the magnificent orange tulip. So one of those is bound to yield the onion, right? Just pick a few orange tulips and eventually it will emerge.");
        }
    },
    PICK_ROSE_BUSH(Type.BLOCK_BREAK, Material.ROSE_BUSH) {
        @Override public List<String> getBookPages() {
            return List.of("Needless to say that one cannot have a rose bush without one of these. Plowing through enough of them should allow you to collect the ingredient in a matter of minutes.");
        }
    },
    PICK_COLD_TALL_GRASS(Type.BLOCK_BREAK, Set.of(Material.GRASS, Material.TALL_GRASS), null) {
        @Override public List<String> getBookPages() {
            return List.of("This one requires devilishly low temperatures to blossom. Even the slightest warm breeze could ruin months of care.",
                           "However, you only need to take care of the easy part. Find its seeds in the taller grass of a snowy area, and you're good to go.");
        }
    },
    PICK_JUNGLE_FERN(Type.BLOCK_BREAK, Material.FERN) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("JUNGLE");
        }

        @Override public List<String> getBookPages() {
            return List.of("It grows between the huge jungle trees. Just keep your eye out for a fern looking plant. Hurry up, please, as my personal stash is almost empty...");
        }
    },
    PICK_DESERT_DEAD_BUSH(Type.BLOCK_BREAK, Material.DEAD_BUSH) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("DESERT");
        }

        @Override public List<String> getBookPages() {
            return List.of("The whole plant can usually be seen all over the desert, but now that spring is here, only the roots are still alive.",
                           "Tear a couple of dead bushes out of the desert floor, and one is bound to surface. Carrying of burn ointment is strongly advised.");
        }
    },
    PICK_CACTUS(Type.BLOCK_BREAK, Material.CACTUS) {
        @Override public boolean checkBlock(Block block) {
            return block.getRelative(0, -1, 0).getType() == Material.SAND;
        }

        @Override public List<String> getBookPages() {
            return List.of("Having said that, be careful when you break those cacti. In order to find a blossom that's intact, you will have to check out more than one.",
                           "I hope I don't need to remind you that pulling cactus spines out of your finger is way more painful than getting them in there.");
        }
    },
    PICK_PUMPKIN(Type.BLOCK_BREAK, Material.PUMPKIN) {
        @Override public List<String> getBookPages() {
            return List.of("You will have to inspect a couple of pumpkins to find one. So make sure not to walk past a pumpkin patch if you come across one.");
        }
    },
    PICK_CORAL(Type.BLOCK_BREAK, Tag.CORALS.getValues(), Set.of()) {
        @Override public List<String> getBookPages() {
            return List.of("The only place protected from various colorful predators are coral reefs.",
                           "Hammer on those for a bit and you will strike gold! Keep in mind that they have to be alive.");
        }
    },
    PICK_BLUE_OR_PACKED_ICE(Type.BLOCK_BREAK, Set.of(Material.BLUE_ICE, Material.PACKED_ICE), Set.of()) {
        @Override public List<String> getBookPages() {
            return List.of("We are taking you one step further, by finding one that's trapped in the blue perma-frost of an iceberg.",
                           "There's simply no better way to conserve prehistoric organic matter. Try mining some blue or packed ice until you find one of these gems.");
        }
    },
    PICK_TAIGA_SPRUCE_LEAVES(Type.BLOCK_BREAK, Material.SPRUCE_LEAVES) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("TAIGA");
        }

@Override public List<String> getBookPages() {
            return List.of("Only where to find a fresh specimen at this time of the year is the question. It's time to shake some pine tree leaves in a taiga biome and find out.");
        }
    },
    PICK_MOSSY_STONE(Type.BLOCK_BREAK,
                     Set.of(Material.MOSSY_COBBLESTONE,
                            Material.MOSSY_COBBLESTONE_SLAB,
                            Material.MOSSY_COBBLESTONE_STAIRS,
                            Material.MOSSY_COBBLESTONE_WALL,
                            Material.MOSSY_STONE_BRICKS,
                            Material.MOSSY_STONE_BRICK_SLAB,
                            Material.MOSSY_STONE_BRICK_STAIRS,
                            Material.MOSSY_STONE_BRICK_WALL),
                     Set.of()) {
        @Override public List<String> getBookPages() {
            return List.of("Try to scrape some off of mossy cobblestone. A handful should be enough for now.");
        }
    },
    PICK_SWAMP_BROWN_MUSHROOM(Type.BLOCK_BREAK, Material.BROWN_MUSHROOM) {
        @Override public List<String> getBookPages() {
            return List.of("However, there is one caveat: The one we are looking for grows in clear daylight.",
                           "It looks just like any other brown mushroom you can find in the swamps, but trust me. Once you sink your teeth into one of these, you will be spoiled forever! We promise to save as many as possible for the Maypole.");
        }
    },
    KILL_NETHER_PIGLIN(Type.ENTITY_KILL, EntityType.PIGLIN) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("This one is the favorite food of the common piglin. Nobody knows how they locate them, it's their trade secret. Some say they can smell it from 128 blocks distance while others claim they grow on them.",
                           "It is conceivable that this is merely a figure of speech, however. I'm afraid they're not going to be part with it willingly, so if they refuse your gold, I see only one other option.",
                           "The mighty Maypole is certainly worth this selfless sacrifice!");
        }
    },
    BUCKET_SURFACE_LAVA(Type.BUCKET_FILL, Material.LAVA) {
        @Override public boolean checkBlock(Block block) {
            return block.getY() > 48
                && block.getRelative(0, 1, 0).getLightFromSky() >= 8;
        }

        @Override public List<String> getBookPages() {
            return List.of("It only survives at the hottest places on earth, in direct sunlight. The most surefire way to find them (no pun intended) is to find a pool of surface lava.",
                           "Load some of it into a bucket and inspect it, carefully. You will know it when you see one. Try not to burn your eyebrows. Trust me, it takes forever to grow them back, not to mention the relentless ridicule from coworkers...");

        }
    },
    BUCKET_DESERT_WATER(Type.BUCKET_FILL, Material.WATER) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("DESERT");
        }

        @Override public List<String> getBookPages() {
            return List.of("Traveling to the desert could be argued to be a little less than pleasant... Anyway, this ingredient can only be found there, in natural water sources on the surface!");
        }
    },
    ;

    public final Type type;
    public final Set<Material> materials;
    public final Set<EntityType> entityTypes;

    MaypoleAction(final Type type, final Material material) {
        this(type, Set.of(material), Set.of());
    }

    MaypoleAction(final Type type, final EntityType entityType) {
        this(type, Set.of(), Set.of(entityType));
    }

    public abstract List<String> getBookPages();

    public boolean checkBlock(Block block) {
        return true;
    }

    public boolean isValid() {
        return type != Type.INVALID;
    }

    public enum Type {
        INVALID,
        BLOCK_BREAK,
        BUCKET_FILL,
        ENTITY_KILL;
    }
}
