package com.example.repasoexamen_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repasoexamen_jetpackcompose.ui.theme.RepasoExamen_JetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepasoExamen_JetpackComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Estados "compartidos" (light) para pasar info entre pantallas
    val pedidoCompartido = remember { mutableStateListOf<ItemPedido>() }
    var entregaCompartida by remember { mutableStateOf("Normal") }
    var promoCompartida by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "formulario") {
        composable("formulario") {
            PantallaFormulario(
                onNavigateToResumen = { nombre, direccion, entrega ->
                    // guardamos entrega (y el resto si quisieras luego)
                    entregaCompartida = entrega
                    navController.navigate("resumen")
                },
                // ✅ añade estos 2 parámetros a tu PantallaFormulario (te digo abajo cómo)
                pedidoCompartido = pedidoCompartido,
                onPromoChange = { promoCompartida = it }
            )
        }

        composable("resumen") {
            PantallaResumen(
                pedido = pedidoCompartido,
                tipoEntrega = entregaCompartida,
                codigoPromo = promoCompartida,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}