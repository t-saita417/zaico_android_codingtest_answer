package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {
    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data object Success : UiState
        data class Error(val e: Throwable?) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun addInventory(data: AddInventoryRequest) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            zaicoRepository.addInventory(data).let {
                when (it) {
                    is Result.Success -> {
                        _uiState.value = UiState.Success
                    }

                    is Result.Error -> {
                        _uiState.value = UiState.Error(it.exception)
                    }
                }
            }
        }
    }
}