package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName

data class StringValue(
    @SerializedName("stringValue") val stringValue: String
)

data class IntegerValue(
    @SerializedName("integerValue") val integerValue: Long
)

data class BooleanValue(
    @SerializedName("booleanValue") val booleanValue: Boolean
)

data class ArrayValue(
    @SerializedName("arrayValue") val arrayValue: StringValues
)

data class StringValues(
    @SerializedName("values") val values: List<StringValue>
)
