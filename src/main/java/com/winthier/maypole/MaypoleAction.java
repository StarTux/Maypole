package com.winthier.maypole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

@RequiredArgsConstructor @SuppressWarnings("LineLength")
public enum MaypoleAction {
    NONE(Type.INVALID, Material.AIR) {
        @Override public List<String> getBookPages() {
            return List.of();
        }
    },
    PICK_SWAMP_SEAGRASS(Type.BLOCK_BREAK, Material.SEAGRASS, Material.TALL_SEAGRASS) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("SWAMP");
        }

        @Override public List<String> getBookPages() {
            return List.of("It should bloom around this time of year in moist climates. If someone were to inspect some seagrass in the swamplands, they would find a specimen in no time, guaranteed!");
        }
    },
    PICK_SWAMP_LILY(Type.BLOCK_BREAK, Material.LILY_PAD) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("SWAMP");
        }

        @Override public List<String> getBookPages() {
            return List.of("It should bloom around this time of year in moist climates. If someone were to expect some lily pads in the swamplands, they would find a specimen in no time, guaranteed!");
        }
    },
    PICK_ORANGE_TULIP(Type.BLOCK_BREAK, Material.ORANGE_TULIP) {
        @Override public List<String> getBookPages() {
            return List.of("What makes this one so special is that it grows the magnificent orange tulip. So one of those is bound to yield it, right? Just pick a few orange tulips and eventually it will emerge.");
        }
    },
    PICK_RED_TULIP(Type.BLOCK_BREAK, Material.RED_TULIP) {
        @Override public List<String> getBookPages() {
            return List.of("The ones we are looking for are of the darker variety, almost red-ish. They grow into the red tulip, so just go pick some of those until you get lucky.");
        }
    },
    PICK_WHITE_TULIP(Type.BLOCK_BREAK, Material.WHITE_TULIP) {
        @Override public List<String> getBookPages() {
            return List.of("The ones we are looking for are particularly pale, almost white. Plant them and they will sprout into the magnificent white tulip. Go looking for some of those tulips in order to proceed.");
        }
    },
    PICK_ROSE_BUSH(Type.BLOCK_BREAK, Material.ROSE_BUSH) {
        @Override public List<String> getBookPages() {
            return List.of("Needless to say that one cannot have a rose bush without one of these. Plowing through enough of them should allow you to collect the ingredient in a matter of minutes.");
        }
    },
    PICK_POPPY(Type.BLOCK_BREAK, Material.POPPY) {
        @Override public List<String> getBookPages() {
            return List.of("However I firmly believe that some poppies are more than meets the eye. Picking some of them will reveal the truth.");
        }
    },
    PICK_COLD_TALL_GRASS(Type.BLOCK_BREAK, Material.GRASS, Material.TALL_GRASS) {
        @Override public boolean checkBlock(Block block) {
            return block.getTemperature() <= 0.2;
        }

        @Override public List<String> getBookPages() {
            return List.of("This one requires devilishly low temperatures to blossom. Even the slightest warm breeze could ruin months of care.",
                           "However, you only need to take care of the easy part. Find its seeds in the taller grass of a snowy area, and you're good to go.");
        }
    },
    PICK_SNOW(Type.BLOCK_BREAK, Material.SNOW) {
        @Override public List<String> getBookPages() {
            return List.of("This one requires devilishly low temperatures to blossom. Even the slightest warm breeze could ruin months of care.",
                           "Your best bet will be to dig up some snow, hoping to find a patch which conserved a pristine exemplar.");
        }
    },
    PICK_JUNGLE_FERN(Type.BLOCK_BREAK, Material.FERN, Material.LARGE_FERN) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("JUNGLE");
        }

        @Override public List<String> getBookPages() {
            return List.of("It grows between the huge jungle trees. Just keep your eye out for a fern looking plant. Hurry up, please, as my personal stash is almost empty...");
        }
    },
    PICK_JUNGLE_LEAVES(Type.BLOCK_BREAK, Material.JUNGLE_LEAVES) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("JUNGLE");
        }

        @Override public List<String> getBookPages() {
            return List.of("It grows on the huge jungle trees. Just climb up and rustle some of those leaves. Please note that we do not cover broken bones.");
        }
    },
    PICK_JUNGLE_VINES(Type.BLOCK_BREAK, Material.VINE) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("JUNGLE");
        }

        @Override public List<String> getBookPages() {
            return List.of("It grows on the huge jungle trees. Just find one with vines and remove them carefully. Bring your own shears.");
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
    PICK_MESA_DEAD_BUSH(Type.BLOCK_BREAK, Material.DEAD_BUSH) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("BADLANDS");
        }

        @Override public List<String> getBookPages() {
            return List.of("The whole plant can usually be seen all over the desert, but now that spring is here, one can only find them in the mesa.",
                           "Tear a couple of dead bushes out of the mesa floor, and one is bound to surface. Carrying of burn ointment is strongly advised.");
        }
    },
    PICK_NETHER_SHROOMLIGHT(Type.BLOCK_BREAK, Material.SHROOMLIGHT) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("Not many people know that it is what gives shroomlight its natural glow. It quickly withers away though.",
                           "Harvest some of these in the Nether, carefully with a hoe, and one of them will yield the object of our desire.");
        }
    },
    PICK_DESERT_CACTUS(Type.BLOCK_BREAK, Material.CACTUS) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("DESERT")
                && block.getRelative(0, -1, 0).getType() != Material.CACTUS;
        }

        @Override public List<String> getBookPages() {
            return List.of("Having said that, be careful when you break those desert cacti. In order to find a blossom that's intact, you will have to check out more than one.",
                           "They can only be found on the bottom block, right above the sand, so don't even bother breaking all three cactus blocks.",
                           "I hope I don't need to remind you that pulling cactus spines out of your finger is way more painful than getting them in there.");
        }
    },
    PICK_MESA_CACTUS(Type.BLOCK_BREAK, Material.CACTUS) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("BADLANDS")
                && block.getRelative(0, -1, 0).getType() != Material.CACTUS;
        }

        @Override public List<String> getBookPages() {
            return List.of("Having said that, be careful when you break those mesa cacti. In order to find a blossom that's intact, you will have to check out more than one.",
                           "They can only be found on the bottom block, right above the red sand, so don't even bother breaking all three cactus blocks.",
                           "I hope I don't need to remind you that pulling cactus spines out of your finger is way more painful than getting them in there.");
        }
    },
    PICK_PUMPKIN(Type.BLOCK_BREAK, Material.PUMPKIN) {
        @Override public List<String> getBookPages() {
            return List.of("You will have to inspect a couple of pumpkins to find one. So make sure not to walk past a pumpkin patch if you come across one.");
        }
    },
    PICK_NETHER_WEEPING_VINE(Type.BLOCK_BREAK, Material.WEEPING_VINES_PLANT, Material.WEEPING_VINES) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("The trick is to find the pretty small ones, still attached to the vines. Some of the weeping vines you encounter in the Nether might still carry them.");
        }
    },
    PICK_CORAL(Type.BLOCK_BREAK, List.of(Tag.CORAL_BLOCKS, Tag.CORAL_PLANTS, Tag.CORALS, Tag.WALL_CORALS)) {
        @Override public List<String> getBookPages() {
            return List.of("The only place protected from various colorful predators are coral reefs.",
                           "Hammer on those for a bit and you will strike gold! Keep in mind that they have to be alive.");
        }
    },
    BUCKET_BEACH_WATER(Type.BUCKET_FILL, Material.WATER) {
        @Override public boolean checkBlock(Block block) {
            String biome = block.getBiome().name();
            return biome.contains("BEACH")
                || biome.contains("SHORE");
        }

        @Override public List<String> getBookPages() {
            return List.of("As you may have guessed by now, this item is found at the beach. Fill some buckets with water and take a close look inside.");
        }
    },
    PICK_BLUE_OR_PACKED_ICE(Type.BLOCK_BREAK, Material.BLUE_ICE, Material.PACKED_ICE) {
        @Override public List<String> getBookPages() {
            return List.of("We are taking you one step further, by finding one that's trapped in the blue perma-frost of an iceberg.",
                           "There's simply no better way to conserve prehistoric organic matter. Try mining some blue or packed ice until you find one of these gems.");
        }
    },
    PICK_COLD_COARSE_DIRT(Type.BLOCK_BREAK, Material.COARSE_DIRT) {
        @Override public boolean checkBlock(Block block) {
            return block.getTemperature() <= 0.3;
        }

        @Override public List<String> getBookPages() {
            return List.of("Coarse dirt isn't overall hard to find, but you must make sure that it is in a cold environment. Better pack some warm clothes!",
                           "In the frozen dirt, you will should find some of these with a little effort.");
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
    PICK_PODZOL(Type.BLOCK_BREAK, Material.PODZOL) {
        @Override public List<String> getBookPages() {
            return List.of("They usually fall of the trees and find their ways to the ground, where they turn to podzol over time.",
                           "I would bet my favorite walking stick that some survive this journey intact, however.");
        }
    },
    PICK_BERRY_BUSH(Type.BLOCK_BREAK, Material.SWEET_BERRY_BUSH) {
        @Override public List<String> getBookPages() {
            return List.of("This one likes to tumble down and get caught up in the undergrowth. Specifically, inside sweet berry bushes.",
                           "You're going to have to break some of those. If you would, bring me some of the berries as well, but it's not a priority; just a personal request.");
        }
    },
    PICK_MOSSY_STONE(Type.BLOCK_BREAK,
                     Material.MOSSY_COBBLESTONE,
                     Material.MOSSY_COBBLESTONE_SLAB,
                     Material.MOSSY_COBBLESTONE_STAIRS,
                     Material.MOSSY_COBBLESTONE_WALL,
                     Material.MOSSY_STONE_BRICKS,
                     Material.MOSSY_STONE_BRICK_SLAB,
                     Material.MOSSY_STONE_BRICK_STAIRS,
                     Material.MOSSY_STONE_BRICK_WALL) {
        @Override public List<String> getBookPages() {
            return List.of("Try to scrape some off of mossy cobblestone. A handful should be enough for now.");
        }
    },
    PICK_GLOW_LICHEN(Type.BLOCK_BREAK, Material.GLOW_LICHEN) {
        @Override public List<String> getBookPages() {
            return List.of("Naturally, this growth lights up the darkest of caves. Go cave diving and scrape off some glow lichen, and you're sure find some in no time.");
        }
    },
    PICK_MOSS(Type.BLOCK_BREAK, Material.MOSS_BLOCK, Material.MOSS_CARPET) {
        @Override public List<String> getBookPages() {
            return List.of("Try to find it inside some moss. Both the full moss block and the moss carpet will occasionaly have it.");
        }
    },
    PICK_SWAMP_BROWN_MUSHROOM(Type.BLOCK_BREAK, Material.BROWN_MUSHROOM) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome().name().contains("SWAMP")
                || block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("It looks just like any other brown mushroom you can find in the swamps or the Nether, but trust me.",
                           "Once you sink your teeth into one of these, you will be spoiled forever! We promise to save as many as possible for the Maypole.");
        }
    },
    PICK_ISLAND_BROWN_MUSHROOM(Type.BLOCK_BREAK, Material.BROWN_MUSHROOM) {
        @Override public boolean checkBlock(Block block) {
            return block.getBiome() == Biome.MUSHROOM_FIELDS;
        }

        @Override public List<String> getBookPages() {
            return List.of("However, there is one caveat: This one only grows in the mooshroom biomes. The cows that live there are not known to be particularly territorial, but one never knows.",
                           "Go there and inspect the common brown mushrooms there in order to find a special specimen.");
        }
    },
    KILL_NETHER_PIGLIN(Type.ENTITY_KILL, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("This one is the favorite food of the common piglin. Nobody knows how they locate them, it's their trade secret. Some say they can smell it from 128 blocks distance while others claim they grow on them.",
                           "It is conceivable that this is merely a figure of speech, however. I'm afraid they're not going to be part with it willingly, so if they refuse your gold, I see only one other option.",
                           "The mighty Maypole is certainly worth this selfless sacrifice!");
        }
    },
    PICK_NETHER_RED_MUSHROOM(Type.BLOCK_BREAK, Material.RED_MUSHROOM) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("This one looks exactly like the common red mushroom, so just check out some patches of those. Only problem: You will have to descend into the depths of the Nether.",
                           "Every child knows that there are mushrooms in hell.");
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
    BUCKET_NETHER_LAVA(Type.BUCKET_FILL, Material.LAVA) {
        @Override public boolean checkBlock(Block block) {
            return block.getWorld().getEnvironment() == Environment.NETHER;
        }

        @Override public List<String> getBookPages() {
            return List.of("It only survives at the hottest places on earth. The most surefire way to find them (no pun intended) is to harvest a lava pool in the Nether.",
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
    PICK_DRIPSTONE(Type.BLOCK_BREAK, Material.POINTED_DRIPSTONE) {
        @Override public List<String> getBookPages() {
            return List.of("This rare substance can only be found attached to stalactites and stalagmites. I can never remember the difference.",
                           "Go break either of these natural wonders until you get lucky.");
        }
    },
    ;

    public final Type type;
    public final Set<Material> materials;
    public final Set<EntityType> entityTypes;

    MaypoleAction(final Type type, final Material... materials) {
        this(type, Set.of(materials), Set.of());
    }

    MaypoleAction(final Type type, final EntityType... entityTypes) {
        this(type, Set.of(), Set.of(entityTypes));
    }

    MaypoleAction(final Type type, final List<Tag<Material>> tags) {
        this(type, sets(tags), Set.of());
    }

    public abstract List<String> getBookPages();

    public boolean checkBlock(Block block) {
        return true;
    }

    public boolean isValid() {
        return type != Type.INVALID;
    }

    private static Set<Material> sets(List<Tag<Material>> tags) {
        Set<Material> result = new HashSet<>();
        for (Tag<Material> tag : tags) {
            result.addAll(tag.getValues());
        }
        return result;
    }

    public enum Type {
        INVALID,
        BLOCK_BREAK,
        BUCKET_FILL,
        ENTITY_KILL;
    }
}
