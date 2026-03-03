package com.typ.handtalk.impl.launcher

import android.content.Context
import android.content.Intent
import com.typ.handtalk.impl.HandSignRecognizer
import com.typ.handtalk.impl.ui.HandTalkRecognizerActivity

actual fun launchHandTalkRecognizer(recognizer: HandSignRecognizer) {
    val intent = Intent(recognizer.context, HandTalkRecognizerActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    recognizer.context.startActivity(intent)
}
