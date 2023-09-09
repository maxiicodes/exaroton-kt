package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.*
import com.github.hofmmaxi.exaroton.exceptions.APIRequestException
import com.github.hofmmaxi.exaroton.resources.Servers
import com.github.hofmmaxi.exaroton.utils.Ktor
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @param data The raw json data that's returned from the API
 *
 * @property id Your server's unique identifier
 * @property name The name you've given to your server
 * @property address Your server's full address (eg. example.exaroton.me)
 * @property motd Your server's MOTD
 * @property status The current status of your server, see [ServerStatus]
 * @property host The server's host address, only available if online
 * @property port The port the server can be accessed on, only available if online
 * @property playerInfo Information about players on the server, see [PlayerInfo]
 * @property software Information about the installed software the server is running, see [Software]
 * @property shared Indicates whether the server is accessed via the Share Access feature
 * @property fetched Indicates if this server has been fetched from the API yet
 */
class Server(private val data: ServerData) {
    val id: String get() = data.id
    val name: String get() = data.name
    val address: String get() = data.address
    val motd: String get() = data.motd
    val status: ServerStatus get() = ServerStatus.fromStatusInt(data.status)
    val host: String? get() = data.host
    val port: Int? get() = data.port
    val playerInfo: PlayerInfo get() = PlayerInfo(data.players)
    val software: Software? = if (data.software !== null) Software(data.software) else null
    val shared: Boolean get() = data.shared

    var fetched: Boolean = false

    val scope = CoroutineScope(Dispatchers.Default)
    internal lateinit var socket: DefaultWebSocketSession

    /**
     * **Check the current state of your server**
     * @param status status codes, see [ServerStatus]
     * @return true if a status matches, else false
     * @since 1.0.0
     */
    fun hasStatus(vararg status: ServerStatus): Boolean {
        for (statusCode in status) {
            if (this.status == statusCode) return true
        }
        return false
    }

    /**
     * **Fetches the MOTD from the API**
     *
     * - To retrieve the cached MOTD use [motd]
     * @return Your server's current MOTD returned from the API
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun fetchMotd(): String {
        val response = Ktor.client
            .get(Servers.Id.Options.Motd(Servers.Id.Options(Servers.Id(id = this.id))))
            .body<Response<MotdData>>()
        if (response.data == null) throw APIRequestException("An error occurred fetching the motd of your server: ${response.error} ")
        return Motd(response.data).motd
    }

    /**
     * **Updates the MOTD to a new one**
     * @param motd The new MOTD you want to set
     * @return The new MOTD you've set
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun setMotd(motd: String): String {
        val response = Ktor.client.post(Servers.Id.Options.Motd(Servers.Id.Options(Servers.Id(id = this.id)))) {
            setBody(MotdData(motd))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<Response<MotdData>>()
        if (response.data == null) throw APIRequestException("An error occurred setting a new motd on: ${response.error}")
        return Motd(response.data).motd
    }

    /**
     * **Fetches the server from the API**
     * @return The updated server object
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun get(): Server {
        val response = Ktor.client.get(Servers.Id(Servers(), data.id)).body<Response<ServerData>>()
        if (response.data == null) throw APIRequestException("An error occurred fetching your server: ${response.error}")
        return setFromObject(Server(response.data))
    }

    /**
     * **Gets the current server log**
     * @return Your server's log
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getLogs(): Logs {
        val response = Ktor.client.get(Servers.Id.Logs(Servers.Id(Servers(), data.id))).body<Response<LogsData>>()
        if (response.data == null) throw APIRequestException("An error occurred getting your server logs: ${response.error}")
        return Logs(response.data)
    }

    /**
     * **Uploads your server's logs to [mclo.gs](https://mclo.gs)**
     * @return An object with the links to your uploaded logs
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun shareLogs(): ShareLogs {
        val response = Ktor.client.get(Servers.Id.Logs.Share(Servers.Id.Logs(Servers.Id(Servers(), data.id))))
            .body<Response<ShareLogsData>>()
        if (response.data == null) throw APIRequestException("An error occurred uploading your logs: ${response.error}")
        return ShareLogs(response.data)
    }

    /**
     * **Gets the amount of ram in GB that's currently configured for your server**
     * @return Your RAM amount currently configured
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getRam(): Int {
        val response = Ktor.client.get(Servers.Id.Options.Ram(Servers.Id.Options(Servers.Id(Servers(), data.id))))
            .body<Response<RamData>>()
        if (response.data == null) throw APIRequestException("An error occurred getting your server's ram: ${response.error}")
        return Ram(response.data).ram
    }

    /**
     * **Updates the amount of RAM for your server**
     * @param ram The amount it should be set to
     * @return The updated RAM amount
     * @throws APIRequestException If an error occurred during the API request
     * @throws IllegalArgumentException If the passed amount of RAM is smaller than 2 or bigger than 16 GB
     * @since 1.0.0
     */
    @Throws(APIRequestException::class, IllegalArgumentException::class)
    suspend fun setRam(ram: Int): Int {
        if (ram !in 2..16) throw IllegalArgumentException("The amount of RAM can only be set to an Integer between 2 and 16")
        val response = Ktor.client.post(Servers.Id.Options.Ram(Servers.Id.Options(Servers.Id(Servers(), data.id)))) {
            setBody(RamData(ram))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<Response<RamData>>()
        if (response.data == null) throw APIRequestException("An error occurred updating your server's RAM size: ${response.error}")
        return Ram(response.data).ram
    }

    /**
     * **Starts your server**
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun start() {
        val response = Ktor.client.get(Servers.Id.Start(Servers.Id(Servers(), data.id))).body<Response<String>>()
        if (response.error !== null) throw APIRequestException("An error occurred starting the server: ${response.error}")
    }

    /**
     * **Starts your server**
     * @param useOwnCredits Use the credits of the account that created the API key instead of the server owner's credits
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    suspend fun start(useOwnCredits: Boolean = false) {
        val response = Ktor.client.post(Servers.Id.Start(Servers.Id(Servers(), data.id))) {
            setBody(StartWithOwnCreditsData(useOwnCredits))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    /**
     * **Stops your server if it's currently running**
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun stop() {
        val response = Ktor.client.get(Servers.Id.Stop(Servers.Id(Servers(), data.id))).body<Response<String>>()
        if (response.error !== null) throw APIRequestException("An error occurred stopping your server: ${response.error}")
    }

    /**
     * **Requests a restart of your server**
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun restart() {
        val response = Ktor.client.get(Servers.Id.Restart(Servers.Id(Servers(), data.id))).body<Response<String>>()
        if (response.error !== null) throw APIRequestException("An error occurred restarting your server: ${response.error}")
    }

    /**
     * **Executes a command on your running server**
     * @param command The command you want to execute
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun executeCommand(command: String) {
        val response = Ktor.client.post(Servers.Id.Command(Servers.Id(Servers(), data.id))) {
            setBody(CommandData(command))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<Response<CommandData>>()
        if (response.error !== null) throw APIRequestException("An error occurred executing the command: ${response.error}")
    }

    /**
     * **Returns a list of all player names that are currently connected to the server**
     * @return A list of all player names
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getPlayerLists(): ImmutableList<String> {
        val response =
            Ktor.client.get(Servers.Id.PlayerLists(Servers.Id(Servers(), data.id))).body<Response<PlayerListData>>()
        if (response.data == null) throw APIRequestException("An error occurred getting the PlayerLists: ${response.error}")
        return response.data.entries.toImmutableList()
    }

    /**
     * **Gets a specific player list with its entries**
     * @param name The player list's name, use [getPlayerLists] to retrieve it
     * @see getPlayerLists
     * @return The specific player list with all it's entries
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getPlayerList(name: String): PlayerList {
        val response =
            Ktor.client.get(Servers.Id.PlayerLists.List(Servers.Id.PlayerLists(Servers.Id(Servers(), data.id)), name))
                .body<Response<List<String>>>()
        if (response.data == null) throw APIRequestException("An error occurred getting the $name player list: ${response.error}")
        return PlayerList(name, this, PlayerListData(response.data))
    }

    /**
     * **Retrieves a file from our server**
     *
     * @param path The path the file or directory is located in,
     * use "/" to list the root
     * @return The file object of the requested file
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getFile(path: String): File {
        val response = Ktor.client.get(
            Servers.Id.Files.Info.Path(
                Servers.Id.Files.Info(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            this.id
                        )
                    )
                ), path
            )
        ).body<Response<FileInfoData>>()
        if (response.data == null) throw APIRequestException("An occurred getting the file from the server")
        return File(this, response.data)
    }

    private fun setFromObject(server: Server): Server {
        return Server(
            ServerData(
                id = server.id,
                name = server.name,
                address = server.address,
                motd = server.motd,
                status = server.status.ordinal,
                host = server.host,
                port = server.port,
                players = server.playerInfo.data,
                software = server.software?.data,
                shared = server.shared
            )
        ).apply {
            fetched = true
        }
    }

    // TODO implement WebSocket API
    suspend fun subscribe() {
        scope.launch {
            socket =
                Ktor.client.webSocketSession(method = HttpMethod.Get, path = "/servers/${this@Server.id}/websocket")
        }
    }

    @Throws(RuntimeException::class)
    suspend fun unsubscribe() {
        if (socket.isActive) socket.close(
            CloseReason(
                CloseReason.Codes.GOING_AWAY,
                ""
            )
        ) else throw RuntimeException("No websocket connection active")
    }

    suspend fun addStatusSubscriber() {

    }
}