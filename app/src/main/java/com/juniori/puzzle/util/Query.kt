package com.juniori.puzzle.util

import com.juniori.puzzle.data.firebase.dto.BooleanFieldFilter
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.FieldReference
import com.juniori.puzzle.data.firebase.dto.Filter
import com.juniori.puzzle.data.firebase.dto.Order
import com.juniori.puzzle.data.firebase.dto.StringFieldFilter
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StructuredQuery

object QueryUtil {
    fun getMyVideoQuery(uid: String, offset: Int?, limit: Int?) = StructuredQuery(
        where = Filter(
            fieldFilter = StringFieldFilter(
                field = FieldReference("owner_uid"),
                op = "EQUAL",
                value = StringValue(uid)
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference("update_time"),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )

    fun getPublicVideoQuery(orderBy: String, offset: Int?, limit: Int?) = StructuredQuery(
        where = Filter(
            fieldFilter = BooleanFieldFilter(
                field = FieldReference("is_private"),
                op = "EQUAL",
                value = BooleanValue(false)
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(orderBy),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )
}