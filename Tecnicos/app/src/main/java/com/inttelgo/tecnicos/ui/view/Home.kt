package com.inttelgo.tecnicos.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
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
import com.inttelgo.tecnicos.components.PhoneCard
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
fun HomeScreenPreview(){ //Previsualizacion del home
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
    //Compose principal del home
    val viewModelL: LoginViewModel = remember { LoginViewModel() } //Llamado al view model Login
    val viewModelH: HomeViewModel = remember { HomeViewModel() } //Lamado al view model Home
    val targetValue = remember { mutableStateOf("Soporte") }
    val expanded = remember { mutableStateOf(false) } //Booleano para el navegador lateral

    //Boolean para comprobar los permisos de la aplicacion
    val hasFineLocation = remember { mutableStateOf(false) }
    val hasCoarseLocation = remember { mutableStateOf(false) }

    // Lanzador para verificar permisos de ubicacion
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
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

    val userPreferences = UserPreferences(context) //Usuario logeado en la aplicacion
    if(userPreferences.getId() == null){
        LaunchedEffect(Unit) {
            viewModelL.isLoggedUser(navigateToLogin, userPreferences.getId()) //navegacion para que se logee el usuario
        }
    }else{
        Scaffold ( //Compose para seccionar la ventana
            topBar = {
                TopAppBar( //Divicion para la cabeza de la ventana
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
                            Icon(Icons.Filled.MoreVert, contentDescription = "More") //icon button para desplegar el DropDownMenu
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(
                                onClick = { /* TODO */ },
                                text = { Text(text = "Perfil") } //Informacion del usuario
                            )
                            DropdownMenuItem(
                                onClick = {
                                    userPreferences.clearUser() //Limpa las credenciales del usuario.
                                },
                                text = { Text(text = "Cerrar sesión") } //Cerrar la sesion
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
                Row {//Targetas para seleccionar el tipo de actividad
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
                    "Soporte" -> Soport(viewModelH, targetValue.value, navigateToSupport, context) //Componente para los soportes
                    "Procesos" -> Process(viewModelH, navigateToUploadImage, context) //Componenete para los procesos
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun Soport(
    viewModelH: HomeViewModel,
    title: String,
    navigateToSupport: (idSupport: String) -> Unit,
    context: Context
){
    val prioritySelected = remember { mutableIntStateOf(0) } //Selector de prioridad, por defecto es 0
    val tickets by viewModelH.tickets.collectAsState() //Lista de tickets de la BD
    val checkProcess by viewModelH.checkProcess.collectAsState() //Lanzador para los tickets
    val checkBarrios by viewModelH.checkBarrios.collectAsState() //Lanzador para los varrios
    val barrios by viewModelH.barrios.collectAsState() //Lista de barrios de la BD

    val hasInternetConnection = rememberNetworkConnectivityState(context) //Lanzador para verificar que el uduario cuente con internet

    Text(
        title,
        Modifier.padding(start = 20.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(Modifier.height(5.dp))
    PrioritiesCard(prioritySelected) //Listado de los tipos de prioridad

    Spacer(Modifier.height(5.dp))
    if (!hasInternetConnection.value) {
        InternetAccess(hasInternetConnection.value)  //Si no hay conexion le notifica al usuario
    }else{
        LaunchedEffect(prioritySelected.intValue) { //Si el usuario cuenta con internet lanza la consulta dependiendo de la prioridad que haya seleccionado
            viewModelH.ticketsList(prioritySelected) //consulta de los tickets en la BD
        }
    }
    if(!checkProcess && !checkBarrios){ //Verifica hay datos en las dos listas
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedIcon()
        }
    }else{
        val ticketsArreglados = tickets?.let { barrios?.let { it1 -> homeProcess().generarConjunto(it, it1) } } //Realiza el agrupamiento de los tickets por prefijo del barrio
        LazyColumn (
            Modifier.padding(20.dp).fillMaxHeight()
        ){
            ticketsArreglados?.forEach { (barrio, ticketsConBarrio) -> //Recorrido de los barrio
                if(ticketsConBarrio.isNotEmpty()){ //Si estan vacios no mostrar
                    item {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Barrio: $barrio",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    items(ticketsConBarrio) { ticket -> //Recorrido de los tickets por barrio
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
                                                fontWeight = FontWeight.ExtraBold
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
                                    val imageResId = remember(ticket.tipo.icon) {
                                        context.resources.getIdentifier(ticket.tipo.icon, "drawable", context.packageName) //imagen para el tipo de ticket que es
                                    }

                                    if (imageResId != 0) {
                                        Column (
                                            Modifier.width(100.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(imageResId),
                                                contentDescription = "Imagen de ticket",
                                                modifier = Modifier.size(60.dp)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text(
                                                ticket.tipo.descripcion,
                                                style = TextStyle(
                                                    fontSize = 15.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.DarkGray
                                                )
                                            )
                                        }
                                    } else {
                                        // Aviso si el icono no se encuentra
                                        Text("Ícono no disponible")
                                    }
                                    Spacer(Modifier.width(15.dp))
                                    Column{
                                        Spacer(Modifier.height(5.dp))
                                        Text(
                                            "#Cliente: ${ticket.cliente.nroCliente}",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray
                                            )
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        Text(
                                            "Cliente: ${ticket.cliente.nombre_1} ${ticket.cliente.apellido_1}",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        )
                                        Spacer(Modifier.height(5.dp))

                                        Text(
                                            "Telefonos: ",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        PhoneCard(ticket.cliente.telefono_1)
                                        Spacer(Modifier.height(5.dp))
                                        ticket.cliente.telefono_2?.let { PhoneCard(it) }
                                        Spacer(Modifier.height(5.dp))
                                        Text(
                                            "Direccion: ${ticket.cliente.direccion}",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        Text(
                                            "Observacion: ${ticket.observacion_u}",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        )
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                                TextButtonForm("Ver más", true){
                                    navigateToSupport(ticket.id_ticket) //Navegacion para ver el detalle del ticket
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
    navigateToUploadImage: (id: String, type: String) -> Unit,
    context: Context
){
    val process = viewModelH.processData.collectAsState() //Proceso a buscar
    val checkProcessData = viewModelH.checkProcessData.collectAsState() //Lanzador de proceso encontrado
    val search = remember { mutableStateOf("") } //Buscador del proceso
    val errorMessage = viewModelH.errorMessage.collectAsState() //Mensaje de error si ocurrio algo
    val warningMessage = viewModelH.warningMessage.collectAsState() //Mensaje de alerta si no se encontro el ticket
    val hasInternetConnection = rememberNetworkConnectivityState(context) //verificar de conexion
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

        //Validacion de conexion
        if (!hasInternetConnection.value) {
            InternetAccess(hasInternetConnection.value)
        }

        //Si exiten mensajes por mostrar
        errorMessage.value?.let { AlertCard(it) }
        warningMessage.value?.let { WarningCard(it) }

        if(checkProcessData.value){ //Si se ecntontro el dato
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
                    val numerosSeparados = process.value!!.telefonos.toString()

                    Log.d("Tecnico", process.value.toString())
                    // Convertir los números de cadena a enteros
                    val numero1 = numerosSeparados.substring(0, numerosSeparados.length/2)
                    val numero2 = numerosSeparados.substring(numerosSeparados.length/2, numerosSeparados.length)
                    PhoneCard(numero1.toString())
                    Spacer(Modifier.height(5.dp))
                    PhoneCard(numero2.toString())
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
                    val textButton = if(process.value!!.fecha_ini == null) "Iniciar Proceso" else "Continuar Proceso"
                    ButtonRainbow(textButton, Modifier.fillMaxWidth(), true, true) {
                        /*
                        * Iniciar el proceso de instalacion
                        *
                        * 1. Cambiando el estado en la base de datos el estado 8 == Proceso de instalacion
                        * 2. Navegando hacia el componente para agregar las evidencias
                        *
                        * */
                        val userPreferences = UserPreferences(context)
                        userPreferences.getId()?.let {
                            viewModelH.ActualizarEstadoI(
                                process.value!!.id_instalacion,
                                8,
                                id = it,
                                process.value!!,
                                navigateToUploadImage
                            )
                        }
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