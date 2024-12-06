package com.example.flo

data class ProfileData (
    val title: String,
    val description: String,
    val album: Int
)

data class AlbumData (
    val title: String,
    val coverImg: Int,
    val singer: String
)