package com.github.hofmmaxi.exaroton.entities

import com.github.hofmmaxi.exaroton.data.FileInfoData
import com.github.hofmmaxi.exaroton.data.Response
import com.github.hofmmaxi.exaroton.exceptions.APIRequestException
import com.github.hofmmaxi.exaroton.resources.Servers
import com.github.hofmmaxi.exaroton.utils.Ktor
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


/**
 * @param server The instance of the server the file belongs to
 * @param data The raw json data that's returned from the API
 *
 * @property path The path the file is located on
 * @property name The file's name
 * @property isTextFile Whether the given file is a plain text file
 * @property isConfigFile Whether the given file is used configuring the server
 * @property isDirectory Whether the file represents a directory on the server
 * @property isLog Whether the given file holds logs of the server
 * @property isReadable Whether the file's content can be read
 * @property isWritable Whether the file can be written to
 * @property size Represents the file size in bytes
 * @property children As list of child files if the given file is a directory
 */
class File(private val server: Server, private val data: FileInfoData) {
    var path: String = data.path
        get() = data.path
        set(value) {
            field = value.replace("^/+".toRegex(), "")
        }
    val name: String get() = data.name
    val isTextFile: Boolean get() = data.isTextFile
    val isConfigFile: Boolean get() = data.isConfigFile
    val isDirectory: Boolean get() = data.isDirectory
    val isLog: Boolean get() = data.isLog
    val isReadable: Boolean get() = data.isReadable
    val isWritable: Boolean get() = data.isWritable
    val size: Int get() = data.size
    val children: List<File>? get() = data.children?.map { File(server, it) }

    /**
     * **Updates this file with file information fetched from the API**
     *
     * @return The updated file instance
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getInfo(): File {
        val response = Ktor.client.get(
            Servers.Id.Files.Info.Path(
                Servers.Id.Files.Info(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), path
            )
        ).body<Response<FileInfoData>>()
        if (response.data == null) throw APIRequestException("An error occurred refreshing the file info: ${response.error}")
        return File(server, data)
    }

    /**
     * **Gets the content of this text file**
     *
     * - To read a different file type use: [downloadStream]
     * - To download a file use: [download]
     *
     * @see downloadStream
     * @see download
     * @return The file content as a single string
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun getContent(): String {
        val response = Ktor.client.get(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), path
            )
        )
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred getting the file content")
        return response.bodyAsText()
    }

    // TODO maybe add a sample someday
    /**
     * **Saves the current file to your disk**
     *
     * - To read a text file's content use: [getContent]
     * - To read a file as an input stream use: [downloadStream]
     *
     * @see getContent
     * @see downloadStream
     * @param path The path the downloaded file should be put in to
     * @throws APIRequestException If an error occurred during the API request
     * @throws IOException If an error occurred while writing the file to the filesystem
     * @since 1.0.0
     */
    @Throws(APIRequestException::class, IOException::class)
    suspend fun download(path: Path) {
        val response = Ktor.client.get(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        )
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred downloading the file")
        try {
            withContext(Dispatchers.IO) {
                Files.copy(response.bodyAsChannel().toInputStream(), path, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (exception: IOException) {
            throw IOException("An error occurred writing your file to the filesystem")
        }
    }

    /**
     * **Gets the download stream of this file**
     *
     * - To read a text file use: [getContent]
     * - To download a file use: [download]
     *
     * @see getContent
     * @see download
     * @throws APIRequestException If an error occurred during the API request
     * @return Input stream for file data
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun downloadStream(): InputStream {
        val response = Ktor.client.get(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        )
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred reading the file input stream")
        return response.bodyAsChannel().toInputStream()
    }

    /**
     *  **Writes text content to the current file**
     *
     * - To upload a local file from your disk use [upload]
     * - To upload from an input stream use [upload]
     *
     * @see upload
     * @param content The content the file should be updated with
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun putContent(content: String) {
        val response = Ktor.client.put(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        ) {
            setBody(ByteArrayContent(content.toByteArray(Charsets.UTF_8)))
        }
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred updating the file")
    }

    /**
     * **Uploads a local file to the server**
     *
     * - To write text content to a remote use: [putContent]
     * - To upload from an input stream use: [upload]
     *
     * @see putContent
     * @see upload
     * @param path Path to the file your going to upload
     * @throws APIRequestException If an error occurred during the API request
     * @throws IOException If an error occurred converting the file to an input stream
     * @since 1.0.0
     */
    @Throws(IOException::class, APIRequestException::class)
    suspend fun upload(path: Path) {
        val response = Ktor.client.put(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        ) {
            setBody(Files.newInputStream(path))
        }
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred uploading the file")
    }

    /**
     * **Uploads a file with an input stream**
     *
     * - To write text content to a remote file use: [putContent]
     * - To upload a local file from a path use: [upload]
     *
     * @see putContent
     * @see upload
     * @param stream Input stream that should be uploaded to a file on the server
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun upload(stream: InputStream) {
        val response = Ktor.client.put(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        ) {
            setBody(stream)
        }
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred uploading the file")
    }

    /**
     * **Deletes the file from the server**
     *
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun delete() {
        val response = Ktor.client.delete(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        )
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred deleting the file from the server")
    }

    /**
     * **Creates a directory on the server named after the current file's name**
     *
     * @throws APIRequestException If an error occurred during the API request
     * @since 1.0.0
     */
    @Throws(APIRequestException::class)
    suspend fun createAsDirectory() {
        val response = Ktor.client.put(
            Servers.Id.Files.Data.Path(
                Servers.Id.Files.Data(
                    Servers.Id.Files(
                        Servers.Id(
                            Servers(),
                            server.id
                        )
                    )
                ), this.path
            )
        ) {
            header(HttpHeaders.ContentType, "inode/directory")
        }
        if (response.status !== HttpStatusCode.OK) throw APIRequestException("An error occurred creating this file as a directory")
    }

    // TODO: Implement the setFromObject method
    fun setFromObject() {}
}