package com.kshitijpatil.rustique

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
}