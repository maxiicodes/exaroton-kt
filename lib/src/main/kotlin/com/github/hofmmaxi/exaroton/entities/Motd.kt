package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.MotdData

/**
 * @property motd Represents the server's current MOTD
 */
class Motd(private val data: MotdData) {
    val motd: String get() = data.motd
}