package com.juniori.puzzle.util


import com.juniori.puzzle.data.firebase.dto.BooleanFieldFilter
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.CompositeFilter
import com.juniori.puzzle.data.firebase.dto.FieldReference
import com.juniori.puzzle.data.firebase.dto.Filter
import com.juniori.puzzle.data.firebase.dto.IntegerFieldFilter
import com.juniori.puzzle.data.firebase.dto.IntegerValue
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
                            op = "ARRAY_CONTAINS",
                            value = StringValue(keyword)
                        )
                    )
                )
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


    fun getPublicVideoQuery(
        orderBy: SortType,
        offset: Int?,
        limit: Int?,
        latestData: Long?,
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
                        fieldFilter = IntegerFieldFilter(
                            field = FieldReference(orderBy.value),
                            op = "LESS_THAN_OR_EQUAL",
                            value = IntegerValue(latestData?:Long.MAX_VALUE)
                        )
                    )
                )
            )
        ),
        orderBy = listOf(
            Order(
                field = FieldReference(orderBy.value),
                direction = "DESCENDING"
            ),
            Order(
                field = FieldReference(when(orderBy){
                    SortType.NEW -> SortType.LIKE.value
                    SortType.LIKE -> SortType.NEW.value
                }),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )

    fun getPublicVideoWithKeywordQuery(
        orderBy: SortType,
        toSearch: String,
        keyword: String,
        latestData: Long?,
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
                            op = "ARRAY_CONTAINS",
                            value = StringValue(keyword)
                        )
                    ),
                    Filter(
                        IntegerFieldFilter(
                            field = FieldReference(orderBy.value),
                            op = "LESS_THAN_OR_EQUAL",
                            value = latestData?.let { IntegerValue(it) }
                                ?: IntegerValue(Long.MAX_VALUE)
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
                field = FieldReference(orderBy.value),
                direction = "DESCENDING"
            ),
            Order(
                field = FieldReference(when(orderBy){
                    SortType.NEW -> SortType.LIKE.value
                    SortType.LIKE -> SortType.NEW.value
                }),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )
}