package com.juniori.puzzle.data.weather

import okhttp3.Address

interface LocationDataSource {
    fun getLatestLocation(): Pair<Double, Double>
    fun getCurrentAddress(lat:Double,long:Double):List<Address>
}