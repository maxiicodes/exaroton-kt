package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class SoftwareData(
    val id: String,
    val name: String,
    val version: String,
)