package io.github.lamelemon.treefellerEnchantment.events

import io.github.lamelemon.treefellerEnchantment.TreefellerEnchantment
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class TreeBreakEvent(val hasToSneak: Boolean, val blockCapMultiplier: Int, config: ConfigurationSection): Listener {

    var blocksLeft: Int = 0
    val allowedTrees: HashMap<String, HashSet<Material>> = HashMap()

    init {
        for (key in config.getKeys(false)) {
            allowedTrees[key] = config.getStringList(key)
                .mapTo(HashSet()) { Material.valueOf(it) }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun blockBreakEvent(event: BlockBreakEvent) {
        val player = event.player
        if (player.isSneaking != hasToSneak) return

        val currentTool = player.inventory.itemInMainHand

        if (!currentTool.containsEnchantment(TreefellerEnchantment.Companion.enchantment)) return

        val block = event.block
        val allowedBlocks = allowedTrees[block.type.toString()]
        if (allowedBlocks is HashSet<Material>) {
            blocksLeft = currentTool.getEnchantmentLevel(TreefellerEnchantment.Companion.enchantment) * blockCapMultiplier
            treeBreaker(block, player, allowedBlocks)
        }
    }

    fun treeBreaker(block: Block, player: Player, allowedBlocks: HashSet<Material>) {
        if (blocksLeft <= 0 || block.type !in allowedBlocks) return

        blocksLeft--
        player.breakBlock(block)

        treeBreaker(block.getRelative(0, 1, 0), player, allowedBlocks)
        for (y in -1..1) {
            for (z in -1..1) {
                for (x in -1..1) {
                    treeBreaker(block.getRelative(x, y, z), player, allowedBlocks)
                }
            }
        }
    }
}