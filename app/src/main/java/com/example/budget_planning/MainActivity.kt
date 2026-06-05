package com.example.budget_planning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budget_planning.data.repository.AppTheme
import com.example.budget_planning.ui.analytics.AnalyticsScreen
import com.example.budget_planning.ui.analytics.AnalyticsViewModel
import com.example.budget_planning.ui.analytics.AnalyticsViewModelFactory
import com.example.budget_planning.ui.budgeting.BudgetingScreen
import com.example.budget_planning.ui.budgeting.BudgetingViewModel
import com.example.budget_planning.ui.budgeting.BudgetingViewModelFactory
import com.example.budget_planning.ui.categories.CategoryManagementScreen
import com.example.budget_planning.ui.categories.CategoryManagementViewModel
import com.example.budget_planning.ui.categories.CategoryManagementViewModelFactory
import com.example.budget_planning.ui.dashboard.DashboardScreen
import com.example.budget_planning.ui.dashboard.DashboardViewModel
import com.example.budget_planning.ui.dashboard.DashboardViewModelFactory
import com.example.budget_planning.ui.theme.BudgetplanningTheme
import com.example.budget_planning.ui.transaction.TransactionLoggingScreen
import com.example.budget_planning.ui.transaction.TransactionViewModel
import com.example.budget_planning.ui.transaction.TransactionViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as BudgetApplication
            val currentTheme by application.themeRepository.themeFlow.collectAsState(initial = AppTheme.LOW_CONTRAST)
            
            BudgetplanningTheme(appTheme = currentTheme) {
                BudgetNavHost(currentTheme)
            }
        }
    }
}

@Composable
fun BudgetNavHost(currentTheme: AppTheme) {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as BudgetApplication
    val repository = application.repository
    val themeRepository = application.themeRepository
    val coroutineScope = rememberCoroutineScope()

    // Epilepsy-friendly transitions: Fluid, gentle fade-through and subtle scale.
    // Replaced bouncy vertical slides with a smooth container-style transformation.
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.95f, animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 1.05f, animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.08f, animationSpec = tween(200))
        }
    ) {
        composable("dashboard") {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(repository)
            )
            DashboardScreen(
                viewModel = viewModel,
                onAddTransaction = { navController.navigate("transaction_form") },
                onEditTransaction = { id -> navController.navigate("transaction_form?transactionId=$id") },
                onNavigateToBudgeting = { navController.navigate("budgeting") },
                onNavigateToAnalytics = { navController.navigate("analytics") },
                onNavigateToCategories = { navController.navigate("categories") },
                currentTheme = currentTheme,
                onThemeToggle = {
                    val nextTheme = if (currentTheme == AppTheme.LOW_CONTRAST) AppTheme.HIGH_CONTRAST else AppTheme.LOW_CONTRAST
                    coroutineScope.launch {
                        themeRepository.setTheme(nextTheme)
                    }
                }
            )
        }
        composable(
            route = "transaction_form?transactionId={transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
            val viewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(repository)
            )
            LaunchedEffect(transactionId) {
                if (transactionId != null) {
                    viewModel.loadTransaction(transactionId)
                }
            }
            TransactionLoggingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("budgeting") {
            val viewModel: BudgetingViewModel = viewModel(
                factory = BudgetingViewModelFactory(repository)
            )
            BudgetingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                currentTheme = currentTheme
            )
        }
        composable("analytics") {
            val viewModel: AnalyticsViewModel = viewModel(
                factory = AnalyticsViewModelFactory(repository)
            )
            AnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                currentTheme = currentTheme
            )
        }
        composable("categories") {
            val viewModel: CategoryManagementViewModel = viewModel(
                factory = CategoryManagementViewModelFactory(repository)
            )
            CategoryManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                currentTheme = currentTheme
            )
        }
    }
}
