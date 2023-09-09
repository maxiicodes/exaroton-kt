package com.github.hofmmaxi.exaroton.data

import kotlinx.serialization.Serializable

@Serializable
data class FileInfoData(
    val path: String,
    val name: String,
    val isTextFile: Boolean,
    val isConfigFile: Boolean,
    val isDirectory: Boolean,
    val isLog: Boolean,
    val isReadable: Boolean,
    val isWritable: Boolean,
    val size: Int,
    val children: List<FileInfoData>?
)