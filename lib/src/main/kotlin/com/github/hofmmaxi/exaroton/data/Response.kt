package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val success: Boolean,
    val error: String?,
    val data: T?
)