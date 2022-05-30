package com.packforyou.data.dijkstrasDataClases

data class Edge<T, E : Number>(
    val from: T, val to: T,
    val value: E
)