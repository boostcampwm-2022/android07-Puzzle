package com.juniori.puzzle.util

import com.google.gson.Gson
import com.juniori.puzzle.data.firebase.dto.*

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

    fun getPublicVideoQuery(
        orderBy: SortType,
        offset: Int?,
        limit: Int?,
        time: Long,
        likeCount: Long
    ): StructuredQuery {
        return if (orderBy == SortType.NEW) {
            StructuredQuery(
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
                                    field = FieldReference("update_time"),
                                    op = "LESS_THAN_OR_EQUAL",
                                    value = IntegerValue(time)
                                )
                            )
                        )
                    )
                ),
                orderBy = listOf(
                    Order(
                        field = FieldReference(SortType.NEW.value),
                        direction = "DESCENDING"
                    ),
                    Order(
                        field = FieldReference(SortType.LIKE.value),
                        direction = "DESCENDING"
                    )
                ),
                offset = offset,
                limit = limit
            )
        } else {
            StructuredQuery(
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
                                    field = FieldReference("like_count"),
                                    op = "LESS_THAN_OR_EQUAL",
                                    value = IntegerValue(likeCount)
                                )
                            )
                        )
                    )
                ),
                orderBy = listOf(
                    Order(
                        field = FieldReference(SortType.LIKE.value),
                        direction = "DESCENDING"
                    ),
                    Order(
                        field = FieldReference(SortType.NEW.value),
                        direction = "DESCENDING"
                    )
                ),
                offset = offset,
                limit = limit
            )
        }

    }

    fun getPublicVideoWithKeywordQuery(
        orderBy: SortType,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?,
        time: Long,
        likeCount: Long
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
                field = FieldReference(orderBy.value),
                direction = "DESCENDING"
            )
        ),
        offset = offset,
        limit = limit
    )
}