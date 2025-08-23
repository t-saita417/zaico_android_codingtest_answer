package jp.co.zaico.codingtest

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    // データ取得
    fun getInventories(): List<Inventory> = runBlocking {
        // TODO:UiState定義してView側に通知する
        return@runBlocking GlobalScope.async {
            return@async zaicoRepository.getInventories()
        }.await()
    }

}
