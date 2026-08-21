package com.inttelgo.tecnicos.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.network.RetrofitClient
import com.inttelgo.tecnicos.ui.view.HomeScreen
import com.inttelgo.tecnicos.ui.view.InstalacionScreen
import com.inttelgo.tecnicos.ui.view.LoginScreen
import com.inttelgo.tecnicos.ui.view.ProfileScreen
import com.inttelgo.tecnicos.ui.view.SupportScreen
import com.inttelgo.tecnicos.ui.view.TareaScreen
import com.inttelgo.tecnicos.ui.view.UploadImgScreen
import com.inttelgo.tecnicos.viewmodel.JornadaViewModel
import com.inttelgo.tecnicos.viewmodel.LoginViewModel

private fun NavHostController.navigateToHomeSection(section: HomeSection) {
    navigate(Home(section)) {
        popUpTo<Home> { inclusive = true }
        launchSingleTop = true
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavigation (context: Context){
    val navController = rememberNavController()
    val userPreferences = remember { UserPreferences(context) }
    val loginViewModel = remember { LoginViewModel() }
    val jornadaViewModel = remember { JornadaViewModel() }

    // Inicializar RetrofitClient con el contexto
    LaunchedEffect (Unit) {
        RetrofitClient.initialize(context)
        // Una sola carga de jornada al abrir la app (si ya hay sesión).
        if (userPreferences.getUser() != null && userPreferences.isTokenValid()) {
            jornadaViewModel.loadJornadaOnce(context)
        }
    }

    // Determinar la ruta inicial
    val startDestination = remember {
        if (userPreferences.getUser() != null && userPreferences.isTokenValid()) {
            Home()
        } else {
            Login
        }
    }


    NavHost(navController, startDestination){
        composable<Login> {
            // Auto-login si hay credenciales guardadas
            LaunchedEffect(Unit) {
                if (userPreferences.hasSavedCredentials() && userPreferences.getUser() != null) {

                    loginViewModel.autoLogin(
                        context,
                        navigateToHome = {
                            jornadaViewModel.loadJornadaOnce(context)
                            navController.navigate(Home()) {
                                popUpTo<Login> { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        navigateToLogin = {
                        }
                    )
                }
            }

            LoginScreen (context){
                jornadaViewModel.loadJornadaOnce(context)
                navController.navigate(Home()) {
                    popUpTo<Login> { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable<Home>{ backStackEntry ->
            val home: Home = backStackEntry.toRoute()
            HomeScreen (
                context,
                selectedSection = home.section,
                onSectionSelected = { section ->
                    if (section != home.section) {
                        navController.navigateToHomeSection(section)
                    }
                },
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))},
                {id -> navController.navigate(Tarea(id))},
                { id -> navController.navigate(Instalacion(id)) },
                { navController.navigate(Profile)},
                jornadaViewModel = jornadaViewModel
            )
        }
        composable<UploadImg>{ backStackEntry ->
            val detail: UploadImg = backStackEntry.toRoute()
            UploadImgScreen(
                detail.id,
                detail.type,
                context,
                navigateToHome = {
                    navController.navigateToHomeSection(homeSectionFromUploadType(detail.type))
                },
                navigateToUp = {
                    // Regresa a la pantalla anterior (detalle de ticket/tarea/instalación).
                    if (!navController.navigateUp()) {
                        navController.navigateToHomeSection(homeSectionFromUploadType(detail.type))
                    }
                },
                navigateToProfile = { navController.navigate(Profile) }
            )
        }
        composable<Support>{ backStackEntry ->
            val detail: Support = backStackEntry.toRoute()
            SupportScreen(detail.idSupport, context,{ id, type -> navController.navigate(UploadImg(id,type)) }, { navController.navigate(Profile)})
        }
        composable<Tarea>{ backStackEntry ->
            val detail: Tarea = backStackEntry.toRoute()
            TareaScreen(detail.idTarea, context, { id, type -> navController.navigate(UploadImg(id,type)) }, { navController.navigate(Profile)})
        }
        composable<Instalacion> { backStackEntry ->
            val detail: Instalacion = backStackEntry.toRoute()
            InstalacionScreen(
                detail.idInstalacion,
                context,
                { id, type -> navController.navigate(UploadImg(id, type)) },
                { navController.navigate(Profile) }
            )
        }
        composable<Profile> {
            ProfileScreen(context){
                jornadaViewModel.resetSession()
                navController.navigate(Login)
            }
        }
    }
}
