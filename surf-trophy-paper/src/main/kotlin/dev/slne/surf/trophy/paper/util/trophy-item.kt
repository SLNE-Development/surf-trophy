package dev.slne.surf.trophy.paper.util

import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.lore
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.core.client.message.receivedTrophyLore
import dev.slne.surf.trophy.core.client.message.trophyDisplayName
import org.bukkit.inventory.ItemStack

/**
 * The item representing [receivedTrophy], named after the trophy and describing when it was
 * received.
 */
fun trophyItem(receivedTrophy: ReceivedTrophy): ItemStack =
    receivedTrophy.trophy.item.apply {
        displayName(trophyDisplayName(receivedTrophy.trophy))
        lore(*receivedTrophyLore(receivedTrophy).toTypedArray())
    }
