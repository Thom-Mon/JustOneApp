package com.example.justone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_table")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int?, //maybe no autogenerate later on
    @ColumnInfo(name = "word") val word: String?,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "difficulty") val difficulty: Int?
)