package com.typ.handtalk.core.a2s.playables

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class A2SignPlayableVideo(filename: String) : A2SignPlayable(filename) {

    override val filePath: String
        get() = "A2S/Videos/${filename}"

    suspend fun copyToCache(context: Context) {
        // todo: Needs to be tested
        withContext(Dispatchers.IO) {
            runBlocking {
                // Cache video
                var ins: InputStream? = null
                var ots: FileOutputStream? = null
                try {
                    // todo: Don't forget to mkdirs for the target file (if needed)
                    val vidFile = asFile(context.cacheDir)
                    // Create in and out IO streams
                    ins = context.assets.open(filePath)
                    ots = FileOutputStream(vidFile)
                    Log.i("A2SignPlayableFile($filename)", "Available: ${ins.available()} bytes.")
                    // Copy video file from in to out
                    var read: Int
                    val buffer = ByteArray(1024)
                    while ((ins.read(buffer).also { read = it }) != -1) {
                        ots.write(buffer, 0, read)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        ins?.close()
                    } catch (_: IOException) {
                    }
                    try {
                        ots?.flush()
                        ots?.close()
                    } catch (_: IOException) {
                    }
                }
            }
        }
    }

}