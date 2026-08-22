package dev.slne.surf.trophy.paper.permission

import dev.slne.surf.api.paper.permission.PermissionRegistry
import dev.slne.surf.trophy.core.client.permission.TrophyPermissions

object PermissionRegistry : PermissionRegistry() {
    val COMMAND_TROPHY = create(TrophyPermissions.COMMAND_TROPHY)
    val COMMAND_TROPHY_OTHERS = create(TrophyPermissions.COMMAND_TROPHY_OTHERS)
    val COMMAND_TROPHY_RELOAD = create(TrophyPermissions.COMMAND_TROPHY_RELOAD)
    val COMMAND_TROPHY_REFRESH = create(TrophyPermissions.COMMAND_TROPHY_REFRESH)
    val COMMAND_TROPHY_MANAGE = create(TrophyPermissions.COMMAND_TROPHY_MANAGE)
    val COMMAND_TROPHY_CREATE = create(TrophyPermissions.COMMAND_TROPHY_CREATE)
    val COMMAND_TROPHY_LIST = create(TrophyPermissions.COMMAND_TROPHY_LIST)
    val COMMAND_TROPHY_DELETE = create(TrophyPermissions.COMMAND_TROPHY_DELETE)
}
