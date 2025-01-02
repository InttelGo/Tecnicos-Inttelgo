package com.inttelgo.tecnicos.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.inttelgo.tecnicos.ui.view.HomeScreen
import com.inttelgo.tecnicos.ui.view.LoginScreen
import com.inttelgo.tecnicos.ui.view.SupportScreen
import com.inttelgo.tecnicos.ui.view.UploadImgScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation (){
    val navController = rememberNavController()

    NavHost(navController, Login){
        composable<Login> {
            Log.d("Login", "Prueba 1")
            LoginScreen { navController.navigate(Home) }
        }
        composable<Home>{
            HomeScreen (
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))}
            )
        }
        composable<UploadImg>{ backStackEntry ->
            val detail: UploadImg = backStackEntry.toRoute()
            UploadImgScreen(detail.id, detail.type){
                navController.navigate(Home){
                    popUpTo<Home>{inclusive=true}
                }
            }
        }
        composable<Support>{ backStackEntry ->
            val detail: Support = backStackEntry.toRoute()
            SupportScreen(detail.idSupport){ id, type -> navController.navigate(UploadImg(id,type)) }
        }
    }
}