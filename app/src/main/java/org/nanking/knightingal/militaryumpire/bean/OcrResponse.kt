package org.nanking.knightingal.militaryumpire.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(val text: String, @SerialName("trust_rate")  val trustRate: Float)