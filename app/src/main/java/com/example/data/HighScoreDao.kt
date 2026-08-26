package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT 10")
    fun getTopHighScores(): Flow<List<HighScoreEntity>>

    @Query("SELECT MAX(score) FROM high_scores")
    fun getMaxScore(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: HighScoreEntity)

    @Query("DELETE FROM high_scores")
    suspend fun clearHighScores()
}
