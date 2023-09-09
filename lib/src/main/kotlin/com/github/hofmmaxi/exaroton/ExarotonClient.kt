package com.github.hofmmaxi.exaroton

import com.github.hofmmaxi.exaroton.data.AccountData
import com.github.hofmmaxi.exaroton.data.Response
import com.github.hofmmaxi.exaroton.data.ServerData
import com.github.hofmmaxi.exaroton.entities.Account
import com.github.hofmmaxi.exaroton.entities.Server
import com.github.hofmmaxi.exaroton.exceptions.APIRequestException
import com.github.hofmmaxi.exaroton.resources.Servers
import com.github.hofmmaxi.exaroton.utils.Ktor
import com.github.hofmmaxi.exaroton.utils.cleanServerId
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.github.hofmmaxi.exaroton.resources.Account as AccountResource

class ExarotonClient(private var token: String) {

    init {
        Ktor.token = this.token
    }

    /**
     * **Returns you the token your currently using**
     * @return Your current token
     * @since 1.0.0
     */
    fun getToken(): String = this.token

    /**
     * **Changes the token which is used for making requests**
     * @param token The token you want to set
     * @return Your newly set token
     * @since 1.0.0
     */
    fun setToken(token: String): ExarotonClient {
        this.token = token
        return this
    }

    /**
     * **Retrieves info about your account**
     * @return Your account info
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getAccount(): Account {
        val response = Ktor.client.get(AccountResource()).body<Response<AccountData>>()
        if (response.data == null) throw APIRequestException("An error occurred while getting account info: ${response.error}")
        return Account(response.data)
    }

    /**
     * **Returns a list of all servers you've created**
     * @return A list of all your servers
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getServers(): ImmutableList<Server> {
        val response = Ktor.client.get(Servers()).body<Response<List<ServerData>>>()
        if (response.data == null) throw APIRequestException("An error occurred while requesting all servers: ${response.error}")
        return response.data.map { Server(it) }.toImmutableList()
    }

    /**
     * **Gets a specific server by its ID**
     * @param id The server's unique identifier
     * @throws APIRequestException If an error occurred during the API request
     * @return An instance of your server
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getServer(id: String): Server {
        val response = Ktor.client.get(Servers.Id(id = cleanServerId(id))).body<Response<ServerData>>()
        if (response.data == null) throw APIRequestException("An error occurred requesting your server with id $id: ${response.error}")
        return Server(response.data)
    }

    /**
     * **Gets the current exaroton server using the `EXAROTON_SERVER_ID` environment variable.**
     *
     * If the environment variable is not set, it returns null.
     * @return The requested server or null if the ID is not found in the environment
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getCurrentServer(): Server? {
        val id = System.getenv("EXAROTON_SERVER_ID") ?: return null
        return getServer(id)
    }

}