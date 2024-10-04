package com.example.codelab1

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Topic(
    @StringRes val name: Int,
    val availableCourses: Int,
    @DrawableRes val imageRes: Int
)