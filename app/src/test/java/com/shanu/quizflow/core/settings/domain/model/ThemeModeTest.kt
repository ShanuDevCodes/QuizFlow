package com.shanu.quizflow.core.settings.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ThemeModeTest {

    @Test
    fun `next cycles Light to Dark`() {
        assertThat(ThemeMode.LIGHT.next()).isEqualTo(ThemeMode.DARK)
    }

    @Test
    fun `next cycles Dark to System`() {
        assertThat(ThemeMode.DARK.next()).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `next cycles System to Light`() {
        assertThat(ThemeMode.SYSTEM.next()).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `next is a closed three-cycle back to the starting mode`() {
        val start = ThemeMode.LIGHT
        val afterThreeCycles = start.next().next().next()
        assertThat(afterThreeCycles).isEqualTo(start)
    }
}
