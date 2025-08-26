package jp.co.zaico.codingtest.core.data

import jp.co.zaico.codingtest.core.model.Inventory
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.core.model.AddInventoryResponse

interface ZaicoRepository {
    /**
     * 在庫データ一覧取得
     * @return Result<List<Inventory>>
     */
    suspend fun getInventories(): Result<List<Inventory>>

    /**
     * 在庫データ個別取得
     * @param inventoryId 在庫データのID
     * @return Result<Inventory>
     */
    suspend fun getInventory(inventoryId: Int): Result<Inventory>

    /**
     * 在庫データ作成
     * @param request リクエストパラメータ
     * @return Result<AddInventoryResponse>
     */
    suspend fun addInventory(request: AddInventoryRequest): Result<AddInventoryResponse>
}