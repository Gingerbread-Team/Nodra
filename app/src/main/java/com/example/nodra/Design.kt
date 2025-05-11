package com.example.nodra
import androidx.compose.runtime.Composable
import com.halilibo.richtext.ui.RichText


import android.annotation.SuppressLint
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.request.CachePolicy
import com.halilibo.richtext.markdown.Markdown

@Composable
fun RedditPostItem(post: RedditVid) {
    var showFullPost by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            // Header (Profile + Author + Time/More)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Just now", //
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title or Selftext
            post.title.takeIf { it.isNotBlank() }?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Truncate the selftext if needed
            if (!post.selftext.isNullOrBlank()) {
                val truncatedText = post.selftext!!.take(200) // Show first 200 characters
                Text(
                    text = if (showFullPost) post.selftext!! else "$truncatedText...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        // Toggle between full post and truncated version
                        showFullPost = !showFullPost
                    }
                )
                if (!showFullPost) {
                    Text(
                        text = "Read More",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            // Toggle between full post and truncated version
                            showFullPost = !showFullPost
                        }
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Image
            post.imageUrl?.let { url ->
                SubredditImage(url = url)
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Video
            post.videoUrl?.let { url ->
                SubredditVideo(url = url)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Divider()

            // Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostActionButton(icon = Icons.Default.ThumbUp, label = "Like")
                PostActionButton(icon = Icons.Default.Comment, label = "Comment")
                PostActionButton(icon = Icons.Default.Share, label = "Share")
            }
        }
    }
}

@Composable
fun PostActionButton(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clickable { }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
fun SubredditImage(url: String) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = remember(url) { // Use remember with url
                ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.DISABLED) // Disable memory cache
                    .diskCachePolicy(CachePolicy.DISABLED)   // Disable disk cache
                    .build()
            },
            contentDescription = "Reddit Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            onLoading = { loading = true },
            onSuccess = { loading = false },
            onError = { error = true; loading = false }
        )

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (error) {
            Text("Failed to load image", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@SuppressLint("OpaqueUnit")
@Composable
fun SubredditVideo(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            playWhenReady = false
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
                layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, 400)
                useController = true
            }
        },
        update = { view ->
            view.player = exoPlayer
        }
    )
}
@Composable
fun MarkdownText(
    content: String
) {
    RichText {
        Markdown(content = content)
    }
}