/*
*@file Untils.java
*
* Copyright (C) 2019. Huawei Technologies Co., Ltd. All rights reserved.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*/
package com.niu.keypoint.utils

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.niu.keypoint.utils.Constant.meanValueOfBlue
import com.niu.keypoint.utils.Constant.meanValueOfGreen
import com.niu.keypoint.utils.Constant.meanValueOfRed
import java.io.*

object Untils {
    private val TAG = Untils::class.java.simpleName
    private var bis: BufferedInputStream? = null
    private var fileInput: InputStream? = null
    private var fileOutput: FileOutputStream? = null
    private var byteOut: ByteArrayOutputStream? = null
    fun getModelBufferFromModelFile(modelPath: String?): ByteArray {
        return try {
            bis = BufferedInputStream(FileInputStream(modelPath))
            byteOut = ByteArrayOutputStream(1024)
            val buffer = ByteArray(1024)
            var size = 0
            while (bis!!.read(buffer, 0, 1024).also { size = it } != -1) {
                byteOut!!.write(buffer, 0, size)
            }
            byteOut!!.toByteArray()
        } catch (e: Exception) {
            ByteArray(0)
        } finally {
            releaseResource(byteOut)
            releaseResource(bis)
        }
    }

    private fun releaseResource(resource: Closeable?) {
        var resource = resource
        if (resource != null) {
            try {
                resource.close()
                resource = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getPixels(
        framework: String?, bitmap: Bitmap,
        resizedWidth: Int, resizedHeight: Int
    ): ByteArray {
        val channel = 3
        val buff = FloatArray(channel * resizedWidth * resizedHeight)
        var rIndex: Int
        var gIndex: Int
        var bIndex: Int
        for (i in 0 until resizedHeight) {
            for (j in 0 until resizedWidth) {
                bIndex = i * resizedWidth + j
                gIndex = bIndex + resizedWidth * resizedHeight
                rIndex = gIndex + resizedWidth * resizedHeight
                val color = bitmap.getPixel(j, i)
                buff[bIndex] = (Color.blue(color) - meanValueOfBlue) as Float
                buff[gIndex] = (Color.green(color) - meanValueOfGreen) as Float
                buff[rIndex] = (Color.red(color) - meanValueOfRed) as Float
            }
        }
        val pixCount = channel * resizedWidth * resizedHeight
        val ret = ByteArray(pixCount * 4)
        for (i in 0 until pixCount) {
            val int_bits = java.lang.Float.floatToIntBits(buff[i])
            ret[i * 4 + 0] = int_bits.toByte()
            ret[i * 4 + 1] = (int_bits ushr 8).toByte()
            ret[i * 4 + 2] = (int_bits ushr 16).toByte()
            ret[i * 4 + 3] = (int_bits ushr 24).toByte()
        }
        return ret
    }

    fun getPixelsAIPP(
        framework: String?,
        bitmap: Bitmap,
        resizedWidth: Int,
        resizedHeight: Int
    ): ByteArray {
        Log.i(TAG, "resizedWidth : $resizedWidth resizedHeight : $resizedHeight")
        return getNV12(resizedWidth, resizedHeight, bitmap)
    }

    private fun getNV12(inputWidth: Int, inputHeight: Int, scaled: Bitmap): ByteArray {
        // Reference (Variation) : https://gist.github.com/wobbals/5725412
        val argb = IntArray(inputWidth * inputHeight)
        Log.i(TAG, "scaled : $scaled")
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
        val yuv = ByteArray(inputWidth * inputHeight * 3 / 2)
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight)

        //scaled.recycle();
        return yuv
    }

    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var a: Int
        var R: Int
        var G: Int
        var B: Int
        var Y: Int
        var U: Int
        var V: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                a = argb[index] and -0x1000000 shr 24 // a is not used obviously
                R = argb[index] and 0xff0000 shr 16
                G = argb[index] and 0xff00 shr 8
                B = argb[index] and 0xff shr 0

                // well known RGB to YUV algorithm
                Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                    yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                }
                index++
            }
        }
    }

    fun copyModelsFromAssetToAppModels(
        am: AssetManager,
        sourceModelName: String,
        destDir: String
    ): Boolean {
        return try {
            fileInput = am.open(sourceModelName)
            val filename = destDir + sourceModelName
            fileOutput = FileOutputStream(filename)
            byteOut = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = -1
            while (fileInput!!.read(buffer).also { len = it } != -1) {
                byteOut!!.write(buffer, 0, len)
            }
            fileOutput!!.write(byteOut!!.toByteArray())
            true
        } catch (ex: Exception) {
            Log.e(TAG, "copyModelsFromAssetToAppModels : $ex")
            false
        } finally {
            releaseResource(byteOut)
            releaseResource(fileOutput)
            releaseResource(fileInput)
        }
    }

    fun isExistModelsInAppModels(modelname: String, savedir: String?): Boolean {
        val dir = File(savedir)
        val currentfiles = dir.listFiles()
        if (currentfiles == null) {
            return false
        } else {
            for (file in currentfiles) {
                if (file.name == modelname) {
                    return true
                }
            }
        }
        return false
    }
}