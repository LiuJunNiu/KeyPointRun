/*
*@file ModelManagerListener.java
*
* Copyright (C) 2019. Huawei Technologies Co., Ltd. All rights reserved.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*/
package com.niu.keypoint.utils

import java.util.*

interface ModelManagerListener {
    fun OnProcessDone(taskId: Int, output: ArrayList<FloatArray?>?, inferencetime: Float)
    fun onServiceDied()
}