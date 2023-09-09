package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.PlayerListData
import com.github.hofmmaxi.exaroton.data.Response
import com.github.hofmmaxi.exaroton.exceptions.APIRequestException
import com.github.hofmmaxi.exaroton.resources.Servers
import com.github.hofmmaxi.exaroton.utils.Ktor
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @property entries A list of all players names this list holds
 * @param name To get the list's name use: [Server.getPlayerLists]
 */
class PlayerList(val name: String, private val server: Server, private val data: PlayerListData) {
    val entries: List<String> get() = data.entries

    /**
     * **Retrieves all player names entered into this list**
     *
     * @return The list of player names
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getEntries(): ImmutableList<String> {
        val response =
            Ktor.client.get(Servers.Id.PlayerLists.List(Servers.Id.PlayerLists(Servers.Id(Servers(), server.id)), name))
                .body<Response<PlayerListData>>()
        if (response.data == null) throw APIRequestException("An error occurred getting player list entries: ${response.error}")
        return response.data.entries.toImmutableList()
    }

    /**
     * **Adds one or more player to the current list**
     *
     * @param entries One or more player names you want to add
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun add(vararg entries: String) {
        val response = Ktor.client.put(
            Servers.Id.PlayerLists.List(
                Servers.Id.PlayerLists(Servers.Id(Servers(), server.id)),
                name
            )
        ) {
            setBody(PlayerListData(entries.toList()))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<Response<List<String>>>()
        if (response.data == null) throw APIRequestException("An error occurred add a player list entry: ${response.error}")
    }

    /**
     * **Removes on or more player names from the current list**
     *
     * @param entries Onr or more player names you want to remove
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun remove(vararg entries: String) {
        val response = Ktor.client.delete(
            Servers.Id.PlayerLists.List(
                Servers.Id.PlayerLists(Servers.Id(Servers(), server.id)),
                name
            )
        ) {
            setBody(PlayerListData(entries.toList()))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<Response<List<String>>>()
        if (response.data == null) throw APIRequestException("An error occurred add a player list entry: ${response.error}")
    }
}