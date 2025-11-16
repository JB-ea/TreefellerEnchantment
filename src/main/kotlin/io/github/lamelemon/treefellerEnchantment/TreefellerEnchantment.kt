package io.github.lamelemon.treefellerEnchantment

import io.github.lamelemon.treefellerEnchantment.commands.LimitlessEnchant
import io.github.lamelemon.treefellerEnchantment.events.TreeBreakEvent
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TreefellerEnchantment : JavaPlugin() {

    companion object {
        lateinit var enchantment: Enchantment
    }

    override fun onEnable() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            saveResource("config.yml", false)
        }

        val config = YamlConfiguration.loadConfiguration(configFile)
        val pluginManager = server.pluginManager

        if (!config.getBoolean("enabled", true)) {
            pluginManager.disablePlugin(this)
        }

        val treeStructure = config.getConfigurationSection("tree-structure")
        if (treeStructure is ConfigurationSection) {
            pluginManager.registerEvents(
                TreeBreakEvent(
                    config.getBoolean("has-to-sneak", true),
                    config.getInt("block-cap-multiplier", 100),
                    treeStructure
                ), this)
        } else {
            pluginManager.disablePlugin(this)
        }

        registerCommand("LimitlessEnchant", LimitlessEnchant())
    }
}
