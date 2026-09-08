package dev.slne.surf.trophy.paper.util

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.trophy.api.trophy.Trophy
import org.bukkit.inventory.ItemStack
import kotlin.io.encoding.Base64

/**
 * How many distinct encoded trophy items stay decoded.
 */
private const val DECODED_ITEM_CACHE_SIZE = 256L

/**
 * Trophy items kept in their decoded form, keyed by the encoding they were read from.
 */
private val decodedItems = Caffeine.newBuilder()
    .maximumSize(DECODED_ITEM_CACHE_SIZE)
    .build<String, ItemStack>()

fun itemStackToString(itemStack: ItemStack): String = Base64.encode(itemStack.serializeAsBytes())
fun itemStackFromString(encoded: String): ItemStack =
    ItemStack.deserializeBytes(Base64.decode(encoded))

val Trophy.item: ItemStack
    get() = decodedItems.get(itemString, ::itemStackFromString).clone()
