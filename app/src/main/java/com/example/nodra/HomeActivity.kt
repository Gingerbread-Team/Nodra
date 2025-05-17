package com.example.nodra

import android.Manifest
import android.app.Activity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.LinearProgressIndicator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.nodra.ui.theme.AppTheme
import com.example.nodra.ui.theme.DyslexicFont
import com.example.nodra.ui.theme.LocalAppColorScheme
import com.example.nodrah_project.AccessibilitySettings
import kotlinx.coroutines.delay
import java.util.Locale

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val talkAndTypeParser by lazy {
                TalkAndType(application)
            }

            val accessibilitySettingsManager =
                (application as MyApplication).accessibilitySettingsManager
            val currentSettings by accessibilitySettingsManager.accessibilitySettingsFlow.collectAsState(
                initial = AccessibilitySettings()
            )
            AppTheme(
                isContrastTheme = currentSettings.isContrast,
                isMonoChrome = currentSettings.isMonochrome,
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentSettings,
                        talkAndTypeParser
                    )

                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentSettings: AccessibilitySettings,
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Video,
        BottomNavItem.Accessibility,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )
    val currentFont = if (currentSettings.useDyslexicFont) DyslexicFont else FontFamily.Default
    val colors = LocalAppColorScheme.current
    val context = LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = colors.onPrimary
                    )
                },
                label = {
                    Text(
                        item.title,
                        color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                        fontFamily = currentFont,

                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (item == BottomNavItem.Accessibility) {
                        val intent = Intent(context, AccessibilityActivity::class.java).apply {
                            // Add these flags for smoother transition
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        }
                        context.startActivity(intent)
                        // Optional: Add custom animation
                        (context as Activity)
                    } else {
                        // Existing navigation code
                    }

                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.secondary,
                    unselectedIconColor = colors.onPrimary,
                    indicatorColor = Color.Transparent
                ),
            )
        }
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    currentSettings: AccessibilitySettings,
    talkAndTypeParser: TalkAndType
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentSettings)
        }
    ) { innerPadding ->

        AppTheme(
            isContrastTheme = currentSettings.isContrast,
            isMonoChrome = currentSettings.isMonochrome,
        ) {
            val colors = LocalAppColorScheme.current
            val currentFont =
                if (currentSettings.useDyslexicFont) DyslexicFont else FontFamily.Default
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(currentSettings)
                }

                composable(BottomNavItem.Video.route) {
                    val viewModel: RedditViewModel = viewModel()
                    val posts by viewModel.videoPosts.collectAsState()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val error by viewModel.error.collectAsState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.background)
                    ) {

                        when {
                            isLoading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = colors.onPrimary)
                                    }
                                }
                            }

                            error != null -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = error ?: "Unexpected Error",
                                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                                        )
                                    }
                                }
                            }

                            else -> {
                                items(posts) { post ->
                                    RedditPostItem(post = post, currentSettings)
                                }
                            }
                        }
                    }
                }

                composable(BottomNavItem.Accessibility.route) {
                    AccessibilityActivity()
                }

                composable(BottomNavItem.Notifications.route) {
                    NotificationScreen()
                }

                composable(BottomNavItem.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

//    @Preview(showBackground = true)
//    @Composable
//    fun MainScreenPreview() {
//        MaterialTheme {
//            MainScreen()
//        }
//    }

@Composable
fun HomeScreen(currentSettings: AccessibilitySettings) {
    val viewModel: RedditViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val colors = LocalAppColorScheme.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        item { HeaderSection(currentSettings) }
        item { StoriesSection(currentSettings) }

        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.onPrimary)
                    }
                }
            }

            error != null -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Unexpected Error",
                            color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                        )
                    }
                }
            }

            else -> {
                items(posts) { post ->
                    RedditPostItem(post = post, currentSettings)
                }
            }
        }
    }
}

@Composable
fun HeaderSection(currentSettings: AccessibilitySettings) {
    val colors = LocalAppColorScheme.current
    val currentFont = if (currentSettings.useDyslexicFont) DyslexicFont else FontFamily.Default

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(colors.background)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Noudrah",
                fontSize = currentSettings.fontSize.sp,
                color = if (currentSettings.isContrast) colors.onBackground else colors.onPrimary,
                fontFamily = currentFont,
                lineHeight = currentSettings.lineHeight.sp,
                letterSpacing = currentSettings.letterSpacing.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Dots Icon",
                    tint = colors.onPrimary
                )
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Mail Icon",
                    tint = colors.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                tint = colors.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Start your Journey...",
                fontSize = currentSettings.fontSize.sp,
                color =  colors.onPrimary,
                fontFamily = currentFont,
                lineHeight = currentSettings.lineHeight.sp,
                letterSpacing = currentSettings.letterSpacing.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.AccountBox, contentDescription = "Profile Icon",
                tint = colors.onPrimary
            )
        }
    }
}

@Composable
fun StoriesSection(currentSettings: AccessibilitySettings) {
    val stories: List<Int?> = listOf(
        null,
        R.drawable.arthur,
        R.drawable.james,
        R.drawable.kratos,
        R.drawable.lara,
        R.drawable.ghost,
        R.drawable.tiger
    )

    val actualStories = stories.filterNotNull() // استبعد null
    var showStories by remember { mutableStateOf(false) }
    var startIndex by remember { mutableStateOf(0) }

    if (showStories) {
        FullscreenStoriesViewer(
            storyImages = actualStories,
            startIndex = startIndex,
            onDismiss = { showStories = false },
            currentSettings
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(stories) { index, item ->
            if (item == null) {
                AddStoryCard()
            } else {
                // لازم نستخدم index - 1 علشان actualStories مبتحسبش null
                StoryImageCard(currentSettings=currentSettings, imageRes = item) {
                    startIndex = index - 1 // اطرح 1 علشان null أول عنصر
                    showStories = true
                }
            }
        }
    }
}

@Composable
fun StoryImageCard(
    currentSettings: AccessibilitySettings,
    @DrawableRes imageRes: Int, onClick: () -> Unit,
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Story Image",
        modifier = Modifier
            .size(width = 120.dp, height = 180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop,
        colorFilter = if (currentSettings.isMonochrome) ColorFilter.colorMatrix(
            ColorMatrix().apply {
                setToSaturation(
                    0f
                )
            }) else null,
    )
}

@Composable
fun AddStoryCard() {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Story",
            tint = Color.DarkGray,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun FullscreenStoriesViewer(
    @DrawableRes storyImages: List<Int>,
    startIndex: Int = 0,
    onDismiss: () -> Unit,
    currentSettings: AccessibilitySettings
) {
    var currentIndex by remember { mutableStateOf(startIndex) }
    val totalStories = storyImages.size
    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
        onDispose {
            systemUiController.isSystemBarsVisible = true
        }
    }

    LaunchedEffect(currentIndex) {
        if (currentIndex < totalStories) {
            delay(2500)
            currentIndex++
        } else {
            onDismiss()
        }
    }

    if (currentIndex < totalStories) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = storyImages[currentIndex]),
                    contentDescription = "Story Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = if (currentSettings.isMonochrome) ColorFilter.colorMatrix(
                        ColorMatrix().apply {
                            setToSaturation(
                                0f
                            )
                        }) else null
                )
            }
        }
    }
}


@Composable
fun VideoScreen() {
    Text("Video Screen")
}


@Composable
fun NotificationScreen() {
    Text("Notification Screenzzz")
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen")
}
