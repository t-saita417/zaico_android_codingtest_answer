package jp.co.zaico.codingtest.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddInventoryRequest(
    val title: String,
    val quantity: String? = null,
    val unit: String? = null,
    val category: String? = null,
    val state: String? = null,
    val place: String? = null,
    val etc: String? = null,
    @SerialName("group_tag")
    val groupTag: String? = null,
    @SerialName("user_group")
    val userGroup: String? = null,
    val code: String? = null,
    @SerialName("item_image")
    val itemImage: String? = null,
    @SerialName("stocktake_attributes")
    val stocktakeAttributes: StocktakeAttributes? = null,
    @SerialName("optional_attributes")
    val optionalAttributes: List<OptionalAttribute>? = null,
    @SerialName("quantity_management_attributes")
    val quantityManagementAttributes: QuantityManagementAttributes? = null,
    @SerialName("inventory_history")
    val inventoryHistory: InventoryHistory? = null,
    @SerialName("is_quantity_auto_conversion_by_unit")
    val isQuantityAutoConversionByUnit: String? = null,
    @SerialName("quantity_auto_conversion_by_unit_name")
    val quantityAutoConversionByUnitName: String? = null,
    @SerialName("quantity_auto_conversion_by_unit_factor")
    val quantityAutoConversionByUnitFactor: String? = null
)

@Serializable
data class StocktakeAttributes(
    @SerialName("checked_at")
    val checkedAt: String? = null
)

@Serializable
data class OptionalAttribute(
    val name: String? = null,
    val value: String? = null
)

@Serializable
data class QuantityManagementAttributes(
    @SerialName("order_point_quantity")
    val orderPointQuantity: String? = null
)

@Serializable
data class InventoryHistory(
    val memo: String? = null
)
