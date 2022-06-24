package com.packforyou.data.models

sealed class CallbackState<T> {
    class Loading<T> : CallbackState<T>()
    data class Success<T>(val data: T) : CallbackState<T>()
    data class Failed<T>(val message: String) : CallbackState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(message: String) = Failed<T>(message)
    }
}