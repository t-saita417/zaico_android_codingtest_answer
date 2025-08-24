package jp.co.zaico.codingtest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryDetailViewModelTest : FunSpec({
    lateinit var repository: ZaicoRepository
    lateinit var viewModel: InventoryDetailViewModel
    val testDispatcher = StandardTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = InventoryDetailViewModel(repository)
    }

    afterTest {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    context("getInventoryのテスト") {
        test("取得成功した場合、UiStateがDataFetchedになること") {
            val id = 123
            val response = Inventory(
                id = id,
                title = "test",
                quantity = "2"
            )
            coEvery { repository.getInventory(id) } returns Result.Success(response)
            runTest(testDispatcher) {
                viewModel.getInventory(id)
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe InventoryDetailViewModel.UiState.DataFetched(response)
        }

        test("取得失敗した場合、UiStateがErrorになること") {
            val id = 123
            val exception = Exception("test exception")
            coEvery { repository.getInventory(id) } returns Result.Error(exception)

            runTest(testDispatcher) {
                viewModel.getInventory(id)
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe InventoryDetailViewModel.UiState.Error(exception)
        }
    }

    context("setUiStateErrorのテスト") {
        test("setUiStateError実行後、UiStateがエラーになっていること") {
            val exception = Exception("test exception")
            viewModel.setUiStateError(exception)
            viewModel.uiState.value shouldBe InventoryDetailViewModel.UiState.Error(exception)
        }
    }
})
