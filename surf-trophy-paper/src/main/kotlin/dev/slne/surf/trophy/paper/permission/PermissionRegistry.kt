package dev.slne.surf.trophy.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.trophy"
    const val PREFIX_COMMAND = "$PREFIX.command"

    val COMMAND_TROPHY = create("$PREFIX_COMMAND.trophy")
    val COMMAND_TROPHY_OTHERS = create("$COMMAND_TROPHY.others")
    val COMMAND_TROPHY_RELOAD = create("$COMMAND_TROPHY.reload")
    val COMMAND_TROPHY_REFRESH = create("$COMMAND_TROPHY.refresh")
    val COMMAND_TROPHY_CREATE = create("$COMMAND_TROPHY.create")
    val COMMAND_TROPHY_LIST = create("$COMMAND_TROPHY.list")
    val COMMAND_TROPHY_DELETE = create("$COMMAND_TROPHY.delete")
}