package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.PlayerInfoData

/**
 * @property max The amount of players that can maximally connect to the server at the same time
 * @property count The current player count connected to your server
 * @property list A list of player names that are currently connected to the server
 */
class PlayerInfo(internal val data: PlayerInfoData) {
    val max: Int get() = data.max
    val count: Int get() = data.count
    val list: List<String> get() = data.list
}