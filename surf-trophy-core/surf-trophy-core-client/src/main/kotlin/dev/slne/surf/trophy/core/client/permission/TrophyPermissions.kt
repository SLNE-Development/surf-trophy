package dev.slne.surf.trophy.core.client.permission

/**
 * Platform-neutral registry of all permission node strings used by surf-trophy.
 */
object TrophyPermissions {
    const val PREFIX = "surf.trophy"
    const val PREFIX_COMMAND = "$PREFIX.command"

    const val COMMAND_TROPHY = "$PREFIX_COMMAND.trophy"
    const val COMMAND_TROPHY_OTHERS = "$COMMAND_TROPHY.others"
    const val COMMAND_TROPHY_RELOAD = "$COMMAND_TROPHY.reload"
    const val COMMAND_TROPHY_REFRESH = "$COMMAND_TROPHY.refresh"
    const val COMMAND_TROPHY_MANAGE = "$COMMAND_TROPHY.manage"
    const val COMMAND_TROPHY_CREATE = "$COMMAND_TROPHY.create"
    const val COMMAND_TROPHY_LIST = "$COMMAND_TROPHY.list"
    const val COMMAND_TROPHY_DELETE = "$COMMAND_TROPHY.delete"
}
