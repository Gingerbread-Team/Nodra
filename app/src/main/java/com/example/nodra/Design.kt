package com.example.nodra

import androidx.compose.runtime.Composable
import com.halilibo.richtext.ui.RichText
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.request.CachePolicy
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.background
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nodra.ui.theme.AppTheme
import com.example.nodra.ui.theme.DyslexicFont
import com.example.nodra.ui.theme.LocalAppColorScheme
import com.example.nodrah_project.AccessibilitySettings

@Composable
fun RedditPostItem(post: RedditVid, currentSettings: AccessibilitySettings) {

    AppTheme(
        isContrastTheme = currentSettings.isContrast,
        isMonoChrome = currentSettings.isMonochrome
    ) {
        val colors = LocalAppColorScheme.current
        val context = LocalContext.current
        val currentFont = if (currentSettings.useDyslexicFont) DyslexicFont else FontFamily.Default
        var showFullPost by remember { mutableStateOf(false) }
        var liked by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (currentSettings.isContrast) Color.DarkGray else Color(0xFFEDEDED))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = colors.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .background(colors.background)
            ) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = currentSettings.fontSize.sp,
                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                            fontFamily = currentFont,
                            lineHeight = currentSettings.lineHeight.sp,
                            letterSpacing = currentSettings.letterSpacing.sp
                        )
                        Text(
                            text = "Just now",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = currentSettings.fontSize.sp,
                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary.copy(alpha = 0.8f),
                            fontFamily = currentFont,
                            lineHeight = currentSettings.lineHeight.sp,
                            letterSpacing = currentSettings.letterSpacing.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                post.title.takeIf { it.isNotBlank() }?.let {
                    Text(text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = currentSettings.fontSize.sp,
                        color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                        fontFamily = currentFont,
                        lineHeight = currentSettings.lineHeight.sp,
                        letterSpacing = currentSettings.letterSpacing.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Selftext
                if (!post.selftext.isNullOrBlank()) {
                    val previewText = post.selftext!!.take(200)
                    val showText = if (showFullPost) post.selftext!! else "$previewText..."
                    Box(modifier = Modifier.clickable { showFullPost = !showFullPost }) {
                        MarkdownText(content = showText, style = TextStyle(
                            fontSize = currentSettings.fontSize.sp,
                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                            fontFamily = currentFont,
                            lineHeight = currentSettings.lineHeight.sp,
                            letterSpacing = currentSettings.letterSpacing.sp
                        )
                        )
                    }
                    if (!showFullPost && post.selftext!!.length > 200) {
                        Text(
                            text = "Read More",
                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                            fontFamily = currentFont,
                            lineHeight = currentSettings.lineHeight.sp,
                            letterSpacing = currentSettings.letterSpacing.sp,
                            modifier = Modifier.clickable { showFullPost = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (post.videoUrl == null) {
                    // Image
                    post.imageUrl?.takeIf { it.isNotBlank() }?.let {
                        SubredditImage(url = it)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                }

                // Video
                post.videoUrl?.takeIf { it.isNotBlank() }?.let {
                    SubredditVideo(url = it)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                HorizontalDivider()

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PostActionButton(
                        icon = Icons.Default.ThumbUp,
                        label = if (liked) "Liked" else "Like",
                        tint = if (liked) MaterialTheme.colorScheme.primary else Color.Unspecified,
                        onClick = { liked = !liked },
                        currentSettings = currentSettings
                    )

                    PostActionButton(icon = Icons.AutoMirrored.Filled.Comment, label = "Comment", currentSettings = currentSettings)
                    PostActionButton(icon = Icons.Default.Share, label = "Share",currentSettings = currentSettings)
                }
            }
        }
    }
}


@Composable
fun PostActionButton(
    icon: ImageVector,
    label: String,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit = {},
currentSettings: AccessibilitySettings

) {
    val colors = LocalAppColorScheme.current
    val currentFont = if (currentSettings.useDyslexicFont) DyslexicFont else FontFamily.Default

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label,
            style = MaterialTheme.typography.bodySmall,fontSize = currentSettings.fontSize.sp,
            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
            fontFamily = currentFont,
            lineHeight = currentSettings.lineHeight.sp,
            letterSpacing = currentSettings.letterSpacing.sp

        )
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


@OptIn(UnstableApi::class)
@Composable
fun SubredditVideo(url: String) {
    val context = LocalContext.current

    // Function to get video dimensions
    @Composable
    fun getVideoHeight(url: String): Dp {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(url)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            ?.toIntOrNull() ?: 0
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            ?.toIntOrNull() ?: 0
        retriever.release()

        return if (width > 0 && height > 0) {
            // Calculate dynamic height based on screen width or other logic
            val aspectRatio = width.toFloat() / height.toFloat()
            val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
            val dynamicHeight = (screenWidth / aspectRatio).toInt().dp
            dynamicHeight
        } else {
            300.dp // Default height if dimensions are not available
        }
    }

    val videoHeight = getVideoHeight(url)

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
                layoutParams = android.view.ViewGroup.LayoutParams(
                    MATCH_PARENT,
                    videoHeight.value.toInt()
                ) // Use dynamic height
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            }
        },
        update = { view -> view.player = exoPlayer }
    )
}


@Composable
fun MarkdownText(
    content: String,
    style: TextStyle = LocalTextStyle.current
) {
    RichText {
        Markdown(content = content)
    }
}