package com.nvrskdev.myschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nvrskdev.myschool.datastore.AppDataStore
import com.nvrskdev.myschool.ui.journal.JournalScreen
import com.nvrskdev.myschool.ui.news.NewsScreen
import com.nvrskdev.myschool.ui.progress.ProgressScreen
import com.nvrskdev.myschool.ui.schedule.ScheduleScreen
import com.nvrskdev.myschool.ui.signin.SignInScreen
import com.nvrskdev.myschool.ui.teacher.TeacherDetailsScreen
import com.nvrskdev.myschool.ui.teacher.TeachersScreen
import com.nvrskdev.myschool.ui.test.NewTestScreen
import com.nvrskdev.myschool.ui.test.TestPassingScreen
import com.nvrskdev.myschool.ui.test.TestsScreen
import com.nvrskdev.myschool.ui.theme.AppTheme
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val _hasEntered: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private lateinit var dataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !_isLoaded.value }

        dataStore = AppDataStore(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStore.userRole.collect {
                    _hasEntered.value = it >= 0

                    _isLoaded.value = true
                }
            }
        }

        setContent {

            AppTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //MyAppNavHost()

                    val hasEntered = _hasEntered.collectAsState()
                    if (hasEntered.value) {
                        MyAppNavHost()
                    } else {
                        SignInScreen(hasEntered = _hasEntered)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        MyAppNavHost()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()

    val items = listOf(
        Screen.News,
        Screen.Schedule,
        //Screen.Progress,
        Screen.Teachers,
        Screen.Tests
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route?.split('?')?.get(0)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = Screen.values().firstOrNull { it.route == currentRoute }?.labelId
                                ?: R.string.app_name
                        )
                    )
                },
                navigationIcon = {
                    if (!items.any { it.route == currentRoute }) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (items.any { it.route == currentRoute }) {
                NavigationBar() {

                    items.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    //popUpTo(0)
                                }
                            },
                            label = { Text(text = stringResource(id = screen.labelId)) },
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.iconId),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.News.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.News.route) { NewsScreen() }
            composable(Screen.Schedule.route) { ScheduleScreen() }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Teachers.route) {
                TeachersScreen(onNavigateToTeacherDetails = { teacherData ->
                    navController.navigate("${Screen.TeacherDetails.route}?teacherData=$teacherData")
                })
            }
            composable(Screen.Tests.route) {
                TestsScreen(
                    onNavigateToNewTest = { navController.navigate(Screen.NewTest.route) },
                    onNavigateToJournal = { journalData ->
                        navController.navigate("${Screen.Journal.route}?journalData=$journalData")
                    },
                    onNavigateToTestPassing = { testData ->
                        navController.navigate("${Screen.TestPassing.route}?testData=$testData")
                    }
                )
            }
            composable(Screen.NewTest.route) {
                NewTestScreen(onNavigateToTest = {
                    navController.navigate(Screen.Tests.route) {

                        popUpTo(Screen.Tests.route) {
                            inclusive = true
                        }
                    }
                })
            }
            composable(
                "${Screen.TeacherDetails.route}?teacherData={teacherData}",
                arguments = listOf(
                    navArgument("teacherData") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val teacherData = navBackStackEntry.arguments?.getString("teacherData")
                teacherData?.let {
                    TeacherDetailsScreen(teacherData = it)
                }
            }
            composable(
                "${Screen.Journal.route}?journalData={journalData}",
                arguments = listOf(
                    navArgument("journalData") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val journalData = navBackStackEntry.arguments?.getString("journalData")
                journalData?.let {
                    JournalScreen(journalData = it)
                }
            }
            composable(
                "${Screen.TestPassing.route}?testData={testData}",
                arguments = listOf(
                    navArgument("testData") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val testData = navBackStackEntry.arguments?.getString("testData")
                testData?.let {
                    TestPassingScreen(testData = it, onNavigateToTest = {
                        navController.navigate(Screen.Tests.route)
                    })
                }
            }
        }
    }
}