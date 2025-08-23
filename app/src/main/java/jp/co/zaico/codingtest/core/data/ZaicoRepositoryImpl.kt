package jp.co.zaico.codingtest.core.data

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import jp.co.zaico.codingtest.Inventory
import jp.co.zaico.codingtest.R
import jp.co.zaico.codingtest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class ZaicoRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : ZaicoRepository {
    override suspend fun getInventories(): List<Inventory> {
        val response: HttpResponse = httpClient.get(
            String.format("%s/api/v1/inventories", context.getString(R.string.api_endpoint))
        ) {
            header("Authorization", String.format("Bearer %s", context.getString(R.string.api_token)))
        }

        // TODO:結果取れなかった場合の処理
        // TODO:Jsonパースもfor文使わず書けそう
        val items = mutableListOf<Inventory>()

        val jsonText = response.bodyAsText()
        println("response $jsonText")
        val jsonArray: JsonArray = Json.parseToJsonElement(jsonText).jsonArray
        for (json in jsonArray) {
            items.add(
                Inventory(
                    id = json.jsonObject["id"].toString().replace(""""""", "").toInt(),
                    title = json.jsonObject["title"].toString().replace(""""""", ""),
                    quantity = json.jsonObject["quantity"].toString().replace(""""""", "")
                )
            )
        }

        return items.toList()
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getInventory(inventoryId: Int): Inventory {
        val response: HttpResponse = httpClient.get(
            String.format("%s/api/v1/inventories/%d", context.getString(R.string.api_endpoint), inventoryId)
        ) {
            header("Authorization", String.format("Bearer %s", context.getString(R.string.api_token)))
        }

        val jsonText = response.bodyAsText()
        val json = Json.parseToJsonElement(jsonText).jsonObject

        return Inventory(
            id = json["id"].toString().replace(""""""", "").toInt(),
            title = json["title"].toString().replace(""""""", ""),
            quantity = json["quantity"].toString().replace(""""""", "")
        )
    }
}