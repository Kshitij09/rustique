package com.kshitijpatil.rustique

import java.nio.ByteBuffer

class Rustique {
    companion object {
        init {
            System.loadLibrary("rustique")
        }
    }

    @JvmName("add")
    external fun add(left: UInt, right: UInt): UInt
    @JvmName("getArchitecture")
    external fun getArchitecture(): String
    @JvmName("grayscale")
    external fun grayscale(buffer: ByteBuffer, width: Int, height: Int, stride: Int)
}