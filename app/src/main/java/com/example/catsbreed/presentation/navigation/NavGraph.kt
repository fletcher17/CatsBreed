package com.example.catsbreed.presentation.navigation

import android.R.attr.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catsbreed.presentation.breedlist.BreedListScreen
import com.example.catsbreed.presentation.detail.BreedDetailScreen
import com.example.catsbreed.presentation.favourites.FavouritesScreen

private data class BottomTab(val route: String, val label: String, val icon: ImageVector)

private val bottomTabs = listOf(
    BottomTab(ScreenRoutes.BREED_LIST, "Breeds", Icons.Filled.Pets),
    BottomTab(ScreenRoutes.FAVOURITES, "Favourites", Icons.Filled.Favorite)
)

@Composable
fun CatsBreedNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            val showBottomBar = bottomTabs.any { it.route == currentDestination?.route }
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoutes.BREED_LIST,
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            composable(ScreenRoutes.BREED_LIST) {
                BreedListScreen(onBreedClick = { id ->
                    navController.navigate(
                        ScreenRoutes.breedDetail(
                            id
                        )
                    )
                })
            }
            composable(
                route = ScreenRoutes.BREED_DETAIL,
                arguments = listOf(navArgument("breedId") { })
            ) { backStackEntry ->
                val breedId = backStackEntry.arguments?.getString("breedId").orEmpty()
                BreedDetailScreen(breedId = breedId, onBackClick = { navController.popBackStack() })
            }
            composable(ScreenRoutes.FAVOURITES) {
                FavouritesScreen(onBreedClick = { id ->
                    navController.navigate(
                        ScreenRoutes.breedDetail(
                            id
                        )
                    )
                })
            }

        }
    }
}