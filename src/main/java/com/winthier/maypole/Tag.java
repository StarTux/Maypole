package com.winthier.maypole;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class Tag {
    protected boolean enabled = false;
    protected boolean debug = false;
    protected Pole pole = new Pole();

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

        @Override
        public String toString() {
            return world + " " + x + " " + y + " " + z;
        }
    }
}
