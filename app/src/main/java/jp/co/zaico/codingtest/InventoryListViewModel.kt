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
class InventoryListViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class DataFetched(val data: List<Inventory>) : UiState
        data class Error(val e: Throwable?) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

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
                        println("getInventories error ${it.exception}")
                        _uiState.value = UiState.Error(it.exception)
                    }
                }
            }
        }
    }

    fun setUiStateInitial() {
        _uiState.value = UiState.Initial
    }
}
