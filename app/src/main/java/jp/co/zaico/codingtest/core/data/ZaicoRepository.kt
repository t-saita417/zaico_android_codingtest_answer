package jp.co.zaico.codingtest.core.data

import jp.co.zaico.codingtest.core.model.Inventory
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse

interface ZaicoRepository {
    suspend fun getInventories(): Result<List<Inventory>>
    suspend fun getInventory(inventoryId: Int): Result<Inventory>
    suspend fun addInventory(request: AddInventoryRequest): Result<AddInventoryResponse>
}