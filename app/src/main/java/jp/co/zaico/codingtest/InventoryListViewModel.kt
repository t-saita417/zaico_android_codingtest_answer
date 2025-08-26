package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.ZaicoApiException
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.Inventory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 在庫一覧画面のViewModel
 */
@HiltViewModel
class InventoryListViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {
    sealed interface UiState {
        // 初期状態
        data object Initial : UiState

        // 読み込み中
        data object Loading : UiState

        // データ取得完了
        data class DataFetched(val data: List<Inventory>) : UiState

        // エラー
        data class Error(val e: ZaicoApiException) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    /**
     * 在庫データ一覧取得
     */
    fun getInventories() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            zaicoRepository.getInventories().let {
                println("getInventories result $it")
                when (it) {
                    is Result.Success -> {
                        println("getInventories success ${it.data}")
                        _uiState.value = UiState.DataFetched(it.data)
                    }

                    is Result.Error -> {
                        println("getInventories error ${it.exception.text}")
                        _uiState.value = UiState.Error(it.exception)
                    }
                }
            }
        }
    }

    /**
     * UiStateをInitialに変更
     */
    fun setUiStateInitial() {
        _uiState.value = UiState.Initial
    }
}
