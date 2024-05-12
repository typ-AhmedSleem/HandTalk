package com.typ.handtalk.core.a2s.playables

import android.content.Context
import android.util.Log
import com.typ.handtalk.ui.a2s.Arabic2SignTranslatorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class A2SignPlayableVideo(
    filename: String, delay: Long = Arabic2SignTranslatorActivity.PLAYABLE_SWITCH_DEFAULT_DELAY_TIME
) : A2SignPlayable(filename, delay) {

    override val filePath: String
        get() = "A2S/Videos/${filename}"

    fun getVideoPath(cacheDir: File): String = asFile(cacheDir).path

    private val TAG: String
        get() = "A2SPV($filePath')"

    suspend fun cacheVideoOnIO(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            // * Cache video
            cacheVideo(context)
        }
    }

    fun cacheVideo(context: Context): Boolean {
        return try {
            // Ensure that videos folder path exists
            getVideosPath(context.cacheDir).apply {
                Log.i(TAG, "copyToCache: mkdirs for videos folder '$path' returns a ${mkdirs()}")
            }
            // Prepare the video file
            val vidFile = asFile(context.cacheDir).apply {
                if (exists()) {
                    Log.i(TAG, "copyToCache: isDir=$isDirectory, isFile=$isFile")
                    if (isFile) return true
                    // Delete the invalid file
                    delete()
                    Log.i(TAG, "copyToCache: Deleted the invalid video file.")
                }
            }
            Log.i(TAG, "copyToCache: Preparing to cache video ('$filePath' --> '${vidFile.path}')...")
            // Create in and out IO streams
            context.assets.open(filePath).use { ins ->
                Log.i(TAG, "copyToCache: Video file is ${ins.available()} bytes.")
                vidFile.outputStream().use { ots ->
                    // Copy video file from ins to outs
                    var read: Int
                    val buffer = ByteArray(1024)
                    while ((ins.read(buffer).also { read = it }) != -1) {
                        ots.write(buffer, 0, read)
                    }
                    Log.i(TAG, "copyToCache: Finished caching file.")
                }
            }
            Log.i(TAG, "copyToCache: Ensuring output isFile=${vidFile.isFile}")
            // Cached successfully
            true
        } catch (e: IOException) {
            Log.i(TAG, "copyToCache: Error caching video. Reason: $e")
            e.printStackTrace()
            // Failed to cache
            false
        }
    }

    override fun toString(): String {
        return "A2SignPlayableVideo($filePath')"
    }


}