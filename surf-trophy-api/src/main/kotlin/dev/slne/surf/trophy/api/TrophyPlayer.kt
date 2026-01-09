package dev.slne.surf.trophy.api

import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

data class TrophyPlayer(
    val uuid: UUID,
    val name: String,
    val trophies: ObjectList<ReceivedTrophy>
)