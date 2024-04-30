package dev.incognitojam.oregenerator

import net.kyori.adventure.audience.Audience
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.annotation.CommandPermission

class ReloadCommand(private val handler: (Audience) -> Unit) {
    @Command("oregenerator reload")
    @CommandPermission("oregenerator.manage")
    fun reloadCommand(sender: Audience) = handler(sender)
}
