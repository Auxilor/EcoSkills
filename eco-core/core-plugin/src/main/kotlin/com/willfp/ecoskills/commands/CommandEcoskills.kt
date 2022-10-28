package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.skills.CustomSkills
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.lrcdb.CommandExport
import com.willfp.libreforge.lrcdb.CommandImport
import com.willfp.libreforge.lrcdb.ExportableConfig
import org.bukkit.command.CommandSender

class CommandEcoskills(plugin: LibReforgePlugin) :
    PluginCommand(
        plugin,
        "ecoskills",
        "ecoskills.command.ecoskills",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }

    init {
        addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandReset(plugin))
            .addSubcommand(CommandGive(plugin))
            .addSubcommand(CommandRecount(plugin))
            .addSubcommand(CommandImport("customskills", plugin))
            .addSubcommand(CommandExport(plugin) {
                CustomSkills.values().map {
                    ExportableConfig(
                        it.id,
                        it.config
                    )
                }
            })
    }
}
