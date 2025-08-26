package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoApiException
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.Inventory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 在庫詳細画面のViewModel
 * @param zaicoRepository ZaicoRepository
 */
@HiltViewModel
class InventoryDetailViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    sealed interface UiState {
        // 初期状態
        data object Initial : UiState

        // 読み込み中
        data object Loading : UiState

        // データ取得完了
        data class DataFetched(val data: Inventory) : UiState

        // エラー
        data class Error(val e: ZaicoApiException? = null) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    /**
     * 在庫データ所得
     * @param inventoryId 在庫データのID
     */
    fun getInventory(inventoryId: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            zaicoRepository.getInventory(inventoryId).let {
                println("getInventory result $it")
                when (it) {
                    is Result.Success -> {
                        println("getInventory success ${it.data}")
                        _uiState.value = UiState.DataFetched(it.data)
                    }

                    is Result.Error -> {
                        println("getInventory error ${it.exception}")
                        _uiState.value = UiState.Error(it.exception)
                    }
                }
            }
        }
    }

    /**
     * UiStateをErrorに変更
     */
    fun setUiStateError() {
        _uiState.value = UiState.Error()
    }
}