package com.example.app_17_todo_revised.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // 정렬 순서에 따라 다른 쿼리를 제공
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getTodosSortedByTime(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items ORDER BY task ASC")
    fun getTodosSortedByName(): Flow<List<TodoItem>>
    // --- 데이터 쓰기 (INSERT) ---
    // ✅ 새로운 아이템을 추가하는 함수
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TodoItem)

    // --- 데이터 수정 (UPDATE) ---
    // ✅ 기존 아이템을 수정하는 함수
    @Update
    suspend fun update(item: TodoItem)

    // --- 데이터 삭제 (DELETE) ---
    // ✅ 아이템을 삭제하는 함수
    @Delete
    suspend fun delete(item: TodoItem)
}
