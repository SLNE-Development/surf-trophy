package dev.slne.surf.trophy.api.player

import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

data class TrophyPlayer(
    val uuid: UUID,
    val name: String,
    val trophies: ObjectList<ReceivedTrophy>,
    var selectedTrophy: ReceivedTrophy?
)