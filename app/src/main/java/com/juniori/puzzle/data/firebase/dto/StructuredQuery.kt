package com.juniori.puzzle.data.firebase.dto

data class StructuredQuery(
    val from: List<CollectionSelector>,
    val where: Filter,
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
    val value: IntegerValue
)

data class FieldReference(
    val fieldPath: String
)
