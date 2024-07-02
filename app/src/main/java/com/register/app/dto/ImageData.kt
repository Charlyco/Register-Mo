package com.register.app.dto

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("asset_folder") val assetFolder: String,
    val signature: String,
    val format: String,
    @SerializedName("resource_type") val resourceType: String,
    @SerializedName("secure_url") val secureUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("asset_id") val assetId: String,
    @SerializedName("version_id") val versionId: String,
    val type: String,
    @SerializedName("display_name") val displayName: String,
    val version: Long,
    val url: String,
    @SerializedName("public_id") val publicId: String,
    val tags: List<Any>,
    @SerializedName("original_filename") val originalFilename: String,
    @SerializedName("api_key") val apiKey: String,
    val bytes: Int,
    val width: Int,
    val etag: String,
    val placeholder: Boolean,
    val height: Int
)
