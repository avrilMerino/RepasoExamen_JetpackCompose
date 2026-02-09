package com.example.repasoexamen_jetpackcompose

//1º- Creo las data Class:

data class Cliente(
    val nombre: String,
    val direccion: String
)
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double
)

data class ItemPedido(
    val producto: Producto,
    val cantidad: Int
)