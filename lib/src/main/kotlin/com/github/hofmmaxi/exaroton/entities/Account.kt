package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.AccountData

/**
 * @param data The raw json data that's returned from the API
 * @property name Represents your account name
 * @property email Your email that is associated with your account
 * @property verified Indicates if your account's email was verified
 * @property credits The amount of credits which is left on your account
 */
class Account(private val data: AccountData) {
    val name: String get() = data.name
    val email: String get() = data.email
    val verified: Boolean get() = data.verified
    val credits: Float get() = data.credits
}