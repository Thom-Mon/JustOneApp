package com.example.justone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardDao {

    @Query("SELECT * FROM card_table")
    fun getAll(): List<Card>

    @Query("SELECT * FROM card_table WHERE difficulty = :difficulty")
    suspend fun getAllByDifficulty(difficulty: Int): List<Card>


    @Query("SELECT * FROM card_table WHERE tags LIKE :tags")
    suspend fun getAllByTags(tags: String): List<Card>

    @Query("SELECT * FROM card_table WHERE id LIKE :id LIMIT 1")
    suspend fun findById(id: Int): Card


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objects: List<Card>)

    //get last inserted id
    @Query("SELECT seq FROM sqlite_sequence WHERE name = :tableName")
    abstract fun getSequenceNumber(tableName: String): Long?
}