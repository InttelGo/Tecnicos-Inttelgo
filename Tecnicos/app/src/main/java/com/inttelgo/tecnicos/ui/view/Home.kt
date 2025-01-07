package com.inttelgo.tecnicos.ui.view

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.PriorityCard
import com.inttelgo.tecnicos.components.SearchInput
import com.inttelgo.tecnicos.components.TargetCustom
import com.inttelgo.tecnicos.components.TextButtonForm
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.ui.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.ui.viewmodel.LoginViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(context: Context, navigateToUploadImage: (id:String, type:String) -> Unit, navigateToSupport: (idSupport: String) -> Unit, navigateToLogin: () -> Unit){
    val viewModelL: LoginViewModel = remember { LoginViewModel() }
    val viewModelH: HomeViewModel = remember { HomeViewModel() }
    val targetValue = remember { mutableStateOf("Soporte") }
    val userData by viewModelL.userData.collectAsState()
    val userPreferences = UserPreferences(context)
    if(userPreferences.getId() == null){
        viewModelL.isLoggedUser(navigateToLogin, userPreferences.getId())
    }else{
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
                    "Soporte" -> Soport(viewModelH, targetValue.value, navigateToSupport)
                    "Procesos" -> Process(viewModelH, targetValue.value, navigateToUploadImage)
                }
            }
        }
    }
}

@Composable
private fun Soport(
    viewModelH: HomeViewModel,
    title: String,
    navigateToSupport: (idSupport: String) -> Unit
){
    LaunchedEffect(Unit) {
        viewModelH.ticketsList()
    }
    val tickets by viewModelH.tickets.collectAsState()
    val checkProcess by viewModelH.checkProcess.collectAsState()
    Text(
        title,
        Modifier.padding(start = 20.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    if(!checkProcess){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
        LazyColumn (
            Modifier.padding(20.dp).height(650.dp)
        ){
            items(tickets!!) { ticket ->
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
                                    "Id Tiecket: ${ticket.id_ticket}",
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
                                PriorityCard(ticket.fecha_hora)
                            }
                        }
                        Row (Modifier.padding(15.dp).fillMaxWidth()){
                            Text("Icono")
                            Spacer(Modifier.width(15.dp))
                            Column{
                                Spacer(Modifier.height(5.dp))
                                Text("Cliente: ${ticket.cliente.nombre_1} ${ticket.cliente.apellido_1}")
                                Spacer(Modifier.height(5.dp))
                                Text("Numeros Telefonicos: " +
                                        ticket.cliente.telefono_1
                                )
                                Spacer(Modifier.height(5.dp))
                                Text("Direccion: ${ticket.cliente.direccion}")
                                Spacer(Modifier.height(5.dp))
                                Text("Observacion: ${ticket.observacion_u}")
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        TextButtonForm("Ver más", true){
                            navigateToSupport(ticket.id_ticket)
                        }
                    }
                    Spacer(Modifier.height(15.dp))
                }
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}

@Composable
private fun Process(
    viewModelH: HomeViewModel,
    title: String,
    navigateToUploadImage: (id: String, type: String) -> Unit
){
    val process = viewModelH.processData.collectAsState()
    val search = remember { mutableStateOf("") }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){
        SearchInput(search) {
            viewModelH.searchProcess(search.value)
        }
        Spacer(Modifier.height(3.dp))
        Spacer(Modifier.height(1.dp).background(Color.Black).width(350.dp))
        Spacer(Modifier.height(3.dp))
        if(process.value != null){
            Card (
                Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ){
                Column (Modifier.padding(20.dp)){
                    Text(
                        "Id: ${process.value!!.id_instalacion}",
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        process.value!!.nombre,
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        process.value!!.telefonos,
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        process.value!!.direccion,
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
                            text = process.value!!.observacion.descripcion
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    ButtonRainbow("Iniciar Proceso", Modifier.fillMaxWidth()) { navigateToUploadImage("id Proceso", "Proceso") }
                }
            }
        }
    }
}