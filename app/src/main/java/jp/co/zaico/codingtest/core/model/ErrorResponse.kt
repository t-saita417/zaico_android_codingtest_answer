package jp.co.zaico.codingtest.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ErrorResponse(
    val code: Int,
    val status: String,
    val message: String
)
