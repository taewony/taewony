package com.example.app_17_todo_revised.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ✅ UUID를 Int로 변경하고, 기본값을 0으로 설정
    val task: String,
    // val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis() // 생성 시각
)