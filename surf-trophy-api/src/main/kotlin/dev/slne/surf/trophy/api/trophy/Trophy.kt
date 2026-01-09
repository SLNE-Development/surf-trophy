package dev.slne.surf.trophy.api.trophy

import org.bukkit.inventory.ItemStack
import java.util.*

data class Trophy(
    val uuid: UUID,
    val name: String,
    val description: String,
    val item: ItemStack
)
