package com.example.eventlistapp.data.remote.response

data class Events(
    val id: Int?,
    val name: String?,
    val ownerName: String?,
    val beginTime: String?,
    val endTime: String?,
    val quota: Int?,
    val registrants: Int?,
    val description: String?,
    val link: String?,
    val mediaCover: String?
)
