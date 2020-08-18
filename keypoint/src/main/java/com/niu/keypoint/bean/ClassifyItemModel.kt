/*
*@file ClassifyItemModel.java
*
* Copyright (C) 2019. Huawei Technologies Co., Ltd. All rights reserved.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*/
package com.niu.keypoint.bean

import android.graphics.Bitmap

class ClassifyItemModel(
    val top1Result: String,
    val otherResults: String,
    val classifyTime: String,
    val classifyImg: Bitmap
)