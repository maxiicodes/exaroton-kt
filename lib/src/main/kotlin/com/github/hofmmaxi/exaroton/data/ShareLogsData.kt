package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class ShareLogsData(val id: String, val url: String, val raw: String)