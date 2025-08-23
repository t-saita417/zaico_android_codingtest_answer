package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class DataFetched(val data: List<Inventory>) : UiState
        data class Error(val e: Exception?) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun getInventories() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = zaicoRepository.getInventories()
            // TODO:resultのエラーチェック。Result型定義して返却するようにしたい
//            if (result is Result.Success) {
            _uiState.value = UiState.DataFetched(result)
//            } else {
//                _uiState.value = UiState.Error(result.e)
//            }
        }
    }

    fun setUiStateInitial() {
        _uiState.value = UiState.Initial
    }
}
