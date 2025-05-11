package com.example.nodra

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RedditViewModel : ViewModel() {
    private val repository = RedditRepository()
    private val _posts = MutableStateFlow<List<RedditVid>>(emptyList())
    val posts: StateFlow<List<RedditVid>> = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _videoPosts = MutableStateFlow<List<RedditVid>>(emptyList())
    val videoPosts: StateFlow<List<RedditVid>> = _videoPosts
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

    private fun fetchVideoPosts() {
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


