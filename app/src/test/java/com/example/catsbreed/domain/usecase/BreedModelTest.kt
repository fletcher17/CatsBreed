package com.example.catsbreed.domain.usecase

import com.example.catsbreed.util.testBreed
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BreedModelTest {
    @Test
    fun `lifeSpanLowerYears parses the lower bound of a range`() {
        assertThat(testBreed(lifeSpan = "9 - 15").lifeSpanLowerYears).isEqualTo(9)
    }

    @Test
    fun `lifeSpanLowerYears trims whitespace`() {
        assertThat(testBreed(lifeSpan = " 12  -  16 ").lifeSpanLowerYears).isEqualTo(12)
    }

    @Test
    fun `lifeSpanLowerYears falls back to 0 for malformed input`() {
        assertThat(testBreed(lifeSpan = "").lifeSpanLowerYears).isEqualTo(0)
        assertThat(testBreed(lifeSpan = "n/a").lifeSpanLowerYears).isEqualTo(0)
    }
}