package jp.co.zaico.codingtest.core.data

import android.content.Context
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import jp.co.zaico.codingtest.R
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse
import jp.co.zaico.codingtest.core.model.ErrorResponse
import jp.co.zaico.codingtest.core.model.Inventory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ZaicoRepositoryImplTest : FunSpec({

    lateinit var mockContext: Context
    lateinit var mockHttpClient: HttpClient
    lateinit var repository: ZaicoRepositoryImpl
    val testDispatcher = StandardTestDispatcher()

    val testJson = Json {
        ignoreUnknownKeys = true
    }

    beforeEach {
        mockContext = mockk<Context>(relaxed = true)
        every { mockContext.getString(R.string.api_endpoint) } returns "https://example.com"
        every { mockContext.getString(R.string.api_token) } returns "test_token"
    }

    context("getInventoriesのテスト") {
        test("正常系 Result.Successを返す") {
            runTest(testDispatcher) {
                val mockInventories = listOf(
                    Inventory(id = 1, title = "Item 1", quantity = "10"),
                    Inventory(id = 2, title = "Item 2", quantity = "5")
                )
                val mockResponseJson = testJson.encodeToString(mockInventories)

                val mockEngine = MockEngine { request ->
                    respond(
                        content = mockResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventories()
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Success<List<Inventory>>>()
                result.data shouldBe mockInventories
            }
        }

        test("異常系 HttpStatusCodeが200以外の場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val errorCode = HttpStatusCode.Forbidden
                val errorMessage = "Could not access API. Please Check Token or enabled API function."
                val errorResponse = ErrorResponse(message = errorMessage, status = errorCode.value.toString(), code = errorCode.value)
                val errorResponseJson = testJson.encodeToString(errorResponse)

                val mockEngine = MockEngine {
                    respond(
                        content = errorResponseJson,
                        status = errorCode,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventories()
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe errorMessage
            }
        }

        test("異常系 例外発生した場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val mockEngine = MockEngine {
                    throw IOException("Network error")
                }
                mockHttpClient = HttpClient(mockEngine)
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventories()
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe "Unexpected error occurred."
            }
        }
    }

    context("getInventoryのテスト") {
        test("正常系 Result.Successを返す") {
            runTest(testDispatcher) {
                val mockEngine = MockEngine { request ->
                    respond(
                        content = testJson.encodeToString(Inventory(id = 123, title = "Specific Item", quantity = "1")),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventory(123)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Success<Inventory>>()
                result.data.id shouldBe 123
                result.data.title shouldBe "Specific Item"
            }
        }
        test("異常系 HttpStatusCodeが200以外の場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val errorMessage = "Could not access API. Please Check Token or enabled API function."
                val errorResponse = ErrorResponse(message = errorMessage, status = "error", code = 403)
                val errorResponseJson = testJson.encodeToString(errorResponse)

                val mockEngine = MockEngine {
                    respond(
                        content = errorResponseJson,
                        status = HttpStatusCode.Forbidden,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventory(123)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe errorMessage
            }
        }
        test("異常系 例外発生した場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val mockEngine = MockEngine {
                    throw IOException("test exception")
                }
                mockHttpClient = HttpClient(mockEngine)
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.getInventory(123)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe "Unexpected error occurred."
            }
        }
    }

    context("addInventoryのテスト") {
        val testRequest = AddInventoryRequest(title = "New Item")

        test("正常系 Result.Successを返す") {
            runTest(testDispatcher) {
                val mockResponse = AddInventoryResponse(
                    code = 200,
                    status = "success",
                    message = "Data was successfully created.",
                    dataId = 1234
                )
                val mockResponseJson = testJson.encodeToString(mockResponse)

                val mockEngine = MockEngine { request ->
                    respond(
                        content = mockResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)
                val result = repository.addInventory(testRequest)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Success<AddInventoryResponse>>()
                result.data shouldBe mockResponse
            }
        }

        test("異常系 HttpStatusCodeが200以外の場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val mockErrorPayload = AddInventoryResponse(
                    message = "Could not access API. Please Check Token or enabled API function.",
                    code = 403,
                    status = "error",
                    dataId = null
                )
                val errorResponseJson = testJson.encodeToString(mockErrorPayload)


                val mockEngine = MockEngine {
                    respond(
                        content = errorResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                mockHttpClient = HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(testJson) }
                }
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.addInventory(testRequest)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe "Could not access API. Please Check Token or enabled API function."
            }
        }


        test("異常系 例外発生した場合、Result.Errorを返す") {
            runTest(testDispatcher) {
                val mockEngine = MockEngine {
                    throw RuntimeException("test Exception")
                }
                mockHttpClient = HttpClient(mockEngine)
                repository = ZaicoRepositoryImpl(mockHttpClient, testDispatcher, mockContext)

                val result = repository.addInventory(testRequest)
                advanceUntilIdle()

                result.shouldBeInstanceOf<Result.Error>()
                val exception = result.exception
                exception.shouldBeInstanceOf<ZaicoApiException>()
                exception.text shouldBe "Unexpected error occurred."
            }
        }
    }
})

