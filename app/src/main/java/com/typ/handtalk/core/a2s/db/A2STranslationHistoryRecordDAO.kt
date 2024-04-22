package com.typ.handtalk.core.a2s.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord

@Dao
interface A2STranslationHistoryRecordDAO {

    @Insert(entity = A2STranslationHistoryRecord::class)
    fun saveTranslation(record: A2STranslationHistoryRecord)

    @Query("SELECT DISTINCT * FROM a2s_history ORDER BY timestamp DESC")
    fun getAllTranslations(): Array<A2STranslationHistoryRecord>

    @Query("DELETE FROM a2s_history")
    fun clearHistory()

}