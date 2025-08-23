package jp.co.zaico.codingtest.core.data


import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import jp.co.zaico.codingtest.core.model.Inventory
import jp.co.zaico.codingtest.R
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse
import jp.co.zaico.codingtest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ZaicoRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : ZaicoRepository {
    override suspend fun getInventories(): Result<List<Inventory>> = withContext(ioDispatcher) {
        try {
            val response: HttpResponse = httpClient.get(
                String.format("%s/api/v1/inventories", context.getString(R.string.api_endpoint))
            ) {
                header("Authorization", String.format("Bearer %s", context.getString(R.string.api_token)))
            }
            println("response json = ${response.bodyAsText()}")
            if (response.status != HttpStatusCode.OK) {
                // TODO:エラーメッセージ返すようにしたい
                throw RuntimeException("http status is not OK")
            } else {
                val data: List<Inventory> = response.body()
                return@withContext Result.Success(data)
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getInventory(inventoryId: Int): Result<Inventory> = withContext(ioDispatcher) {
        try {
            val response: HttpResponse = httpClient.get(
                String.format("%s/api/v1/inventories/%d", context.getString(R.string.api_endpoint), inventoryId)
            ) {
                header("Authorization", String.format("Bearer %s", context.getString(R.string.api_token)))
            }
            println("response json = ${response.bodyAsText()}")
            if (response.status != HttpStatusCode.OK) {
                // TODO:エラーメッセージ返すようにしたい
                throw RuntimeException("http status is not OK")
            } else {
                val data: Inventory = response.body()
                return@withContext Result.Success(data)
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun addInventory(request: AddInventoryRequest): Result<AddInventoryResponse> = withContext(ioDispatcher) {
        try {
            val response = httpClient.post(
                urlString = String.format("%s/api/v1/inventories", context.getString(R.string.api_endpoint))
            ) {
                header("Authorization", String.format("Bearer %s", context.getString(R.string.api_token)))
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            println("response json = ${response.bodyAsText()}")
            if (response.status != HttpStatusCode.OK) {
                // TODO:エラーメッセージ返すようにしたい
                throw RuntimeException("http status is not OK")
            } else {
                val data: AddInventoryResponse = response.body()
                return@withContext Result.Success(data)
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}