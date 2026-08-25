package com.example.catsbreed

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class BreedListScreenE2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun breedListScreen_displaysTitleAndSearchBar() {
        composeRule.onNodeWithText("Cat Breeds").assertExists()
        composeRule.onNodeWithText("Search breeds...").performTextInput("Ab")
    }

    @Test
    fun bottomNavigation_navigatesToFavourites() {
        composeRule.onNodeWithText("Favourites").performClick()
        composeRule.onNodeWithText("No favourites yet. Tap the heart on any breed to add it here.").assertExists()
    }
}