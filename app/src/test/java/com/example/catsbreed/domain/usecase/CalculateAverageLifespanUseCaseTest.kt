package com.example.catsbreed.domain.usecase

import com.example.catsbreed.util.testBreed
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculateAverageLifespanUseCaseTest {

    private val useCase = CalculateAverageLifespanUseCase()

    @Test
    fun `returns 0 for an empty list`() {
        assertThat(useCase(emptyList())).isEqualTo(0.0)
    }

    @Test
    fun `averages the lower bound of each breed's life span`() {
        val breeds = listOf(
            testBreed(id = "a", lifeSpan = "10 - 14"),
            testBreed(id = "b", lifeSpan = "12 - 16"),
            testBreed(id = "c", lifeSpan = "8 - 12")
        )
        // lower bounds: 10, 12, 8 -> average 10.0
        assertThat(useCase(breeds)).isEqualTo(10.0)
    }

    @Test
    fun `rounds to one decimal place`() {
        val breeds = listOf(
            testBreed(id = "a", lifeSpan = "10 - 14"),
            testBreed(id = "b", lifeSpan = "11 - 14"),
            testBreed(id = "c", lifeSpan = "12 - 14")
        )
        // lower bounds: 10, 11, 12 -> average 11.0
        assertThat(useCase(breeds)).isEqualTo(11.0)
    }

    @Test
    fun `treats an unparsable life span as zero instead of crashing`() {
        val breeds = listOf(testBreed(id = "a", lifeSpan = "unknown"))
        assertThat(useCase(breeds)).isEqualTo(0.0)
    }
}