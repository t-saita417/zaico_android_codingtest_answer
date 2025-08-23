package jp.co.zaico.codingtest.core.data

import jp.co.zaico.codingtest.Inventory

interface ZaicoRepository {
    suspend fun getInventories(): List<Inventory>
    suspend fun getInventory(inventoryId: Int): Inventory
    //TODO:登録処理を追加
}