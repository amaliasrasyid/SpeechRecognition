package com.wisnu.speechrecognition.utils

data class Resource<out T>(
    val status : Status,
    val data :T?,
    val message :String?
){
    companion object{
        fun <T> success (data: T?,message: String? = null) = Resource<T>(Status.SUCCESS,data,message)
        fun <T> error (message: String?,data: T?) = Resource<T>(Status.ERROR,data,message)
        fun <T> loading () = Resource<T>(Status.LOADING,null,null)
    }
}
