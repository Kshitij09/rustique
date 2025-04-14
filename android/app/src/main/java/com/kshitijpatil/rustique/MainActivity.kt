package com.kshitijpatil.rustique

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.kshitijpatil.rustique.ui.theme.RustiqueTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var image by mutableStateOf<ImageBitmap?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val lib = Rustique()
        loadImage()
        setContent {
            RustiqueTheme {
                MainScreen(
                    architecture = lib.getArchitecture(),
                    image = image
                )
            }
        }
    }

    private fun loadImage() {
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.minecraft)
            image = bitmap.asImageBitmap()
        }
    }
}

@Composable
fun MainScreen(
    architecture: String,
    image: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            Text("Hello Android: $architecture")
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            }
        }
    }
}