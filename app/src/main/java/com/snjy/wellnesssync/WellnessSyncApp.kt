package com.snjy.wellnesssync

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.snjy.wellnesssync.presentation.navigation.WellnessSyncNavGraph
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme
import com.snjy.wellnesssync.presentation.viewmodel.ThemeViewModel

@Composable
fun WellnessSyncApp() {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

    WellnessSyncTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            Scaffold { paddingValues ->
                WellnessSyncNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}