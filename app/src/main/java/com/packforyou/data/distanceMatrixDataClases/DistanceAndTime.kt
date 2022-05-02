package com.packforyou.data.distanceMatrixDataClases

data class DistanceAndTime(
    val destination_addresses: List<String>,
    val origin_addresses: List<String>,
    val rows: List<Row>,
    val status: String
)