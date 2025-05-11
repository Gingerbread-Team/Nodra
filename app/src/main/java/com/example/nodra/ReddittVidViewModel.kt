package com.example.nodra

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RedditVidViewModel : ViewModel() {

    private val _videoPosts = MutableStateFlow<List<RedditVid>>(listOf(
        RedditVid(
            author = "Author1",  // Add an author
            title = "قصص نجاح ذوي الهمم في مصر",
            selftext = "هذه قصة نجاح لأحد الأبطال الذين تحدوا الإعاقة.",
            imageUrl = null, // Optional, can be null
            videoUrl = "https://v.redd.it/h9qe0gq4g10f1/DASH_720.mp4?source=fallback"
       )
,
        RedditVid(
            author = "Author2", // Add an author
            title = "رحلة بطل بارالمبي",
            selftext = "هذه رحلة بطل بارالمبي وكيف حقق أهدافه.",
            imageUrl = null,
            videoUrl = "https://v.redd.it/h9qe0gq4g10f1/DASH_720.mp4?source=fallback"
        ),
        RedditVid(
            author = "Author3", // Add an author
            title = "التعليم الدامج - دعم ذوي الإعاقة",
            selftext = "هذا الفيديو يناقش كيفية دعم ذوي الإعاقة في التعليم.",
            imageUrl = null,
            videoUrl = "https://v.redd.it/h9qe0gq4g10f1/DASH_720.mp4?source=fallback"
        )
    ))

    val videoPosts: StateFlow<List<RedditVid>> = _videoPosts
    val isVideoLoading = MutableStateFlow(false)
    val videoError = MutableStateFlow<String?>(null)
}