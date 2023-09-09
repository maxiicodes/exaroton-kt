package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.LogsData

/**
 * @property content Represents the whole server logs as a single string
 */
class Logs(private val data: LogsData) {
    val content: String? get() = data.content
}