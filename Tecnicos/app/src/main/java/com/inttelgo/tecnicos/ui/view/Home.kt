package com.inttelgo.tecnicos.ui.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.PriorityCard
import com.inttelgo.tecnicos.components.SearchInput
import com.inttelgo.tecnicos.components.TargetCustom
import com.inttelgo.tecnicos.components.TextButtonForm
import com.inttelgo.tecnicos.navigation.Home
import com.inttelgo.tecnicos.navigation.Support
import com.inttelgo.tecnicos.navigation.UploadImg

@Preview
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    NavHost(navController, Home){
        composable<Home>{
            HomeScreen (
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))}
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigateToUploadImage: (id:String, type:String) -> Unit, navigateToSupport: (idSupport: String) -> Unit){

    val search = remember { mutableStateOf("") }
    val targetValue = remember { mutableStateOf("Soporte") }
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
                "Soporte" -> Soport(targetValue.value, navigateToSupport)
                "Procesos" -> Process(targetValue.value, search, navigateToUploadImage)
            }
        }
    }
}

@Composable
private fun Soport(title: String, navigateToSupport: (idSupport: String) -> Unit){
    Text(
        title,
        Modifier.padding(start = 20.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    LazyColumn (
        Modifier.padding(20.dp).height(650.dp)
    ){
        items(5) { index ->
            Card (
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ){
                Column (
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(Modifier.height(5.dp))
                    Row (
                        Modifier.fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ){
                        Column (
                            Modifier.width(200.dp)
                        ){
                            Text(
                                "Id Tiecket: $index",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column (
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            PriorityCard()
                        }
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
                    TextButtonForm("Ver más", true){
                        navigateToSupport("$index")
                    }
                }
                Spacer(Modifier.height(15.dp))
            }
            Spacer(Modifier.height(15.dp))
        }
    }
}

@Composable
private fun Process(title: String, search: MutableState<String>, navigateToUploadImage: (id: String, type: String) -> Unit){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){
        SearchInput(search) {

        }
        Spacer(Modifier.height(3.dp))
        Spacer(Modifier.height(1.dp).background(Color.Black).width(350.dp))
        Spacer(Modifier.height(3.dp))
        Card (
            Modifier.fillMaxWidth().padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ){
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
                        .height(150.dp)
                        .verticalScroll(rememberScrollState()) // Habilita el desplazamiento horizontal
                ) {
                    Text(
                        text = "Este es un texto de prioridad muy largo que puede deslizarse horizontalmente para ver todo el contenido."
                    )
                }
                Spacer(Modifier.height(10.dp))
                ButtonRainbow("Iniciar Proceso", Modifier.fillMaxWidth()) { navigateToUploadImage("id Proceso", "Proceso") }
            }
        }
    }
}