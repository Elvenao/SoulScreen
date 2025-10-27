package com.example.mongodb.utils

import android.content.Context
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import java.io.File

object Compressor {
    suspend fun compressImage(context: Context, imageFile: File): File {
        return Compressor.compress(context, imageFile) {
            resolution(100,100)
            quality(70)

        }
    }
}