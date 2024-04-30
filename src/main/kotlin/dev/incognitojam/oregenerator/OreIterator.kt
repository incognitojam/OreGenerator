package dev.incognitojam.oregenerator

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class OreIterator(config: FileConfiguration) : Iterator<Material> {
    private data class OreEntry(val material: Material, val weight: Int)

    private val ores = mutableListOf<OreEntry>()
    private val lock = Any()
    private var totalWeight = 0

    init {
        loadConfig(config)
    }

    fun loadConfig(config: FileConfiguration) {
        synchronized(lock) {
            ores.clear()
            totalWeight = 0

            val section =
                config.getConfigurationSection("ores")
                    ?: throw IllegalArgumentException("Config section 'ores' not found.")
            val map = section.getValues(false)
            if (map.isEmpty()) throw IllegalArgumentException("Config section 'ores' is empty.")

            map.forEach { (key, value) ->
                val material =
                    Material.matchMaterial(key) ?: throw IllegalArgumentException("Material '$key' not found.")
                val weight = value as? Int ?: throw IllegalArgumentException("Weight for '$key' is not an integer.")
                if (weight <= 0) throw IllegalArgumentException("Weight for '$key' must be greater than 0.")
                totalWeight += weight
                ores.add(OreEntry(material, weight))
            }
        }
    }

    override fun hasNext() = true

    override fun next(): Material {
        synchronized(lock) {
            val target = (0 until totalWeight).random()

            var current = 0
            for (ore in ores) {
                current += ore.weight
                if (target < current) return ore.material
            }

            throw IllegalStateException("Failed to select a material.")
        }
    }
}
