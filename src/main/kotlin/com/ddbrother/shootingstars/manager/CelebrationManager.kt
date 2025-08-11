package com.ddbrother.shootingstars.manager

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CelebrationManager {
    private val markedEntities = ConcurrentHashMap<UUID, Int>()

    fun register() {
        ServerTickEvents.END_SERVER_TICK.register(::onServerTick)
        ServerLifecycleEvents.SERVER_STOPPING.register(::onServerStopping)
    }

    fun markEntity(uuid: UUID, durationTicks: Int) {
        markedEntities[uuid] = durationTicks
    }

    fun isMarked(uuid: UUID): Boolean {
        return markedEntities.containsKey(uuid)
    }

    private fun onServerTick(server: MinecraftServer) {
        if (markedEntities.isEmpty()) return

        for (uuid in markedEntities.keys) {
            val newTicks = (markedEntities[uuid] ?: 0) - 1
            if (newTicks > 0) {
                markedEntities[uuid] = newTicks
            } else {
                markedEntities.remove(uuid)
            }
        }
    }

    private fun onServerStopping(server: MinecraftServer) {
        markedEntities.clear()
    }
}