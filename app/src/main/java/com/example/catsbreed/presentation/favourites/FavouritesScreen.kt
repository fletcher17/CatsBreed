package com.example.catsbreed.presentation.favourites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catsbreed.presentation.breedlist.BreedListItemRow
import com.example.catsbreed.presentation.components.EmptyState
import com.example.catsbreed.presentation.components.FullScreenLoading
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    onBreedClick: (String) -> Unit,
    viewModel: FavouritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favourites") }) }
    ) { padding ->
        when {
            uiState.isLoading -> FullScreenLoading(modifier = Modifier.padding(padding))
            uiState.isEmpty -> EmptyState(
                message = "No favourites yet. Tap the heart on any breed to add it here.",
                modifier = Modifier.padding(padding)
            )
            else -> Column(modifier = Modifier.padding(padding)) {
                AverageLifespanSummary(
                    averageYears = uiState.averageLifespanYears,
                    count = uiState.favourites.size
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(uiState.favourites, key = { it.id }) { breed ->
                        BreedListItemRow(breed = breed, onClick = { onBreedClick(breed.id) })
                    }
                }
            }
        }
    }
}



@Composable
private fun AverageLifespanSummary(averageYears: Double, count: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Average lifespan across $count favourite${if (count == 1) "" else "s"}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "$averageYears years",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}