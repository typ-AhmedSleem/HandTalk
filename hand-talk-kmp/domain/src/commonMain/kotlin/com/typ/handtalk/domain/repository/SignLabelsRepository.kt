package com.typ.handtalk.domain.repository

import com.typ.handtalk.domain.models.HandSign

object SignLabelsRepository {
    // Single source of truth for supported signs
    val supportedSigns = setOf(
        "none",
        // Add other signs here as needed
    )

    fun isSupported(label: String): Boolean {
        return supportedSigns.contains(label.lowercase())
    }
}
