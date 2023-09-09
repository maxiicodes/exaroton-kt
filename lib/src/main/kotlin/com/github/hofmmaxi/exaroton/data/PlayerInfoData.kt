package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfoData(
    val max: Int,
    val count: Int,
    val list: List<String>
)