package jp.co.zaico.codingtest.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddInventoryResponse(
    val code: Int,
    val status: String,
    val message: String,
    @SerialName("data_id")
    val dataId: Int? = null
)