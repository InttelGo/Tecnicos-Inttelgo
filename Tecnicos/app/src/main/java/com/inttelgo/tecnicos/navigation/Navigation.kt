package com.inttelgo.tecnicos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inttelgo.tecnicos.ui.view.Home
import com.inttelgo.tecnicos.ui.view.Login
import com.inttelgo.tecnicos.ui.view.UploadImg

@Composable
fun AppNavigation (){
    val navController = rememberNavController()

    NavHost(navController, EnumNavigation.LOGIN.toString()){
        composable(EnumNavigation.LOGIN.toString()) {
            Login(navController)
        }
        composable(EnumNavigation.HOME.toString()) {
            Home(navController)
        }
        composable(EnumNavigation.UPLOAD_IMAGE.toString()){
            UploadImg(navController)
        }

    }


}