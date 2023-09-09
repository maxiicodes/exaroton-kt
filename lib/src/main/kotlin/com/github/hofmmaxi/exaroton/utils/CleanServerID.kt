package com.github.hofmmaxi.exaroton.utils

internal fun cleanServerId(id: String): String {
    return id.removePrefix("#")
}