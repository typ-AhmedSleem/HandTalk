package com.typ.handtalk.core.a2s.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class A2STranslationsHistoryDatabase : RoomDatabase() {

    abstract val dao: A2STranslationHistoryRecordDAO

    companion object {

        @Volatile
        private var INSTANCE: A2STranslationsHistoryDatabase? = null // Internal instance.

        fun getInstance(ctx: Context): A2STranslationsHistoryDatabase {
            if (INSTANCE == null) {
                synchronized(A2STranslationsHistoryDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            ctx,
                            A2STranslationsHistoryDatabase::class.java,
                            "A2STranslationsHistoryDatabase"
                        ).allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

    }

}