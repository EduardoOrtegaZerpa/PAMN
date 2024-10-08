package com.example.codelab1

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Subject(
    @StringRes val name: Int,
    val availableCourses: Int,
    @DrawableRes val imageRes: Int
)