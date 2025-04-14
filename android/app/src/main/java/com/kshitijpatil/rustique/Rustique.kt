package com.kshitijpatil.rustique

import java.nio.ByteBuffer

class Rustique {
    companion object {
        init {
            System.loadLibrary("rustique")
        }
    }

    @JvmName("getArchitecture")
    external fun getArchitecture(): String

    @JvmName("grayscale")
    external fun grayscale(buffer: ByteBuffer, height: Int, stride: Int)

    @JvmName("invert")
    external fun invert(buffer: ByteBuffer, height: Int, stride: Int)
}