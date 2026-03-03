package com.typ.handtalk.impl.di

import com.typ.handtalk.impl.HandSignRecognizer
import com.typ.handtalk.impl.HandTalk
import org.koin.dsl.module

/**
 * Koin module that provides [HandSignRecognizer] and [HandTalk] as singletons.
 *
 * On Android, [HandSignRecognizer] receives `android.content.Context` via Koin's
 * `androidContext()`, so ensure your app calls `androidContext(this)` during Koin init.
 *
 * Example (Android Application class):
 * ```kotlin
 * startKoin {
 *     androidContext(this@MyApplication)
 *     modules(handTalkModule)
 * }
 * ```
 */
val handTalkModule = module {
    single { HandSignRecognizer(context = get()) }
    single { HandTalk(recognizer = get()) }
}
