package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.RamData

/**
 * @param data The raw json data that's returned from the API
 * @property ram The amount of ram in GB the server is currently assigned to
 */
class Ram(private val data: RamData) {
    val ram: Int get() = data.ram
}