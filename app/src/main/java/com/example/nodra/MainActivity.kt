package com.example.nodra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nodra.ui.theme.NodraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NodraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Nav(modifier = Modifier.padding(innerPadding))
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



