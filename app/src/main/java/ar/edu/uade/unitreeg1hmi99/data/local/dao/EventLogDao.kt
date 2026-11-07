package ar.edu.uade.unitreeg1hmi99.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.unitreeg1hmi99.data.local.entity.EventLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventLogDao {
    @Query("SELECT * FROM event_logs ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<EventLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: EventLogEntity): Long

    @Query("DELETE FROM event_logs")
    fun clearEvents(): Int
}
