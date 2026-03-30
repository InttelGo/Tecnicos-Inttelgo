package com.inttelgo.tecnicos.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.network.RetrofitClient
import com.inttelgo.tecnicos.ui.view.HomeScreen
import com.inttelgo.tecnicos.ui.view.LoginScreen
import com.inttelgo.tecnicos.ui.view.ProfileScreen
import com.inttelgo.tecnicos.ui.view.SupportScreen
import com.inttelgo.tecnicos.ui.view.TareaScreen
import com.inttelgo.tecnicos.ui.view.UploadImgScreen
import com.inttelgo.tecnicos.viewmodel.LoginViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavigation (context: Context){
    val navController = rememberNavController()
    val userPreferences = remember { UserPreferences(context) }
    val loginViewModel = remember { LoginViewModel() }

    // Inicializar RetrofitClient con el contexto
    LaunchedEffect (Unit) {
        RetrofitClient.initialize(context)
    }

    // Determinar la ruta inicial
    val startDestination = remember {
        if (userPreferences.getUser() != null && userPreferences.isTokenValid()) {
            Home
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
                            navController.navigate(Home) {
                                popUpTo<Login> { inclusive = true }
                            }
                        },
                        navigateToLogin = {
                        }
                    )
                }
            }

            LoginScreen (context){
                navController.navigate(Home){
                    popUpTo<Home>{inclusive=true}
                }
            }
        }
        composable<Home>{
            HomeScreen (context,
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))},
                {id -> navController.navigate(Tarea(id))},
                { navController.navigate(Login)},
                { navController.navigate(Profile)}
            )
        }
        composable<UploadImg>{ backStackEntry ->
            val detail: UploadImg = backStackEntry.toRoute()
            UploadImgScreen(detail.id, detail.type, context,{
                    navController.navigate(Home){
                        popUpTo<Home>{inclusive=true}
                    }
                },
                {
                    if(detail.type == "Soporte"){
                        navController.navigateUp()
                    }else{
                        navController.navigate(Home){
                            popUpTo<Home>{inclusive=true}
                        }
                    }
                },
                { navController.navigate(Profile)}
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
        composable<Profile> {
            ProfileScreen(context){ navController.navigate(Login)}
        }
    }
}