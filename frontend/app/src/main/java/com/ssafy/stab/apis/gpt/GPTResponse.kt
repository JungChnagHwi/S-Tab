package com.ssafy.stab.apis.gpt

import com.google.gson.annotations.SerializedName

data class GPTResponse(
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String
)

data class GPTRequest(
    @SerializedName("question") val question: String
)