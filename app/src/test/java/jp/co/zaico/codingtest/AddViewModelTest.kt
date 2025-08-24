package jp.co.zaico.codingtest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest : FunSpec({

    lateinit var repository: ZaicoRepository
    lateinit var viewModel: AddViewModel
    val testDispatcher = StandardTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = AddViewModel(repository)
    }

    afterTest {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    context("addInventoryのテスト") {
        test("登録成功した場合、UiStateがSuccessになること") {
            val response = AddInventoryResponse(
                code = 200,
                status = "success",
                message = "Data was successfully created.",
                dataId = 1234
            )
            coEvery { repository.addInventory(any()) } returns Result.Success(response)
            val data = AddInventoryRequest(title = "test")
            runTest(testDispatcher) {
                viewModel.addInventory(data)
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe AddViewModel.UiState.Success(response)
        }

        test("登録失敗した場合、UiStateがErrorになること") {
            val exception = Exception("test exception")
            coEvery { repository.addInventory(any()) } returns Result.Error(exception)
            val data = AddInventoryRequest(title = "test")

            runTest(testDispatcher) {
                viewModel.addInventory(data)
                advanceUntilIdle()
            }

            viewModel.uiState.value shouldBe AddViewModel.UiState.Error(exception)
        }

    }
    context("isAddButtonEnabledのテスト") {
        test("値が入力されていない場合、false") {
            runTest(testDispatcher) {
                var collectedValue: Boolean? = null
                val job = launch {
                    viewModel.isAddButtonEnabled.collect {
                        collectedValue = it
                    }
                }

                advanceUntilIdle()
                collectedValue shouldBe false

                viewModel.onTitleChanged(" ")
                advanceUntilIdle()

                collectedValue shouldBe false

                job.cancel()
            }
        }
        test("値が入力されている場合、true") {
            runTest(testDispatcher) {
                var collectedValue: Boolean? = null
                val job = launch {
                    viewModel.isAddButtonEnabled.collect {
                        collectedValue = it
                    }
                }

                advanceUntilIdle()
                collectedValue shouldBe false

                viewModel.onTitleChanged("a")
                advanceUntilIdle()

                collectedValue shouldBe true

                job.cancel()
            }
        }
        test("clearInputのテスト") {
            runTest(testDispatcher) {
                viewModel.onTitleChanged("test")
                advanceUntilIdle()
                viewModel.clearInput()
                advanceUntilIdle()
                viewModel.title.value shouldBe ""
            }

        }
        test("onTitleChangedのテスト") {
            runTest(testDispatcher) {
                viewModel.onTitleChanged("test")
                advanceUntilIdle()
                viewModel.title.value shouldBe "test"
            }
        }
    }
})
