package com.example.catsbreed.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.catsbreed.presentation.components.FavouriteToggleButton
import com.example.catsbreed.presentation.components.FullScreenError
import com.example.catsbreed.presentation.components.FullScreenLoading
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    breedId: String,
    onBackClick: () -> Unit
) {

    val viewModel = koinViewModel<BreedDetailViewModel>(parameters = { parametersOf(breedId) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breed Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val state = uiState
                    if (state is BreedDetailUiState.Success) {
                        FavouriteToggleButton(
                            isFavourite = state.breed.isFavourite,
                            onToggle = viewModel::onToggleFavourite
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is BreedDetailUiState.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is BreedDetailUiState.Error -> FullScreenError(
                message = state.message,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding)
            )
            is BreedDetailUiState.Success -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = state.breed.imageUrl,
                    contentDescription = "Photo of ${state.breed.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = state.breed.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer()
                    DetailSection(
                        title = "Origin",
                        body = state.breed.origin.ifBlank { "Unknown" })
                    DetailSection(
                        title = "Temperament",
                        body = state.breed.temperament.ifBlank { "Unknown" })
                    DetailSection(
                        title = "Life span",
                        body = "${state.breed.lifeSpan} years"
                    )
                    DetailSection(
                        title = "Description",
                        body = state.breed.description.ifBlank { "No description available." })
                }
            }
        }
    }
}

@Composable
private fun Spacer() = androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

@Composable
private fun DetailSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = body, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
    }
}