package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.Result
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {
    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class Success(val data: AddInventoryResponse) : UiState
        data class Error(val e: Throwable?) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    // 登録するボタンを有効化するかどうか
    val isAddButtonEnabled: StateFlow<Boolean> = _title.map { title ->
        title.isNotBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun addInventory(data: AddInventoryRequest) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            zaicoRepository.addInventory(data).let {
                println("addInventory result $it")
                when (it) {
                    is Result.Success -> {
                        println("addInventory success ${it.data}")
                        _uiState.value = UiState.Success(it.data)
                    }

                    is Result.Error -> {
                        println("addInventory error ${it.exception}")
                        _uiState.value = UiState.Error(it.exception)
                    }
                }
            }
        }
    }

    fun clearInput() {
        _title.value = ""
    }

    fun onTitleChanged(title: String) {
        _title.value = title
    }
}