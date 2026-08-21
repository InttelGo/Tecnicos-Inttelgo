package com.inttelgo.tecnicos.logic.Model

import com.google.gson.annotations.SerializedName

enum class MediaKind {
    IMAGE,
    VIDEO,
    AUDIO,
    UNKNOWN
}

data class EvidenciaMedia(
    val id: String = "",
    @SerializedName(value = "url", alternate = ["link"])
    val url: String = "",
    val nombre: String = "",
    @SerializedName(value = "create_at", alternate = ["fecha"])
    val create_at: String = "",
    val update_at: String = "",
    val create_by: Usuario? = null,
    val update_by: Usuario? = null,
    val ubicacion: String = "",
    val tipo: String = ""
) {
    val displayName: String
        get() = nombre.ifBlank {
            url.substringAfterLast('/').substringBefore('?').ifBlank { "Evidencia" }
        }

    val displayDate: String
        get() = create_at

    fun kind(): MediaKind {
        val tipoNorm = tipo.lowercase()
        if (tipoNorm.contains("audio")) return MediaKind.AUDIO
        if (tipoNorm.contains("video")) return MediaKind.VIDEO
        if (tipoNorm.contains("image") || tipoNorm.contains("foto") || tipoNorm.contains("img")) {
            return MediaKind.IMAGE
        }

        val source = "${nombre.lowercase()} ${url.lowercase()}"
        return when {
            source.contains(Regex("\\.(mp3|wav|m4a|aac|ogg|amr|flac)(\\?|$)", RegexOption.IGNORE_CASE)) ->
                MediaKind.AUDIO
            source.contains(Regex("\\.(mp4|mov|avi|mkv|webm|3gp)(\\?|$)", RegexOption.IGNORE_CASE)) ->
                MediaKind.VIDEO
            source.contains(Regex("\\.(jpg|jpeg|png|gif|webp|heic|bmp)(\\?|$)", RegexOption.IGNORE_CASE)) ->
                MediaKind.IMAGE
            else -> MediaKind.UNKNOWN
        }
    }
}

data class ObservacionConEvidencias(
    val id: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val usuario: Usuario? = null,
    val evidencias: List<EvidenciaMedia>? = emptyList()
)
