package com.example.nodra

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RedditViewModel : ViewModel() {
    private val repository = RedditRepository()
    private val _posts = MutableStateFlow<List<RedditPost>>(emptyList())
    val posts: StateFlow<List<RedditPost>> = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _videoPosts = MutableStateFlow<List<RedditPost>>(emptyList())
    val videoPosts: StateFlow<List<RedditPost>> = _videoPosts
    private val _isVideoLoading = MutableStateFlow(false)
    val isVideoLoading: StateFlow<Boolean> = _isVideoLoading
    private val _videoError = MutableStateFlow<String?>(null)
    val videoError: StateFlow<String?> = _videoError

    init {
        fetchPosts()
        fetchVideoPosts() // Fetch video posts when ViewModel is created
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _posts.value = repository.fetchAndProcessPosts()
            } catch (e: Exception) {
                _error.value = "Failed to load posts: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchVideoPosts() {
        viewModelScope.launch {
            _isVideoLoading.value = true
            _videoError.value = null
            try {
                val allPosts = repository.fetchAndProcessPosts()
                _videoPosts.value = repository.filterVideoPosts(allPosts)
            } catch (e: Exception) {
                _videoError.value = "Failed to load video posts: ${e.localizedMessage}"
            } finally {
                _isVideoLoading.value = false
            }
        }
    }
}