package com.packforyou.ui.utils

import com.packforyou.data.models.Algorithm

object UsefulFunctions {
    fun getAlgorithmGivenString(algorithmString: String): Algorithm {
        return when (algorithmString) {
            "Directions API" -> {
                Algorithm.DIRECTIONS_API
            }

            "Brute Force" -> {
                Algorithm.BRUTE_FORCE
            }

            "Closest Neighbour" -> {
                Algorithm.CLOSEST_NEIGHBOUR
            }

            "Urgency" -> {
                Algorithm.URGENCY
            }

            else -> {
                Algorithm.NOT_ALGORITHM
            }
        }
    }

    fun getStringGivenAlgorithm(algorithm: Algorithm): String {
        return when (algorithm) {
            Algorithm.DIRECTIONS_API -> {
                "Directions API"
            }

            Algorithm.BRUTE_FORCE -> {
                "Brute Force"
            }

            Algorithm.CLOSEST_NEIGHBOUR -> {
                "Closest Neighbour"
            }

            Algorithm.URGENCY -> {
                "Urgency"
            }

            else -> {
                "Custom Algorithm"
            }
        }
    }
}