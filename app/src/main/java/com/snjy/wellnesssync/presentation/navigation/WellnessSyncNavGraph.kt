package com.snjy.wellnesssync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.snjy.wellnesssync.presentation.screens.activities.ActivitiesScreen
import com.snjy.wellnesssync.presentation.screens.add_workout.AddWorkoutScreen
import com.snjy.wellnesssync.presentation.screens.auth.login.LoginScreen
import com.snjy.wellnesssync.presentation.screens.auth.register.RegisterScreen
import com.snjy.wellnesssync.presentation.screens.chat.ChatScreen
import com.snjy.wellnesssync.presentation.screens.devices.DevicesScreen
import com.snjy.wellnesssync.presentation.screens.home.HomeScreen
import com.snjy.wellnesssync.presentation.screens.profile.ProfileScreen
import com.snjy.wellnesssync.presentation.screens.splash.SplashScreen
import com.snjy.wellnesssync.presentation.screens.splash.SplashViewModel
import com.snjy.wellnesssync.presentation.screens.workout.detail.WorkoutDetailScreen
import com.snjy.wellnesssync.presentation.screens.workout.list.WorkoutListScreen

@Composable
fun WellnessSyncNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH,
    modifier: Modifier = Modifier  // Add this parameter
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier  // Pass the modifier to NavHost
    ) {
        composable(Routes.SPLASH) {
            val viewModel = hiltViewModel<SplashViewModel>()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()

            SplashScreen(
                onSplashComplete = {
                    if (isLoggedIn) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToActivities = { navController.navigate(Routes.ACTIVITIES) },
                onNavigateToAddWorkout = { navController.navigate(Routes.ADD_WORKOUT) },
                onNavigateToWorkouts = { navController.navigate(Routes.WORKOUTS) },
                onNavigateToDevices = { navController.navigate(Routes.DEVICES) },
                onNavigateToChat = { navController.navigate(Routes.CHAT) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.ACTIVITIES) {
            ActivitiesScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.ADD_WORKOUT) {
            AddWorkoutScreen(
                onNavigateBack = { navController.navigateUp() },
                onWorkoutAdded = { navController.navigate(Routes.HOME) }
            )
        }

        composable(Routes.WORKOUTS) {
            WorkoutListScreen(
                onNavigateBack = { navController.navigateUp() },
                onWorkoutClick = { workoutId ->
                    navController.navigate("${Routes.WORKOUT_DETAIL}/$workoutId")
                }
            )
        }

        composable(
            route = "${Routes.WORKOUT_DETAIL}/{workoutId}",
            arguments = listOf(
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            WorkoutDetailScreen(
                workoutId = workoutId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.CHAT) {
            ChatScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DEVICES) {
            DevicesScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}