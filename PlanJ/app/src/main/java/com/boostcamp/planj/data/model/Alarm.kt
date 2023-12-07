package com.boostcamp.planj.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alarm(
    val alarmType: String,
    val alarmTime: Int,
    val firstScheduleUuid: String,
) : Parcelable
