package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class ServerData(
    val id: String,
    val name: String,
    val address: String,
    val motd: String,
    val status: Int,
    val host: String?,
    val port: Int?,
    val players: PlayerInfoData,
    val software: SoftwareData?,
    val shared: Boolean,
)