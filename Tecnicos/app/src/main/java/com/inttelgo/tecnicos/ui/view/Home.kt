package com.inttelgo.tecnicos.ui.view

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.InternetAccess
import com.inttelgo.tecnicos.components.PrioritiesCard
import com.inttelgo.tecnicos.components.PriorityCard
import com.inttelgo.tecnicos.components.SearchInput
import com.inttelgo.tecnicos.components.TargetCustom
import com.inttelgo.tecnicos.components.TextButtonForm
import com.inttelgo.tecnicos.components.WarningCard
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.logic.process.homeProcess
import com.inttelgo.tecnicos.navigation.Home
import com.inttelgo.tecnicos.navigation.Login
import com.inttelgo.tecnicos.navigation.Support
import com.inttelgo.tecnicos.navigation.UploadImg
import com.inttelgo.tecnicos.ui.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.ui.viewmodel.LoginViewModel

@Preview
@Composable
fun HomeScreenPreview(){
    val navController = rememberNavController()
    NavHost(navController, Home){
        composable<Home>{
            HomeScreen (
                LocalContext.current,
                { id,type -> navController.navigate(UploadImg(id,type)) },
                { id -> navController.navigate(Support(id))},
                { navController.navigate(Login)}
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(context: Context, navigateToUploadImage: (id:String, type:String) -> Unit, navigateToSupport: (idSupport: String) -> Unit, navigateToLogin: () -> Unit){
    val viewModelL: LoginViewModel = remember { LoginViewModel() }
    val viewModelH: HomeViewModel = remember { HomeViewModel() }
    val targetValue = remember { mutableStateOf("Soporte") }
    val userData by viewModelL.userData.collectAsState()
    val expanded = remember { mutableStateOf(false) }

    val hasFineLocation = remember { mutableStateOf(false) }
    val hasCoarseLocation = remember { mutableStateOf(false) }

    // Lanzador para múltiples permisos
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Actualiza el estado en base a los resultados
        hasFineLocation.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        hasCoarseLocation.value = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    }
    // Lanzar la solicitud de permisos
    LaunchedEffect(Unit) {
        if(!hasFineLocation.value || !hasFineLocation.value){
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
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
                        },
                    actions = {
                        IconButton(onClick = { expanded.value = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(
                                onClick = { /* TODO */ },
                                text = { Text(text = "Perfil") }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    userPreferences.clearUser()
                                },
                                text = { Text(text = "Cerrar sesión") }
                            )
                        }
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
                    "Soporte" -> Soport(viewModelH, targetValue.value, navigateToSupport, context)
                    "Procesos" -> Process(viewModelH, targetValue.value, navigateToUploadImage, context)
                }
            }
        }
    }
}

@Composable
private fun Soport(
    viewModelH: HomeViewModel,
    title: String,
    navigateToSupport: (idSupport: String) -> Unit,
    context: Context
){
    val prioritySelected = remember { mutableIntStateOf(0) }
    val tickets by viewModelH.tickets.collectAsState()
    val checkProcess by viewModelH.checkProcess.collectAsState()
    val checkBarrios by viewModelH.checkBarrios.collectAsState()
    val barrios by viewModelH.barrios.collectAsState()

    val hasInternetConnection = rememberNetworkConnectivityState(context)

    Text(
        title,
        Modifier.padding(start = 20.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(Modifier.height(5.dp))
    PrioritiesCard(prioritySelected)

    Spacer(Modifier.height(5.dp))
    if (!hasInternetConnection.value) { // Mostrar algo si no hay conexión
        InternetAccess(hasInternetConnection.value)
    }else{
        LaunchedEffect(prioritySelected.intValue) { // Update launchedEffect to trigger on prioritySelected change
            viewModelH.ticketsList(prioritySelected) // Use prioritySelected.value directly
        }
    }
    if(!checkProcess && !checkBarrios){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedIcon()
        }
    }else{
        val ticketsArreglados = tickets?.let { barrios?.let { it1 -> homeProcess().generarConjunto(it, it1) } }
        LazyColumn (
            Modifier.padding(20.dp).fillMaxHeight()
        ){
            ticketsArreglados?.forEach { (barrio, ticketsConBarrio) ->
                if(ticketsConBarrio.isNotEmpty()){
                    item {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Barrio: $barrio",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    items(ticketsConBarrio) { ticket ->
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
                                        Text("#Cliente: ${ticket.cliente.nroCliente}")
                                        Spacer(Modifier.height(5.dp))
                                        Text("Cliente: ${ticket.cliente.nombre_1} ${ticket.cliente.apellido_1}")
                                        Spacer(Modifier.height(5.dp))
                                        Text("Telefonos: " +
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
    }
}

@Composable
private fun Process(
    viewModelH: HomeViewModel,
    title: String,
    navigateToUploadImage: (id: String, type: String) -> Unit,
    context: Context
){
    val process = viewModelH.processData.collectAsState()
    val checkProcessData = viewModelH.checkProcessData.collectAsState()
    val search = remember { mutableStateOf("") }
    val errorMessage = viewModelH.errorMessage.collectAsState()
    val warningMessage = viewModelH.warningMessage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){
        SearchInput(search) {
            if(hasInternetConnection.value){
                if(search.value.isNotEmpty()){
                    viewModelH.setNofifies()
                }
                viewModelH.searchProcess(search.value)
            }
        }
        Spacer(Modifier.height(5.dp))
        Spacer(Modifier.height(1.dp).background(Color.Black).width(350.dp))
        Spacer(Modifier.height(5.dp))


        if (!hasInternetConnection.value) {
            InternetAccess(hasInternetConnection.value)
        }
        errorMessage.value?.let { AlertCard(it) }
        warningMessage.value?.let { WarningCard(it) }

        if(checkProcessData.value){
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
                    ButtonRainbow("Iniciar Proceso", Modifier.fillMaxWidth(), true) {
                        viewModelH.ActualizarEstadoI(process.value!!.id_instalacion, navigateToUploadImage)
                    }
                }
            }
        }else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedIcon()
            }
        }
    }
}