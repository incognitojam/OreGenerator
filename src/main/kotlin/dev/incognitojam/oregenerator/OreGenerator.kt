package dev.incognitojam.oregenerator

import gg.flyte.twilight.scheduler.async
import gg.flyte.twilight.twilight
import io.papermc.lib.PaperLib
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitCommandHandler

class OreGenerator : JavaPlugin() {
    companion object {
        lateinit var instance: OreGenerator
        lateinit var oreIterator: OreIterator
    }

    override fun onEnable() {
        instance = this
        twilight(this) { }

        saveDefaultConfig()
        oreIterator = OreIterator(config)

        BukkitCommandHandler.create(this).apply {
            enableAdventure()
            register(ReloadCommand { sender ->
                async {
                    reloadConfig()
                    oreIterator.loadConfig(config)
                    sender.sendMessage(Component.text("Config reloaded."))
                }
            })
            registerBrigadier()
        }

        OreListener(oreIterator)

        PaperLib.suggestPaper(this)
    }
}
