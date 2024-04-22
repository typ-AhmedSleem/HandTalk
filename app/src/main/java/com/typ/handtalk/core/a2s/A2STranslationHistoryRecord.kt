package com.typ.handtalk.core.a2s

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.typ.handtalk.core.a2s.playables.A2SignPlayable

@Entity(tableName = "a2s_history")
class A2STranslationHistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val sentence: String,
    @Ignore val signs: Array<A2SignPlayable> = emptyArray(),
    @ColumnInfo val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is A2STranslationHistoryRecord) return false

        if (id != other.id) return false
        if (sentence.lowercase().trim() != other.sentence.lowercase().trim()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sentence.hashCode()
        result = 31 * result + signs.contentHashCode()
        return result
    }
}
