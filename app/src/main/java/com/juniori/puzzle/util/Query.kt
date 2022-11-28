package com.juniori.puzzle.util

import com.juniori.puzzle.data.firebase.dto.BooleanFieldFilter
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.CompositeFilter
import com.juniori.puzzle.data.firebase.dto.FieldReference
import com.juniori.puzzle.data.firebase.dto.Filter
import com.juniori.puzzle.data.firebase.dto.Order
import com.juniori.puzzle.data.firebase.dto.StringFieldFilter
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StructuredQuery
import com.juniori.puzzle.data.firebase.dto.Where

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

    fun getMyVideoWithKeywordQuery(
        uid: String,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) = StructuredQuery(
        where = Where(
            CompositeFilter(
                op = "AND",
                filters = listOf(
                    Filter(
                        StringFieldFilter(
                            field = FieldReference("owner_uid"),
                            op = "EQUAL",
                            value = StringValue(uid)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "GREATER_THAN_OR_EQUAL",
                            value = StringValue(keyword)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "LESS_THAN_OR_EQUAL",
                            value = StringValue("${keyword}힣")
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(toSearch),
                direction = "ASCENDING"
            ),
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

    fun getPublicVideoWithKeywordQuery(
        orderBy: String,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?
    ) = StructuredQuery(
        where = Where(
            CompositeFilter(
                op = "AND",
                filters = listOf(
                    Filter(
                        fieldFilter = BooleanFieldFilter(
                            field = FieldReference("is_private"),
                            op = "EQUAL",
                            value = BooleanValue(false)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "GREATER_THAN_OR_EQUAL",
                            value = StringValue(keyword)
                        )
                    ),
                    Filter(
                        StringFieldFilter(
                            field = FieldReference(toSearch),
                            op = "LESS_THAN_OR_EQUAL",
                            value = StringValue("$${keyword}힣")
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(toSearch),
                direction = "ASCENDING"
            ),
            Order(
                field = FieldReference(orderBy),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )
}