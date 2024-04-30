package dev.incognitojam.oregenerator

import gg.flyte.twilight.event.event
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFormEvent

class OreListener(private val oreIterator: OreIterator) : Listener {
    init {
        event<BlockFormEvent> {
            if (block.state.type != Material.LAVA || newState.type != Material.COBBLESTONE) return@event
            isCancelled = true
            block.type = oreIterator.next()
        }
    }
}
