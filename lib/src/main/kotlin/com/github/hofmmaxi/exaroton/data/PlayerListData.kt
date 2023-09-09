package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerListData(val entries: List<String>)