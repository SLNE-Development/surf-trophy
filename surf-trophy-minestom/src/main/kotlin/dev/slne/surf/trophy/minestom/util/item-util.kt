package dev.slne.surf.trophy.minestom.util

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.trophy.api.trophy.Trophy
import net.kyori.adventure.nbt.BinaryTagIO
import net.minestom.server.MinecraftServer
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import kotlin.io.encoding.Base64

private val log = logger()

private const val GZIP_MAGIC_FIRST = 0x1f.toByte()
private const val GZIP_MAGIC_SECOND = 0x8b.toByte()

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

val FALLBACK_TROPHY_MATERIAL: Material = Material.BARRIER

/**
 * Reads the item an encoded trophy stands for.
 *
 * The encoding is Base64 over the item's NBT representation, optionally gzip compressed, as
 * written by the platforms that create trophies.
 *
 * @return the decoded item, or `null` when [encoded] does not describe a readable item
 */
fun itemStackFromString(encoded: String): ItemStack? = try {
    val bytes = Base64.decode(encoded)
    val compound = bytes.inputStream().use { input ->
        BinaryTagIO.unlimitedReader().read(input, compressionOf(bytes))
    }

    ItemStack.fromItemNBT(compound, MinecraftServer.getRegistries())
} catch (exception: Exception) {
    log.atWarning()
        .withCause(exception)
        .log("Failed to read the item of a trophy")
    null
}

/**
 * The item this trophy is shown as, or a [FALLBACK_TROPHY_MATERIAL] stack when its stored item
 * cannot be read.
 */
val Trophy.item: ItemStack
    get() = decodedItems.get(itemString) { encoded ->
        itemStackFromString(encoded) ?: run {
            log.atWarning()
                .log("Trophy '$name' falls back to $FALLBACK_TROPHY_MATERIAL")
            ItemStack.of(FALLBACK_TROPHY_MATERIAL)
        }
    }

private fun compressionOf(bytes: ByteArray) = if (
    bytes.size >= 2 && bytes[0] == GZIP_MAGIC_FIRST && bytes[1] == GZIP_MAGIC_SECOND
) {
    BinaryTagIO.Compression.GZIP
} else {
    BinaryTagIO.Compression.NONE
}
