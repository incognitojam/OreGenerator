package com.github.incognitojam.oregenerator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

class HopperListener implements Listener {
    private final OreConfig oreConfig;
    private final Random random;

    HopperListener(OreConfig oreConfig) {
        this.oreConfig = oreConfig;
        this.random = new Random();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHopperTransferEvent(InventoryMoveItemEvent event) {
        // Ensure the item is being transferred from a hopper
        if (event.getSource().getType() != InventoryType.HOPPER)
            return;

        // Get the hopper inventory contents
        ItemStack item = event.getItem();
        Inventory hopperInventory = event.getSource();
        ItemStack[] contents = hopperInventory.getStorageContents();

        // Count how many slots containing flint are in the hopper
        int flintCount = 0;
        for (ItemStack slot : contents) {
            // Ensure the stack is not null
            if (slot == null)
                continue;

            // Ensure the item is flint
            if (slot.getType() != Material.FLINT)
                continue;

            // Increment the flint count!
            flintCount++;
        }

        /*
         * If the item we are attempting to transfer is FLINT, we should
         * prevent it UNLESS there is more than 4 flint (all 5 slots are flint)
         */
        if (item.getType() == Material.FLINT && flintCount < 5) {
            event.setCancelled(true);
            return;
        }

        // Otherwise, we are only concerned with SANDSTONE.
        if (item.getType() != Material.SANDSTONE)
            return;

        /*
         * In this case, we only manipulate the SANDSTONE if there is 4 flint
         * in the hopper.
         */
        if (flintCount < 4)
            return;

        /*
         * Now that we know we are dealing with SANDSTONE leaving a hopper with
         * 4 flint, we can cancel the event and instead send between 2 and 4
         * SAND to the target inventory.
         */
//        event.setCancelled(true);

//        System.out.println("hmm");
//
//        // Manually remove the event item from the hopper
//        for (ItemStack slot : contents) {
//            System.out.println("a? " + slot);
//
//            if (slot.getType() != Material.SANDSTONE)
//                continue;
//
//            System.out.println("b!");
//
//            slot.setAmount(slot.getAmount() - 1);
//            System.out.println("c: " + slot);
//
//            break;
//        }

//        hopperInventory.removeItem(new ItemStack(Material.SANDSTONE, 1));

        // Add between 2 and 4 sand to the destination inventory
        // TODO(feat): make configurable
//        Inventory destination = event.getDestination();
        ItemStack sand = new ItemStack(Material.SAND, 2 + random.nextInt(2));
        event.setItem(sand);
//        destination.addItem(sand);
    }
}
