package com.inttelgo.tecnicos.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.PassFlied
import com.inttelgo.tecnicos.components.TextFlieldCustom
import com.inttelgo.tecnicos.ui.viewmodel.LoginViewModel

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen {  }
}

@Composable
fun LoginScreen(navigateToHome: () -> Unit) {
    val viewModelL: LoginViewModel= remember { LoginViewModel() }
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoggedIn by viewModelL.isLoggedIn.collectAsState()
    val errorMessage by viewModelL.errorMessage.collectAsState()
    Scaffold { innerPadding ->
        Column  (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Image
            Image(painterResource(
                R.drawable.logo_inttelgo),
                "InttelGo-Logo.png",
                modifier = Modifier.width(300.dp)
            )
            Spacer(Modifier.height(300.dp))
            errorMessage?.let {
                AlertCard(errorMessage!!)
                Spacer(Modifier.height(10.dp))
            }
            //Form
            TextFlieldCustom("Nombre Usuario", userName, 300.dp)
            Spacer(Modifier.height(5.dp))
            PassFlied(password, "Contraseña", 300.dp)
            Spacer(Modifier.height(50.dp))
            ButtonRainbow("Iniciar Sesion", Modifier.width(300.dp)){
                viewModelL.loginWithEmail(userName.value, password.value)
                navigateToHome()
            }
        }
    }
}