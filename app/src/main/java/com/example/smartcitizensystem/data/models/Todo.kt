package com.example.smartcitizensystem.data.models

data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)