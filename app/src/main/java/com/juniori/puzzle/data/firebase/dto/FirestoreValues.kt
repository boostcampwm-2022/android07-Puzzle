package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName

data class StringValue(
    val stringValue: String
)

data class IntegerValue(
    @SerializedName("integerValue") val integerValue: Long
)

data class BooleanValue(
    val booleanValue: Boolean
)

data class EnumValue(
    val enumValue: String
)