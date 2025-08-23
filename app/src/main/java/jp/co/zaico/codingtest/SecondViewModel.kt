package jp.co.zaico.codingtest

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.zaico.codingtest.core.data.ZaicoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    val zaicoRepository: ZaicoRepository
) : ViewModel() {

    // データ取得
    @SuppressLint("DefaultLocale")
    fun getInventory(inventoryId: Int): Inventory = runBlocking {
        return@runBlocking GlobalScope.async {
            zaicoRepository.getInventory(inventoryId)
        }.await()

    }

}