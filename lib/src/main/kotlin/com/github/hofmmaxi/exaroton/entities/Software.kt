package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.SoftwareData

/**
 * @property id The unique identifier of this software
 * @property name The name of this software
 * @property version The software's version code
 */
class Software(internal val data: SoftwareData) {
    val id: String get() = data.id
    val name: String get() = data.name
    val version: String get() = data.version
}