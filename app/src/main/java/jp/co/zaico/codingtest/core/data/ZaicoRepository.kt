package jp.co.zaico.codingtest.core.data

import jp.co.zaico.codingtest.Inventory
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse

interface ZaicoRepository {
    suspend fun getInventories(): List<Inventory>
    suspend fun getInventory(inventoryId: Int): Inventory
    suspend fun addInventory(request: AddInventoryRequest): Result<AddInventoryResponse>
}