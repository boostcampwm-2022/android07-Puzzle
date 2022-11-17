package com.juniori.puzzle.data.firebase.dto

data class StructuredQuery(
    val from: List<CollectionSelector> = listOf(
        CollectionSelector(
            collectionId = "videoReal",
            allDescendants = true
        )
    ),
    val where: Filter,
    val orderBy: List<Order>? = null
)

data class CollectionSelector(
    val collectionId: String,
    val allDescendants: Boolean = false
)

data class Filter(
    val fieldFilter: FieldFilter
)

data class FieldFilter(
    val field: FieldReference,
    val op: String,
    val value: BooleanValue
)

data class FieldReference(
    val fieldPath: String
)

data class Order(
    val field: FieldReference,
    val direction: String
)
