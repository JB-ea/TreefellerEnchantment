package io.github.lamelemon.treefellerEnchantment.commands

import io.github.lamelemon.treefellerEnchantment.utils.Utils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import java.util.Locale

class LimitlessEnchant: BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        val player = commandSourceStack.sender
        if (player !is Player) return

        if (args.isEmpty()) {
            Utils.messagePlayer(player, "<red>Missing arguments!</red>")
            Utils.simplePlaySound(player, Sound.BLOCK_NOTE_BLOCK_BASS)
            return
        }

        val enchantName = args[0].lowercase(Locale.getDefault())
        var level = 1

        if (args.size >= 2) {
            try {
                level = Integer.parseInt(args[1])
            } catch (_: NumberFormatException) {}
        }

        val item = player.inventory.itemInMainHand
        if (item.type.isAir) {
            Utils.messagePlayer(player, "<red>You must be holding an item!</red>")
            Utils.simplePlaySound(player, Sound.BLOCK_NOTE_BLOCK_BASS)
            return
        }

        val enchant = Utils.getEnchant(enchantName)
        if (enchant is Enchantment) {
            Utils.messagePlayer(player, "Applying $enchantName...")
            item.addUnsafeEnchantment(enchant, level)
        }
    }
}