package com.juniori.puzzle.data.firebase.dto

data class StructuredQuery(
    val from: List<CollectionSelector> = listOf(
        CollectionSelector(
            collectionId = "videoReal",
            allDescendants = true
        )
    ),
    val where: Filter,
    val orderBy: List<Order>? = null,
    val offset: Int?,
    val limit: Int?
)

data class CollectionSelector(
    val collectionId: String,
    val allDescendants: Boolean = false
)

data class Filter(
    val fieldFilter: FieldFilter
)

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

data class FieldReference(
    val fieldPath: String
)

data class Order(
    val field: FieldReference,
    val direction: String
)
