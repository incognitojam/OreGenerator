package com.github.incognitojam.oregenerator;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class OreListener implements Listener {
    private final OreConfig config;
    private final Random random;
    private Set<Location> flintLocations;

    OreListener(OreConfig config) {
        this.config = config;
        this.random = new Random();
        this.flintLocations = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event) {
        Block block = event.getBlock();

        /*
         * Ensure it is a cobblestone generator event (new state is either
         * cobblestone or smooth stone)
         */
        if (block.getState().getType() != Material.LAVA
                || (event.getNewState().getType() != Material.COBBLESTONE
                && event.getNewState().getType() != Material.STONE))
            return;

        // We don't want cobblestone to generate
        event.setCancelled(true);

        // Set our own material
        Material material = config.getNextOreMaterial();
        block.setType(material);

        /*
         * If we spawned cobblestone, add location to flintLocations set so
         * that we can wait for the block break event here (to spawn a flint
         * item).
         */
        if (material == Material.COBBLESTONE)
            flintLocations.add(block.getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();

        // Ensure this is a ore location, otherwise ignore the event
        if (!flintLocations.contains(location))
            return;

        // Ensure the player is not in creative mode
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        // Roll the dice for spawning a flint item
        if (random.nextDouble() >= config.getFlintChance())
            return;

        // Get the world to spawn the entity in
        World world = location.getWorld();
        if (world == null)
            throw new IllegalStateException("World must not be null");

        // Spawn the flint item at the location of the ore
        world.dropItemNaturally(location, new ItemStack(Material.FLINT, 1));

        // and remove the ore location
        flintLocations.remove(location);
    }
}
