package com.example.flagchallenge.dataclass

data class Question(
    val answerId: Int,
    val questionText: String,
    val image: Int, // Resource ID for the image
    val alternatives: List<String>,
    val correctAnswerIndex: Int,
)
