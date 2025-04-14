package com.kshitijpatil.rustique

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.kshitijpatil.rustique.ui.theme.RustiqueTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {
    private var image by mutableStateOf<ImageBitmap?>(null)
    private lateinit var bitmap: Bitmap
    private lateinit var originalBuffer: ByteBuffer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val lib = Rustique()
        loadImage()
        setContent {
            RustiqueTheme {
                MainScreen(
                    architecture = lib.getArchitecture(),
                    image = image,
                    onGrayscale = { turnGrayscale(lib) },
                    onRestore = ::restore
                )
            }
        }
    }

    private fun loadImage() {
        lifecycleScope.launch(Dispatchers.IO) {
            val opts = Options().apply {
                inMutable = true
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.minecraft, opts)
            originalBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
            bitmap.copyPixelsToBuffer(originalBuffer)
            originalBuffer.rewind()
            image = bitmap.asImageBitmap()
        }
    }

    private fun turnGrayscale(lib: Rustique) {
        val buffer = ByteBuffer.allocateDirect(originalBuffer.capacity())
        buffer.put(originalBuffer)
        originalBuffer.rewind()
        buffer.rewind()
        lib.processBitmap(buffer, bitmap.width, bitmap.height, bitmap.rowBytes)
        buffer.rewind()
        bitmap.copyPixelsFromBuffer(buffer)
        image = bitmap.asImageBitmap()
    }

    private fun restore() {
        bitmap.copyPixelsFromBuffer(originalBuffer)
        originalBuffer.rewind()
        image = bitmap.asImageBitmap()
    }
}

@Composable
fun MainScreen(
    architecture: String,
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
    onGrayscale: () -> Unit = {},
    onRestore: () -> Unit = {},
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
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onGrayscale) {
                    Text("gray")
                }
                Button(onClick = onRestore) {
                    Text("restore")
                }
            }
        }
    }
}