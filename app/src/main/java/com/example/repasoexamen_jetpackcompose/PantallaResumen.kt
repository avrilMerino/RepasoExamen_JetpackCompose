package com.example.repasoexamen_jetpackcompose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaResumen(
    pedido: List<ItemPedido>,
    tipoEntrega: String,
    codigoPromo: String,
    onVolver: () -> Unit
) {

    // =============================
    // VARIABLES DERIVADAS (CLAVE EXAMEN)
    // =============================

    // Subtotal = suma de precio * cantidad
    val subtotal by remember(pedido) {
        derivedStateOf {
            pedido.sumOf { it.producto.precio * it.cantidad }
        }
    }

    // Descuento (10% si código PROMO10)
    val descuento by remember(subtotal, codigoPromo) {
        derivedStateOf {
            if (codigoPromo.equals("PROMO10", ignoreCase = true)) {
                subtotal * 0.10
            } else {
                0.0
            }
        }
    }

    // Subtotal con descuento
    val subtotalConDescuento by remember(subtotal, descuento) {
        derivedStateOf {
            subtotal - descuento
        }
    }

    // Coste de envío
    val envio by remember(tipoEntrega, codigoPromo) {
        derivedStateOf {
            when {
                codigoPromo.equals("ENVIOGRATIS", ignoreCase = true) -> 0.0
                tipoEntrega == "Express" -> 3.99
                else -> 0.0
            }
        }
    }

    // Total final
    val total by remember(subtotalConDescuento, envio) {
        derivedStateOf {
            subtotalConDescuento + envio
        }
    }

    // =============================
    // UI
    // =============================

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del pedido") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // -----------------------------
            // LISTA DE PRODUCTOS
            // -----------------------------
            Text("Productos", fontWeight = FontWeight.Bold)

            if (pedido.isEmpty()) {
                Text("No hay productos en el pedido")
            } else {
                pedido.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.producto.nombre} x${item.cantidad}")
                        Text(String.format("%.2f€", item.producto.precio * item.cantidad))
                    }
                }
            }

            Divider()

            // -----------------------------
            // DESGLOSE
            // -----------------------------
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal")
                Text(String.format("%.2f€", subtotal))
            }

            if (descuento > 0) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Descuento")
                    Text("-${String.format("%.2f€", descuento)}")
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Envío ($tipoEntrega)")
                Text(String.format("%.2f€", envio))
            }

            Divider(thickness = 2.dp)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TOTAL", fontWeight = FontWeight.Bold)
                Text(String.format("%.2f€", total), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // -----------------------------
            // BOTÓN VOLVER
            // -----------------------------
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}