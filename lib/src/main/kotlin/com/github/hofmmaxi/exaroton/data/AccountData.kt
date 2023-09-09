package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class AccountData(val name: String, val email: String, val verified: Boolean, val credits: Float)