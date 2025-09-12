package com.example.noteapp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.presentation.navigation.Screens
import com.example.noteapp.presentation.screens.home.HomeScreen
import com.example.noteapp.presentation.screens.note_details.NoteDetailsScreen
import com.example.noteapp.presentation.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), NavController.OnDestinationChangedListener {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            navController.addOnDestinationChangedListener(this)

            NoteAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteApp(viewModel = viewModel, navController)
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }
}

@Composable
fun NoteApp(viewModel: MainViewModel, navController: NavHostController) {

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.White)
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screens.HomeScreen.route) { HomeScreen(navController) }
            composable(
                route = "${Screens.NoteDetailsScreen.route}?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) {
                NoteDetailsScreen(navController)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteAppTheme {
        NoteApp(viewModel = MainViewModel(), navController = rememberNavController())
    }
}