package com.inttelgo.tecnicos.navigation

import android.content.Context
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
fun AppNavigation (context: Context){
    val navController = rememberNavController()

    NavHost(navController, Home){
        composable<Login> {
            LoginScreen (context){ navController.navigate(Home){
                popUpTo<Home>{inclusive=true}
            } }
        }
        composable<Home>{
            HomeScreen (context,
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))},
                { navController.navigate(Login)}
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