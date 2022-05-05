package com.winthier.maypole;

import com.cavetale.core.command.CommandWarn;
import com.cavetale.mytems.Mytems;
import java.util.List;

@SuppressWarnings("LineLength")
public enum Collectible {
    LUCID_LILY(Mytems.LUCID_LILY) {
        @Override public List<String> getBookPages() {
            return List.of("Most commonly associated with witches who grow them in the ponds around their huts. Nobody knows what draws them to this flower, but I bet it's the olfactory appeal, however... repulsive you and I may perceive it.",
                           "Its potential for an effective deodorant must be enormous. However, we only care about its visual appeal, which is just perfect for the Maypole! This fragile blossom should bloom around this time of year in moist climates.",
                           "If someone were to inspect some seagrass in the swamplands, they would find a specimen in no time, guaranteed!");
        }
    },
    PINE_CONE(Mytems.PINE_CONE) {
        @Override public List<String> getBookPages() {
            return List.of("This funny looking cone falls of the fir tree to carry its seed far away. Some people love to dry them and build small figurines with them. You may see where this is going.",
                           "Only where to find a fresh specimen at this time of the year is the question. It's time to shake some pine tree leaves in a taiga biome and find out.");
        }
    },
    ORANGE_ONION(Mytems.ORANGE_ONION) {
        @Override public List<String> getBookPages() {
            return List.of("There are not many things in the world that can bring me to tears. Cut onions surely are one of them, and the orange kind is no exception. What makes this one so special is that it grows the magnificent orange tulip.",
                           "So one of those is bound to yield the onion, right? Just pick a few naturally grown orange tulips and eventually an onion will emerge.");
        }
    },
    MISTY_MOREL(Mytems.MISTY_MOREL) {
        @Override public List<String> getBookPages() {
            return List.of("Large crowds comb through the woods every year to find this delicacy. They are usually easy to find in the shady woods. However, there is one caveat: The one we are looking for grows in clear daylight.",
                           "It looks just like any other brown mushroom you can find in the swamps, but trust me. Once you sink your teeth into one of these, you will be spoiled forever! We promise to save as many as possible for the Maypole.");
        }
    },
    RED_ROSE(Mytems.RED_ROSE) {
        @Override public List<String> getBookPages() {
            return List.of("It is a common misconception that they removed the red rose from the game in favor of the common poppy. Needless to say that one cannot have a rose bush without roses.",
                           "Plowing through enough of them should allow you to come up with a red rose in a matter of minutes.");
        }
    },
    FROST_FLOWER(Mytems.FROST_FLOWER) {
        @Override public List<String> getBookPages() {
            return List.of("Oh boy, finally we are getting to the good stuff. The frost flower is incredibly tricky to cultivate in your garden as it requires devilishly low temperatures to blossom.",
                           "Even the slightest warm breeze could ruin months of care. However, you only need to take care of the easy part. Find its seeds in the taller grass of a snowy area, and you're good to go.");
        }
    },
    HEAT_ROOT(Mytems.HEAT_ROOT) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pan, into the fire! This subterranean growth is searing to the touch and should only be harvested while wearing gloves.",
                           "The whole plant can usually be seen all over the desert, but now that spring is here, only the roots are still alive.",
                           "Tear a couple of dead bushes out of the desert floor, and one is bound to surface. Carrying of burn ointment is strongly advised.");
        }
    },
    CACTUS_BLOSSOM(Mytems.CACTUS_BLOSSOM) {
        @Override public List<String> getBookPages() {
            return List.of("If it seems at this point that we are sending you out just to get your hands burned and prickled, you are mistaken. Having said that, be careful when you break those cacti.",
                           "In order to find a blossom that's intact, you will have to check out more than one. I hope I don't need to remind you that pulling cactus spines out of your finger is way more painful than getting them in there.");
        }
    },
    PIPE_WEED(Mytems.PIPE_WEED) {
        @Override public List<String> getBookPages() {
            return List.of("Oh, the blissful nights I spent in front of a hot chimney, thanks to Old Toby! I promise you that I will not smoke most of it. It's important for the Maypole festivities. It grows between the huge jungle trees.",
                           "Just keep your eye out for a fern looking plant. Hurry up, please, as my personal stash is almost empty...");
        }
    },
    KINGS_PUMPKIN(Mytems.KINGS_PUMPKIN) {
        @Override public List<String> getBookPages() {
            return List.of("There is a special kind of distinguished pumpkin, identifiable by it's crown-shaped leaves on top, hence the name. You will have to inspect a couple of pumpkins to find one. So make sure not to walk past a pumpkin patch if you come across one.");
        }
    },
    SPARK_SEED(Mytems.SPARK_SEED) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pan, into the... I made that joke already, didn't I? Anyway, this seed is equally hot, if not moreso than the Heat Root (see above). Getting your hands dirty (or burned) is something you may not be able to avoid this time around...",
                           "So this seed. It only survives at the hottest places on earth, in direct sunlight. The most surefire way to find them (no pun intended) is to find a pool of surface lava.",
                           "Load some of it into a bucket and inspect it, carefully. You will know it when you see one. Try not to burn your eyebrows. Trust me, it takes forever to grow them back, not to mention the relentless ridicule from coworkers...");
        }
    },
    OASIS_WATER(Mytems.OASIS_WATER) {
        @Override public List<String> getBookPages() {
            return List.of("Time for a break! Well actually, traveling to the desert could be argued to be a little less than pleasant... Anyway, the Oasis Water can only be found there, in natural water sources on the surface!",
                           "We need that water to keep the more delicate exhibits moist, and it doesn't stay fresh for very long. so someone will have to go and fetch some.");
        }
    },
    CLAMSHELL(Mytems.CLAMSHELL) {
        @Override public List<String> getBookPages() {
            return List.of("Susie sells seashells by the seashore. These clams are the remains of shellfish and once they're abandoned, they sink to the ground. There, the only place protected from various colorful predators are coral reefs.",
                           "Hammer on those for a bit and you will strike gold! Keep in mind that they have to be alive.");
        }
    },
    FROZEN_AMBER(Mytems.FROZEN_AMBER) {
        @Override public List<String> getBookPages() {
            return List.of("You may have heard that mosquitoes sometimes get frozen in Amber, to be conserved for millions of years. We are taking you one step further, by finding amber that's trapped in the blue perma-frost of an iceberg.",
                           "There's simply no better way to conserve prehistoric organic matter. Try mining some blue or packed ice until you find one of these gems.");
        }
    },
    CLUMP_OF_MOSS(Mytems.CLUMP_OF_MOSS) {
        @Override public List<String> getBookPages() {
            return List.of("Moss loves dark and moist spaces, so the woods are bound to be lousy with them. Try to scrape some off of mossy cobblestone. A handful should be enough for now.");
        }
    },
    FIRE_AMANITA(Mytems.FIRE_AMANITA) {
        @Override public List<String> getBookPages() {
            return List.of("Out of the frying pa-... never mind. So this fiery fungus grows in the depths of the Nether. This is by far the most dangerous place we are sending you to, so do be careful. The Fire Amanita is the favorite food of the common piglin.",
                           "Nobody knows how they locate them, it's their trade secret. Some say they can smell it from 128 blocks distance while others claim they grow on them. It is conceivable that this is merely a figure of speech, however.",
                           "I'm afraid they're not going to be part with it willingly, so if they refuse your gold, I see only one other option. The mighty Maypole is certainly worth this selfless sacrifice!");
        }
    };

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
}
