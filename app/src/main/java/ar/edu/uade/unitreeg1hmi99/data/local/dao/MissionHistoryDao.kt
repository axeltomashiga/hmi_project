package ar.edu.uade.unitreeg1hmi99.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.unitreeg1hmi99.data.local.entity.MissionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionHistoryDao {
    @Query("SELECT * FROM mission_history ORDER BY startTime DESC")
    fun getAllMissions(): Flow<List<MissionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(mission: MissionHistoryEntity): Long

    @Query("DELETE FROM mission_history")
    fun clearHistory(): Int
}
