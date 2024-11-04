package com.example.eventlistapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Favorite (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "mediaCover")
    val mediaCover: String? = null,

    @ColumnInfo(name = "registrants")
    val registrants: Int? = null,

    @ColumnInfo(name = "imageLogo")
    val imageLogo: String? = null,

    @ColumnInfo(name = "link")
    val link: String? = null,

    @ColumnInfo(name = "ownerName")
    val ownerName: String? = null,

    @ColumnInfo(name = "cityName")
    val cityName: String? = null,

    @ColumnInfo(name = "quota")
    val quota: Int? = null,

    @ColumnInfo(name = "beginTime")
    val beginTime: String? = null,

    @ColumnInfo(name = "endTime")
    val endTime: String? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "date")
    var date: String? = null
) : Parcelable
