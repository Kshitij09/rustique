package com.kshitijpatil.rustique

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kshitijpatil.rustique.ui.theme.RustiqueTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.loadImage(resources, R.drawable.buildings)
        setContent {
            val state by viewModel.stateFlow.collectAsState()
            RustiqueTheme {
                MainScreen(
                    state = state,
                    onGrayscale = { viewModel.turnGrayscale() },
                    onInvert = { viewModel.turnInverted() },
                    onRestore = { viewModel.restore() }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    state: ImageUiState,
    modifier: Modifier = Modifier,
    onGrayscale: () -> Unit = {},
    onInvert: () -> Unit = {},
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
            Text("Hello Android: ${state.architecture}")
            if (state.image != null) {
                Image(
                    bitmap = state.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().weight(1f)
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
                Button(onClick = onInvert) {
                    Text("invert")
                }
                Button(onClick = onRestore) {
                    Text("restore")
                }
            }
        }
    }
}

