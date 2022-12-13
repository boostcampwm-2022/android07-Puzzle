package com.juniori.puzzle.data.firebase.dto

data class StructuredQuery(
    val from: List<CollectionSelector> = listOf(
        CollectionSelector(
            collectionId = "videoReal",
            allDescendants = true
        )
    ),
    val where: Filters,
    val orderBy: List<Order>? = null,
    val offset: Int?,
    val limit: Int?
)

data class CollectionSelector(
    val collectionId: String,
    val allDescendants: Boolean = false
)

sealed interface Filters

data class Where(
    val compositeFilter: CompositeFilter
) : Filters

data class CompositeFilter(
    val filters: List<Filter>,
    val op: String
)

data class Filter(
    val fieldFilter: FieldFilter
) : Filters

sealed interface FieldFilter

data class BooleanFieldFilter(
    val field: FieldReference,
    val op: String,
    val value: BooleanValue
) : FieldFilter

data class StringFieldFilter(
    val field: FieldReference,
    val op: String,
    val value: StringValue
) : FieldFilter

data class IntegerFieldFilter(
    val field: FieldReference,
    val op: String,
    val value: IntegerValue
) : FieldFilter

data class FieldReference(
    val fieldPath: String
)

data class Order(
    val field: FieldReference,
    val direction: String
)