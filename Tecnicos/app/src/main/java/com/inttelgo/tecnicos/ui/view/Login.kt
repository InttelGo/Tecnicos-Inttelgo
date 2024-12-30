package com.inttelgo.tecnicos.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.PassFlied
import com.inttelgo.tecnicos.components.TextFlieldCustom

@Preview
@Composable
fun LoginPreview() {
    Login()
}

@Composable
fun Login() {
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Scaffold { innerPadding ->
        Column  (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Image
            Image(painterResource(R.drawable.logo_inttelgo), "InttelGo-Logo.png")
            Spacer(Modifier.height(300.dp))
            //Form
            TextFlieldCustom("Text", "Nombre Usuario", userName, 300.dp)
            Spacer(Modifier.height(5.dp))
            PassFlied(password, "Contraseña", 300.dp)
            Spacer(Modifier.height(50.dp))
            ButtonRainbow("Iniciar Sesion", Modifier.width(300.dp)){
                /* TODO */
            }
        }
    }
}