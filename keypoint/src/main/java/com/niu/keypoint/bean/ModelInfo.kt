/*
*@file ModelInfo.java
*
* Copyright (C) 2019. Huawei Technologies Co., Ltd. All rights reserved.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*/
package com.niu.keypoint.bean

import java.io.Serializable

class ModelInfo : Serializable {
    var modelSaveDir = ""
    var onlineModelLabel = ""
    var offlineModelName = ""
    var useAIPP = false
    var input_N = 0
    var input_C = 0
    var input_H = 0
    var input_W = 0
    var input_Number = 0
    var output_N = 0
    var output_C = 0
    var output_H = 0
    var output_W = 0
    var output_Number = 0
    //    /**
    //     * caffe : xxx.prototxt
    //     * tensorflow : xxx.pb
    //     * default is "" if don't have online model
    //     */
    //    private String onlineModel = "";
    //    /**
    //     * caffe: xxx.caffemodel
    //     * tensorflow: ""
    //     * default is "" if don't have online model
    //     */
    //    private String onlineModelPara = "";
    /**
     * xxx.om
     * default is "" if don't have offline model
     */
    var offlineModel = ""

    /**
     * "" or "100.100.001.010" or "100.150.010.010" ...
     * default is "100.100.001.010" if don't know offline model version
     */
    var offlineModelVersion = "100.100.001.010"

    /**
     * caffe or tensorflow
     */
    var framework = ""
    val modelPath: String
        get() = modelSaveDir + offlineModel
}