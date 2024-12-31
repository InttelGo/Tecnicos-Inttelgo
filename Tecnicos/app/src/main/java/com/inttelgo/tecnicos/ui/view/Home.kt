package com.inttelgo.tecnicos.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.PriorityCard
import com.inttelgo.tecnicos.components.SearchInput
import com.inttelgo.tecnicos.components.TargetCustom
import com.inttelgo.tecnicos.navigation.EnumNavigation

@Preview
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()

    NavHost(navController, EnumNavigation.HOME.toString()){
        composable(EnumNavigation.HOME.toString()) {
            Home(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController){
    val search = remember { mutableStateOf("") }
    val targetValue = remember { mutableStateOf("Procesos") }
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo_inttelgo_2),
                        contentDescription = null,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(15.dp),
                    )
                }
            )
        }
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))
            Row {
                Spacer(Modifier.width(20.dp))
                TargetCustom("Soportes", (targetValue.value == "Soporte")) {
                    targetValue.value = "Soporte"
                }
                Spacer(Modifier.width(5.dp))
                TargetCustom("Procesos", (targetValue.value == "Procesos")) {
                    targetValue.value = "Procesos"
                }
            }
            Spacer(Modifier.height(12.dp))
            when(targetValue.value){
                "Soporte" -> Soport(targetValue.value)
                "Procesos" -> Process(targetValue.value, search, navController)
            }
        }
    }
}

@Composable
private fun Soport(title: String){
    Text(
        title,
        Modifier.padding(start = 20.dp)
    )

    LazyColumn (
        Modifier.padding(20.dp).height(650.dp)
    ){
        items(5) { index ->
            Card (
                Modifier.fillMaxWidth()
            ){
                Spacer(Modifier.height(5.dp))
                Row (
                    Modifier.fillMaxWidth()
                       .padding(15.dp)
                ){
                    Text("$index")
                    Spacer(Modifier.width(250.dp))
                    PriorityCard()
                }
                Row (Modifier.padding(15.dp).fillMaxWidth()){
                    Text("Icono")
                    Spacer(Modifier.width(15.dp))
                    Column{
                        Text("Id Cliente")
                        Spacer(Modifier.height(5.dp))
                        Text("Nombre Cliente")
                        Spacer(Modifier.height(5.dp))
                        Text("Numeros Telefonicos")
                        Spacer(Modifier.height(5.dp))
                        Text("Direccion: ")
                        Spacer(Modifier.height(5.dp))
                        Text("Observacion")
                    }
                }
                Spacer(Modifier.height(5.dp))
                ButtonRainbow("Ver más", Modifier.width(70.dp).align(Alignment.CenterHorizontally)) { }
                Spacer(Modifier.height(15.dp))
            }
            Spacer(Modifier.height(15.dp))
        }
    }
}

@Composable
private fun Process(title: String, search: MutableState<String>, navController: NavController){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){
        SearchInput(search) { }
        Spacer(Modifier.height(3.dp))
        Spacer(Modifier.height(1.dp).background(Color.Black).width(350.dp))
        Spacer(Modifier.height(3.dp))
        Card (Modifier.fillMaxWidth().padding(20.dp)){
            Column (Modifier.padding(20.dp)){
                Text(
                    "id Proceso",
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "id Cliente",
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Nombre Cliente",
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Numero telefonico",
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Direccion",
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ultima observacion",
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .verticalScroll(rememberScrollState()) // Habilita el desplazamiento horizontal
                ) {
                    Text(
                        text = "Este es un texto de prioridad muy largo que puede deslizarse horizontalmente para ver todo el contenido."
                    )
                }
                Spacer(Modifier.height(10.dp))
                ButtonRainbow("Iniciar Proceso", Modifier.fillMaxWidth()) {
                    navController.navigate(EnumNavigation.UPLOAD_IMAGE.toString())
                }
            }
        }
    }
}