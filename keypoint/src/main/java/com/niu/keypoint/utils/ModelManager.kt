/*
*@file ModelManager.java
*
* Copyright (C) 2019. Huawei Technologies Co., Ltd. All rights reserved.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*/
package com.niu.keypoint.utils

import android.util.Log
import com.niu.keypoint.bean.ModelInfo
import java.util.*

object ModelManager {
    private val TAG = ModelManager::class.java.simpleName
    fun loadJNISo(): Boolean {
        return try {
            System.loadLibrary("hiaijni")
            true
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "failed to load native library: " + e.message)
            false
        }
    }

    external fun runModelSync(
        modelInfo: ModelInfo?,
        buf: ArrayList<ByteArray?>?
    ): ArrayList<FloatArray?>?

    external fun GetTimeUseSync(): Long
    external fun runModelAsync(
        modelInfo: ModelInfo?,
        buf: ArrayList<ByteArray?>?,
        listener: ModelManagerListener?
    )

    external fun loadModelAsync(modelInfo: ArrayList<ModelInfo?>?): ArrayList<ModelInfo?>?
    external fun loadModelSync(modelInfo: ArrayList<ModelInfo?>?): ArrayList<ModelInfo?>?

    /**
     *
     * @param offlinemodelpath   /xxx/xxx/xxx/xx.om
     * @return ture : it can run on NPU
     * false: it should run on CPU
     */
    external fun modelCompatibilityProcessFromFile(offlinemodelpath: String?): Boolean //public static native boolean modelCompatibilityProcessFromBuffer(byte[] onlinemodelbuffer,byte[] modelparabuffer,String framework,String offlinemodelpath);
}