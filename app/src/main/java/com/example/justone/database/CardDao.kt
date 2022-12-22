package com.example.justone.database

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardDao {

    @Query("SELECT * FROM card_table")
    fun getAll(): List<Card>

    @Query("SELECT COUNT(id) FROM card_table")
    fun getTotalCount(): Int

    @Query("SELECT * FROM card_table WHERE difficulty = :difficulty")
    fun getAllByDifficulty(difficulty: Int): List<Card>


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    @Query("SELECT * FROM card_table WHERE tags LIKE :tags")
    fun getAllByTags(tags: String): List<Card>

    @Query("SELECT COUNT(id) FROM card_table WHERE tags LIKE :tags")
    fun getTagCount(tags: String): Int



    @Query("SELECT * FROM card_table WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Card

    @Query("SELECT * FROM card_table WHERE id IN (:id)")
    fun findByIds(id: List<Int>): List<Card>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(objects: List<Card>)

    @Query("DELETE FROM card_table")
    suspend fun  deleteAll()

    //get last inserted id
    @Query("SELECT seq FROM sqlite_sequence WHERE name = :tableName")
    abstract fun getSequenceNumber(tableName: String): Long?
}