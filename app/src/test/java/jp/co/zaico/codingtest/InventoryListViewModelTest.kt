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
class InventoryListViewModelTest : FunSpec({
    lateinit var repository: ZaicoRepository
    lateinit var viewModel: InventoryListViewModel
    val testDispatcher = StandardTestDispatcher()

    beforeTest {
        repository = mockk()
        viewModel = InventoryListViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    afterTest {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    context("getInventoriesのテスト") {
        test("取得成功した場合、UiStateがDataFetchedになること") {
            val response = listOf(
                Inventory(
                    id = 1,
                    title = "test",
                    quantity = "2"
                ), Inventory(
                    id = 2,
                    title = "test",
                    quantity = "2"
                ), Inventory(
                    id = 3,
                    title = "test",
                    quantity = "2"
                )
            )
            coEvery { repository.getInventories() } returns Result.Success(response)
            runTest(testDispatcher) {
                viewModel.getInventories()
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe InventoryListViewModel.UiState.DataFetched(response)
        }

        test("取得失敗した場合、UiStateがErrorになること") {
            val exception = Exception("test exception")
            coEvery { repository.getInventories() } returns Result.Error(exception)

            runTest(testDispatcher) {
                viewModel.getInventories()
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe InventoryListViewModel.UiState.Error(exception)
        }
    }

    context("setUiStateInitialのテスト") {
        test("setUiStateInitial実行後、UiStateがInitialになっていること") {
            viewModel.setUiStateInitial()
            viewModel.uiState.value shouldBe InventoryListViewModel.UiState.Initial
        }
    }
})
