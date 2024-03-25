package com.typ.handtalk.core.a2s.playables

import android.content.res.AssetManager
import java.io.File
import java.io.IOException

/**
 * Base class that is inherited making two variants: Image, Video
 * that holds info like filename and path for the target file in
 * assets folder and cache dir with also helper method for caching.
 *
 * @param filename Filename of the target file. NOTE: filename includes
 * the extension of the target file (e.g: filename.ext) not just its name.
 */
abstract class A2SignPlayable(val filename: String) {

    //  ? Maybe I better use raw res folder to store
    //  ? videos and images for A2S. -\_(^_^)/_-
    //  ? DOUBLE CHECK THIS SOLUTION LATER

    /**
     * Path to the file and folder containing it as below:
     * for static images => A2S/Images/FILE.jpeg
     * for videos => A2S/Videos/FILE.mp4
     *
     */
    abstract val filePath: String

    fun asFile(cacheDir: File) = File(cacheDir, filePath)

    fun existsInCache(cacheDir: File) = asFile(cacheDir).exists()

    fun existsInAssets(assets: AssetManager): Boolean {
        // todo: Double check behavior of this method at runtime
        return try {
            assets.open(filePath).close()
            true
        } catch (_: IOException) {
            false
        }
    }

}
