package com.kshitijpatil.rustique

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

enum class PreviewState {
    Undefined,
    Original,
    Grayscale,
    Inverted
}

data class ImageUiState(
    val previewState: PreviewState = PreviewState.Undefined,
    val image: ImageBitmap? = null,
    val architecture: String? = null,
)


class ImageViewModel : ViewModel() {
    private val _state = MutableStateFlow(ImageUiState())
    val stateFlow: StateFlow<ImageUiState> = _state.asStateFlow()
    private val state: ImageUiState get() = stateFlow.value
    private var bitmap: Bitmap? = null
    private var originalBuffer: ByteBuffer? = null
    private val lib by lazy { Rustique() }

    fun loadImage(resources: Resources, @DrawableRes resId: Int) {
        if (state.previewState != PreviewState.Undefined) return
        viewModelScope.launch(Dispatchers.IO) {
            val opts = Options().apply {
                inMutable = true
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val bitmap = BitmapFactory.decodeResource(resources, resId, opts)?.also {
                this@ImageViewModel.bitmap = it
            } ?: return@launch
            val originalBuffer = ByteBuffer.allocateDirect(bitmap.byteCount).also {
                this@ImageViewModel.originalBuffer = it
            }
            bitmap.copyPixelsToBuffer(originalBuffer)
            originalBuffer.rewind()
            _state.update {
                it.copy(
                    image = bitmap.asImageBitmap(),
                    previewState = PreviewState.Original,
                    architecture = lib.getArchitecture()
                )
            }
        }
    }

    fun turnGrayscale() {
        if (state.previewState == PreviewState.Grayscale) return
        val originalBuffer = originalBuffer ?: return
        val bitmap = bitmap ?: return
        applyTransformation(originalBuffer, bitmap, PreviewState.Grayscale) { buffer, bitmap ->
            lib.grayscale(buffer, bitmap.height, bitmap.rowBytes)
        }
    }

    fun turnInverted() {
        if (state.previewState == PreviewState.Inverted) return
        val originalBuffer = originalBuffer ?: return
        val bitmap = bitmap ?: return
        applyTransformation(originalBuffer, bitmap, PreviewState.Inverted) { buffer, bitmap ->
            lib.invert(buffer, bitmap.height, bitmap.rowBytes)
        }
    }

    private fun applyTransformation(
        originalBuffer: ByteBuffer,
        bitmap: Bitmap,
        previewState: PreviewState,
        transformation: (ByteBuffer, Bitmap) -> Unit
    ) {
        val buffer = ByteBuffer.allocateDirect(originalBuffer.capacity())
        buffer.put(originalBuffer)
        originalBuffer.rewind()
        buffer.rewind()
        transformation(buffer, bitmap)
        buffer.rewind()
        bitmap.copyPixelsFromBuffer(buffer)
        _state.update {
            it.copy(
                image = bitmap.asImageBitmap(),
                previewState = previewState
            )
        }
    }

    fun restore() {
        val originalBuffer = originalBuffer ?: return
        val bitmap = bitmap ?: return
        bitmap.copyPixelsFromBuffer(originalBuffer)
        originalBuffer.rewind()
        _state.update {
            it.copy(
                image = bitmap.asImageBitmap(),
                previewState = PreviewState.Original
            )
        }
    }
}