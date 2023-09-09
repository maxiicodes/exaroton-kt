package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.CommandData


// FIXME: is this even needed?
/**
 * @param data The raw json data that's returned from the API
 * @property command
 */
class Command(val data: CommandData) {
    val command: String? get() = data.command
}