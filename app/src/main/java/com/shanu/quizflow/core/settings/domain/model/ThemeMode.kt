package com.shanu.quizflow.core.settings.domain.model

/** User-selectable app theme preference. Pure domain enum — no Android/Compose dependency. */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    fun next(): ThemeMode = when (this) {
        LIGHT -> DARK
        DARK -> SYSTEM
        SYSTEM -> LIGHT
    }
}
