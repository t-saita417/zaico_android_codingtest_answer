package jp.co.zaico.codingtest.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddInventoryRequest(
    val title: String,
    val quantity: String,
    val unit: String,
    val category: String,
    val state: String,
    val place: String,
    val etc: String,
    @SerialName("group_tag")
    val groupTag: String,
    @SerialName("user_group")
    val userGroup: String,
    val code: String,
    @SerialName("item_image")
    val itemImage: String,
    @SerialName("stocktake_attributes")
    val stocktakeAttributes: StocktakeAttributes,
    @SerialName("optional_attributes")
    val optionalAttributes: List<OptionalAttribute>,
    @SerialName("quantity_management_attributes")
    val quantityManagementAttributes: QuantityManagementAttributes,
    @SerialName("inventory_history")
    val inventoryHistory: InventoryHistory,
    @SerialName("is_quantity_auto_conversion_by_unit")
    val isQuantityAutoConversionByUnit: String,
    @SerialName("quantity_auto_conversion_by_unit_name")
    val quantityAutoConversionByUnitName: String,
    @SerialName("quantity_auto_conversion_by_unit_factor")
    val quantityAutoConversionByUnitFactor: String
)

@Serializable
data class StocktakeAttributes(
    @SerialName("checked_at")
    val checkedAt: String
)

@Serializable
data class OptionalAttribute(
    val name: String,
    val value: String
)

@Serializable
data class QuantityManagementAttributes(
    @SerialName("order_point_quantity")
    val orderPointQuantity: String
)

@Serializable
data class InventoryHistory(
    val memo: String
)
