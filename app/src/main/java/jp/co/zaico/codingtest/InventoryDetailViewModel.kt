package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.Inventory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryDetailViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class DataFetched(val data: Inventory) : UiState
        data class Error(val e: Throwable?) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    // データ取得
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

    fun setUiStateError(e: Exception? = null) {
        _uiState.value = UiState.Error(e)
    }
}