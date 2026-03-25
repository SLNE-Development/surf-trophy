package dev.slne.surf.trophy.core.paper.util

import dev.slne.surf.trophy.api.trophy.Trophy
import org.bukkit.inventory.ItemStack
import kotlin.io.encoding.Base64

fun itemStackToString(itemStack: ItemStack): String = Base64.encode(itemStack.serializeAsBytes())
fun itemStackFromString(encoded: String): ItemStack =
    ItemStack.deserializeBytes(Base64.decode(encoded))

val Trophy.item get() = itemStackFromString(itemString)