package com.typ.handtalk.core.a2s

import android.content.Context
import com.typ.handtalk.core.a2s.db.A2STranslationsHistoryDatabase

object A2STranslationsHistory {

    fun getAllRecords(ctx: Context): Array<A2STranslationHistoryRecord> {
        return A2STranslationsHistoryDatabase.getInstance(ctx).dao.getAllTranslations()
    }

    fun saveTranslation(ctx: Context, record: A2STranslationHistoryRecord) {
        A2STranslationsHistoryDatabase.getInstance(ctx).dao.saveTranslation(record)
    }

}