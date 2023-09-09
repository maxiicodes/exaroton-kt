package com.github.hofmmaxi.exaroton.entities

import java.util.*

@OptIn(ExperimentalStdlibApi::class)
enum class ServerStatus(status: Int) {
    OFFLINE(0),
    ONLINE(1),
    STARTING(2),
    STOPPING(3),
    RESTARTING(4),
    SAVING(5),
    LOADING(6),
    CRASHED(7),
    PENDING(8),
    PREPARING(10);

    override fun toString(): String {
        return super.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    companion object {
        fun fromStatusInt(int: Int): ServerStatus {
            return entries.find { it.ordinal == int } ?: throw IllegalArgumentException("Invalid status int: $int")
        }
    }
}