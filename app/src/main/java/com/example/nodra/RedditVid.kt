package com.example.nodra

data class RedditVid(
    val author: String,
    val title: String,
    val selftext: String?, // ← هذا هو المطلوب
    val imageUrl: String?,
    val videoUrl: String?
)