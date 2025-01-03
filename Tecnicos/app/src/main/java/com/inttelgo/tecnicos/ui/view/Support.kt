package com.inttelgo.tecnicos.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.FloatingButtons


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(idSuport: String, navigateToUploadImage: (id: String, type: String) -> Unit){
    val showHostory = remember { mutableStateOf(true) }
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
                    Text(
                        "$idSuport",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            )

        },
        floatingActionButton = {
            FloatingButtons(idSuport, navigateToUploadImage)
        }
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card (
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ){
                Column(Modifier.padding(20.dp)){
                    Text("id Cliente")
                    Spacer(Modifier.height(5.dp))
                    Text("Nombre Cliente")
                    Spacer(Modifier.height(5.dp))
                    Text("Numeros telefonicos")
                    Spacer(Modifier.height(5.dp))
                    Text("Direccion")
                }
            }
            Spacer(Modifier.height(10.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(Modifier.height(1.dp).width(120.dp).background(Color.Black))
                Card(
                    onClick = {showHostory.value = !showHostory.value},
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ){
                    Row (
                        Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(if(!showHostory.value) "Ver Historial" else "Ver menos")
                        Spacer(Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(if(!showHostory.value) R.drawable.arrow_drop_down_icon else R.drawable.arrow_drop_upward_icon),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
                Spacer(Modifier.height(1.dp).width(120.dp).background(Color.Black))
            }

            if(showHostory.value){
                LazyColumn {
                    items(10) {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ){
                            Row (Modifier.padding(20.dp)){
                                Column (
                                    Modifier.width(250.dp)
                                ){
                                    Text(
                                        "Observacion",
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "Esto es una observacion",
                                        fontSize = 12.sp
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        "DD:MM:AAAA",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }
                                //Pick Icon
                                Column (
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(
                                        "Evidencia",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    IconButton(onClick = {
                                        //ImagePreview(null)
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.photo_library_icon),
                                            contentDescription = "Photo_library icon",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}