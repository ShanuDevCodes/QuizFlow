package com.shanu.quizflow.core.settings.domain.model

/** User-selectable app theme preference. Pure domain enum — no Android/Compose dependency. */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    /** Cycling order used by the single-tap theme toggle: Light -> Dark -> System -> Light. */
    fun next(): ThemeMode = when (this) {
        LIGHT -> DARK
        DARK -> SYSTEM
        SYSTEM -> LIGHT
    }
}
