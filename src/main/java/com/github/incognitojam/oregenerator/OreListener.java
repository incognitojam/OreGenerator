package com.github.incognitojam.oregenerator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

class OreListener implements Listener {
    private final OreConfig config;
    
    OreListener(OreConfig config) {
        this.config = config;
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event) {
        Block block = event.getBlock();
        
        // Ensure it is a cobblestone generator event
        if (block.getState().getType() != Material.LAVA
                || event.getNewState().getType() != Material.COBBLESTONE) return;
        
        // We don't want cobblestone to generate
        event.setCancelled(true);
        
        // Set our own material
        Material material = config.getNextOreMaterial();
        System.out.println(material);
        block.setType(material);
    }
}
