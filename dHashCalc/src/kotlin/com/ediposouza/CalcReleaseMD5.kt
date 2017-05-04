package com.ediposouza

import com.ediposouza.extensions.getMD5
import java.io.File

/**
 * Created by ediposouza on 06/03/17.
 */
object CalcReleaseMD5 {

    @JvmStatic fun main(args: Array<String>) {
        val currentPath = File(TESLTracker.jarPath).parentFile.parentFile.parentFile.path
        println("$currentPath/artifacts/WabbaTrackFull.exe")
        val artifactFile = File(currentPath, "out/artifacts/WabbaTrack/WabbaTrack.jar")
        println(artifactFile.getMD5())
    }

}