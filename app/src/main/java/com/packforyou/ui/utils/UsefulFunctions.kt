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

            "Nearest Neighbour" -> {
                Algorithm.NEAREST_NEIGHBOUR
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

            Algorithm.NEAREST_NEIGHBOUR -> {
                "Nearest Neighbour"
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