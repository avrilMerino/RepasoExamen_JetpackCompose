package com.example.repasoexamen_jetpackcompose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.snapshots.SnapshotStateList

@OptIn(ExperimentalMaterial3Api::class)
/*@Composable
fun PantallaFormulario(
    onNavigateToResumen: (String, String, String) -> Unit
)*/
@Composable
fun PantallaFormulario(
    onNavigateToResumen: (String, String, String) -> Unit,
    pedidoCompartido: SnapshotStateList<ItemPedido>,
    onPromoChange: (String) -> Unit
){
    // =========================================================
    // ESTADOS OBLIGATORIOS (remember + state)
    // =========================================================
    // 1) Nombre cliente
    var nombreCliente by remember { mutableStateOf("") }

    // 2) Dirección
    var direccion by remember { mutableStateOf("") }

    // 3) Tipo de entrega (RadioButtons): "Normal" o "Express"
    var tipoEntrega by remember { mutableStateOf("Normal") }

    // 4) Metodo de pago (DropdownMenu)
    var metodoPago by remember { mutableStateOf("Tarjeta") }

    // 5) Código promocional
    var codigoPromo by remember { mutableStateOf("") }


    // 6) Pedido (lista mutable observable)
    //val pedido =remember { mutableStateListOf<ItemPedido>() } //se pobse asu es pq el contenido cambia

    val pedido = pedidoCompartido

    // 7) Error (null = sin error, String = mensaje)
    var error by remember { mutableStateOf<String?>(null) }

    // 8) Control del menú desplegable (Dropdown)
    var menuPagoExpandido by remember { mutableStateOf(false) }

    // =========================================================
    // LISTAS FIJAS (no son state porque no cambian)
    // =========================================================

    val tiposDeEntrega = listOf("Normal", "Express")

    val metodosPago = listOf("Tarjeta", "Efectivo", "Bizum")

    // Catálogo fijo (lo usaremos luego para LazyColumn + botón +)
    val productosDisponibles = remember {
        listOf(
            Producto(1, "Pizza", 9.99),
            Producto(2, "Hamburguesa", 8.49),
            Producto(3, "Ensalada", 6.50),
            Producto(4, "Bebida", 1.99),
            Producto(5, "Postre", 3.75)
        )
    }

    // -----------------------------
    // Función: añadir producto al pedido
    // - Si existe, incrementa cantidad
    // - Si no existe, añade con cantidad 1
    // -----------------------------
    fun agregarProducto(producto: Producto) {
        val existente = pedido.find { it.producto.id == producto.id }

        if (existente == null) {
            pedido.add(ItemPedido(producto = producto, cantidad = 1))
        } else {
            val index = pedido.indexOf(existente)
            pedido[index] = existente.copy(cantidad = existente.cantidad + 1)
        }
    }
    // Elimina una línea del pedido (da igual la cantidad)
    fun eliminarItem(item: ItemPedido) {
        pedido.remove(item)
    }

    // Modifica la cantidad (+1 o -1). Si llega a 0, elimina la línea.
    fun modificarCantidad(item: ItemPedido, incremento: Int) {
        val nuevaCantidad = item.cantidad + incremento

        if (nuevaCantidad <= 0) {
            pedido.remove(item)
        } else {
            val index = pedido.indexOf(item)
            pedido[index] = item.copy(cantidad = nuevaCantidad)
        }
    }
    // -----------------------------
    // Función: validar código promocional
    // -----------------------------
    fun promoValida(codigo: String): Boolean {
        val codigosValidos = listOf("PROMO10", "ENVIOGRATIS")
        return codigosValidos.any { it.equals(codigo, ignoreCase = true) }
    }
    // =========================================================
    // (De momento) UI mínima para que lo pruebes y veas que compila
    // =========================================================

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text("📦 Pedido a Domicilio", style = MaterialTheme.typography.headlineSmall)
        }

        // -----------------------------
        // 1) Nombre
        // -----------------------------
        item { Text("Nombre del cliente", fontWeight = FontWeight.SemiBold) }

        item {
            OutlinedTextField(
                value = nombreCliente,
                onValueChange = { nombreCliente = it; error = null },
                label = { Text("Nombre") },
                placeholder = { Text("Ej: Aarón Quevedo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = (error != null && nombreCliente.isBlank())
            )
        }

        item { Divider() }

        // -----------------------------
        // 2) Dirección
        // -----------------------------
        item { Text("Dirección", fontWeight = FontWeight.SemiBold) }

        item {
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it; error = null },
                label = { Text("Dirección") },
                placeholder = { Text("Ej: Calle Mayor 12") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = (error != null && direccion.isBlank())
            )
        }

        // Error
        if (error != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        item { Divider() }

        // -----------------------------
        // 3) Tipo entrega (RadioButtons)
        // -----------------------------
        item { Text("Tipo de entrega", fontWeight = FontWeight.SemiBold) }

        item {
            Column(modifier = Modifier.selectableGroup()) {
                tiposDeEntrega.forEach { opcion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (tipoEntrega == opcion),
                            onClick = { tipoEntrega = opcion }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (opcion == "Normal") "Normal (0€)" else "Express (+3.99€)")
                    }
                }
            }
        }

        item { Divider() }

        // -----------------------------
        // 4) Metodo de pago (Dropdown)
        // -----------------------------
        item { Text("Método de pago", fontWeight = FontWeight.SemiBold) }

        item {
            Box {
                OutlinedButton(
                    onClick = { menuPagoExpandido = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = metodoPago, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (menuPagoExpandido) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Desplegar"
                    )
                }

                DropdownMenu(
                    expanded = menuPagoExpandido,
                    onDismissRequest = { menuPagoExpandido = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    metodosPago.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                metodoPago = opcion
                                menuPagoExpandido = false
                            }
                        )
                    }
                }
            }
        }

        item { Divider() }

        // -----------------------------
        // 5) Productos disponibles (LazyColumn dentro NO: aquí usamos items directos)
        // -----------------------------
        item { Text("Productos disponibles", fontWeight = FontWeight.SemiBold) }

        items(productosDisponibles) { producto ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(producto.nombre, fontWeight = FontWeight.Bold)
                        Text(String.format("%.2f€", producto.precio))
                    }
                    IconButton(onClick = { agregarProducto(producto) }) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        item { Divider() }

        item { Text("Tu pedido", fontWeight = FontWeight.SemiBold) }

        // Si está vacío
        if (pedido.isEmpty()) {
            item { Text("🛒 El pedido está vacío") }
        } else {
            // Lista de líneas del pedido
            items(pedido) { itemPedido ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Info del producto
                        Column(modifier = Modifier.weight(1f)) {
                            Text(itemPedido.producto.nombre, fontWeight = FontWeight.Bold)
                            Text(String.format("%.2f€ x %d", itemPedido.producto.precio, itemPedido.cantidad))
                        }

                        // Botón -
                        IconButton(onClick = { modificarCantidad(itemPedido, -1) }) {
                            Text("−")
                        }

                        // Cantidad
                        Text("${itemPedido.cantidad}", modifier = Modifier.padding(horizontal = 6.dp))

                        // Botón +
                        IconButton(onClick = { modificarCantidad(itemPedido, +1) }) {
                            Text("+")
                        }

                        // Botón eliminar
                        IconButton(onClick = { eliminarItem(itemPedido) }) {
                            Text("🗑️")
                        }
                    }
                }
            }

            // Unidades totales (útil para comprobar)
            item {
                Text("Unidades totales: ${pedido.sumOf { it.cantidad }}")
            }
        }
        item { Divider() }


        item { Divider() }

        // -----------------------------
        // 6) Código promocional
        // -----------------------------
        item { Text("Código promocional", fontWeight = FontWeight.SemiBold) }

        item {
            OutlinedTextField(
                value = codigoPromo,
                onValueChange = { codigoPromo = it },
                label = { Text("Código (PROMO10 / ENVIOGRATIS)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Mensajito opcional para feedback rápido
            if (codigoPromo.isNotBlank()) {
                val ok = promoValida(codigoPromo)
                Text(
                    text = if (ok) "✅ Código válido" else "❌ Código no válido",
                    color = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }

        item { Divider() }

        // -----------------------------
        // 7) Botón final: navegar a resumen
        // -----------------------------
        item {
            Button(
                onClick = {
                    // Validación completa antes de navegar
                    when {
                        nombreCliente.isBlank() -> error = "El nombre no puede estar vacío"
                        direccion.isBlank() -> error = "La dirección no puede estar vacía"
                        pedido.isEmpty() -> error = " Añade al menos 1 producto al pedido"
                        else -> {
                            error = null
                            // Navegamos pasando 3 valores (como tu callback)
                            onNavigateToResumen(nombreCliente, direccion, tipoEntrega)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ver resumen")
            }
        }

        // -----------------------------
        // Botón validar
        // -----------------------------
        item {
            Button(
                onClick = {
                    if (nombreCliente.isBlank()) error = "❌ El nombre no puede estar vacío"
                    else if (direccion.isBlank()) error = "❌ La dirección no puede estar vacía"
                    else error = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Validar datos")
            }
        }
    }
}

