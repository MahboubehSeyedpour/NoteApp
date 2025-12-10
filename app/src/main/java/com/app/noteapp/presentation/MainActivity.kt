package com.app.noteapp.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.noteapp.presentation.navigation.Screens
import com.app.noteapp.presentation.screens.add_note.NoteDetailScreen
import com.app.noteapp.presentation.screens.home.HomeScreen
import com.app.noteapp.presentation.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val noteIdFromNotif = intent.getLongExtra("noteId", 0L)

        setContent {
            val navController = rememberNavController()
            // Handle listener inside composition if you need it
            DisposableEffect(navController) {
                navController.addOnDestinationChangedListener(this@MainActivity)
                onDispose {
                    navController.removeOnDestinationChangedListener(this@MainActivity)
                }
            }

            val currentFont by viewModel.currentFont.collectAsState()

            NoteAppTheme(
                darkTheme = isSystemInDarkTheme(), dynamicColor = false, appFont = currentFont
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteApp(
                        viewModel = viewModel,
                        navController = navController,
                        initialNoteId = noteIdFromNotif
                    )
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
    }
}

@Composable
fun NoteApp(viewModel: MainViewModel, navController: NavHostController, initialNoteId: Long = 0L) {

    val currentFont by viewModel.currentFont.collectAsStateWithLifecycle()

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.White) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screens.HomeScreen.route) {
                HomeScreen(
                    navController,
                    currentFont = currentFont,
                    onFontSelected = viewModel::onFontSelected
                )
            }
            composable(
                route = "${Screens.NoteDetailScreen.route}?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) {
                NoteDetailScreen(navController)
            }
        }
    }

    LaunchedEffect(initialNoteId) {
        if (initialNoteId != 0L) {
            navController.navigate("${Screens.NoteDetailScreen.route}?id=$initialNoteId") {
                launchSingleTop = true
                restoreState = true
                popUpTo(Screens.HomeScreen.route) { saveState = true }
            }
        }
    }
}

//@SuppressLint("ViewModelConstructorInComposable")
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    NoteAppTheme(appFont = AppFont.PELAK) {
//        NoteApp(viewModel = MainViewModel(), navController = rememberNavController())
//    }
//}