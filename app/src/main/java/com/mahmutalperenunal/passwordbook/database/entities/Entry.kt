package com.mahmutalperenunal.passwordbook.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "entry")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val category: String,
    val icon: Int,
    var favourite: Int
) : Serializable