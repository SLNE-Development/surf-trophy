package dev.slne.surf.trophy.minestom.util

import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.core.client.message.receivedTrophyLore
import dev.slne.surf.trophy.core.client.message.trophyDisplayName
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack

/**
 * The item representing [receivedTrophy], named after the trophy and describing when it was
 * received.
 */
fun trophyItem(receivedTrophy: ReceivedTrophy): ItemStack = receivedTrophy.trophy.item
    .withDisplayName(trophyDisplayName(receivedTrophy.trophy))
    .withLore(receivedTrophyLore(receivedTrophy))

/**
 * Returns this item shown under [name], without the italics Minecraft renders custom names in.
 */
fun ItemStack.withDisplayName(name: Component): ItemStack =
    with(DataComponents.CUSTOM_NAME, name.withoutDefaultItalics())

/**
 * Returns this item described by [lore], without the italics Minecraft renders lore in.
 */
fun ItemStack.withLore(lore: List<Component>): ItemStack =
    with(DataComponents.LORE, lore.map { it.withoutDefaultItalics() })

private fun Component.withoutDefaultItalics() =
    decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
