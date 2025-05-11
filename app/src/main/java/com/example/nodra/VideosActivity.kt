package com.example.nodra
import androidx.media3.common.MediaItem


import android.annotation.SuppressLint

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
//
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.nodra.ui.theme.NodraTheme

class VideosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NodraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideosScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}




@Composable
fun VideosScreen(
    modifier: Modifier = Modifier,
    viewModel: RedditViewModel = viewModel()
) {
    val videoPosts by viewModel.videoPosts.collectAsState()
    val isVideoLoading by viewModel.isVideoLoading.collectAsState()
    val videoError by viewModel.videoError.collectAsState()

    Scaffold(
        containerColor = Color.Black // خلفية داكنة مثل Reels
    ) { paddingValues ->
        when {
            isVideoLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            videoError != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = videoError!!, color = Color.White)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(videoPosts) { post ->
                        post.videoUrl?.let { url ->
                            ReelsStyleVideoItem(videoUrl = url, title = post.title)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPostItem(post: RedditVid) {
    Column(modifier = Modifier.padding(12.dp)) {
        post.title.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        // Video handling
        post.videoUrl?.let { url ->
            val uri = Uri.parse(url)
            SubredditVideo(uri = uri) // Now using Uri
        }
    }
}
@Composable
fun ReelsStyleVideoItem(videoUrl: String, title: String) {
    val uri = remember(videoUrl) { Uri.parse(videoUrl) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color.Black)
            .clip(RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp)
    ) {
        SubredditVideo(uri = uri)

        // Overlay title text
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .background(Color(0x88000000), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }
    }
}
@SuppressLint("OpaqueUnit")
@Composable
fun SubredditVideo(uri: Uri) {
    val context = LocalContext.current

    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder().setUri(uri).build()
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                useController = false
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}
fun createMediaItemFromUri(uri: Uri): MediaItem {
    return MediaItem.Builder()
        .setUri(uri)
        .build()
}

