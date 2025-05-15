package com.winthier.maypole;

import java.io.Serializable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter
public final class Tag implements Serializable {
    protected boolean debug = false;
    protected Pole pole = new Pole();

    @Getter
    public static final class Pole {
        protected String world = "";
        protected int x;
        protected int y;
        protected int z;

        public Block toBlock() {
            World w = Bukkit.getWorld(world);
            if (w == null) return null;
            return w.getBlockAt(x, y, z);
        }

        public boolean isAt(Block block) {
            return block.getX() == x
                && block.getZ() == z
                && block.getY() == y
                && block.getWorld().getName().equals(world);
        }

        public boolean isIn(World bworld) {
            return bworld.getName().equals(world);
        }

        public boolean isNearby(Location location) {
            return isIn(location.getWorld())
                && Math.abs(location.getBlockX() - x) < 32
                && Math.abs(location.getBlockZ() - z) < 32;
        }

        @Override
        public String toString() {
            return world + " " + x + " " + y + " " + z;
        }
    }
}
