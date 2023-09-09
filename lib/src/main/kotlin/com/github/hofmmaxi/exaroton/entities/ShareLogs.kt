package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.ShareLogsData

/**
 * @param data The raw json data that's returned from the API
 * @property id The unique identifier of this uploaded log
 * @property url The uploaded log can be found on this URL
 * @property raw This URL points to the raw content of the uploaded log
 */
class ShareLogs(private val data: ShareLogsData) {
    val id: String get() = data.id
    val url: String get() = data.url
    val raw: String get() = data.raw
}