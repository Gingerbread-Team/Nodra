package com.example.nodra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nodra.navigation.authNavGraph
import com.example.nodra.repository.AuthRepository
import com.example.nodra.ui.theme.AppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val authRepository = AuthRepository(auth)
        Log.d("FirebaseCheck", "Current user: ${auth.currentUser?.email ?: "null"}")

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val startDestination = remember {
                    if (authRepository.isUserLoggedIn()) "home" else "auth"
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        authNavGraph(
                            navController = navController,
                            authRepository = authRepository
                        )

                        composable("home") {
                            MainScreen()
                        }
                    }
//                    Nav(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}

@Composable
fun Nav(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Button(
        onClick =
        {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }, modifier = Modifier.padding(top = 81.dp)) {Text(text = "start")

    }
}



