package com.example.socksstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SocksStoreApp()
            }
        }
    }
}

data class SockProduct(
    val id: Int,
    val name: String,
    val material: String,
    val price: Double
)

@Composable
fun SocksStoreApp() {
    val products = listOf(
        SockProduct(1, "Calcetín Deportivo", "Algodón transpirable", 8.99),
        SockProduct(2, "Calcetín Térmico", "Lana merina", 14.50),
        SockProduct(3, "Calcetín Casual", "Bambú suave", 11.25),
        SockProduct(4, "Calcetín Running", "Compresión ligera", 12.75)
    )

    var cart by remember { mutableStateOf(mapOf<Int, Int>()) }
    val total = products.sumOf { product -> (cart[product.id] ?: 0) * product.price }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "SockStore",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Compra de calcetines desde tu móvil",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        quantity = cart[product.id] ?: 0,
                        onAdd = {
                            cart = cart.toMutableMap().also { current ->
                                current[product.id] = (current[product.id] ?: 0) + 1
                            }
                        },
                        onRemove = {
                            cart = cart.toMutableMap().also { current ->
                                val newQuantity = (current[product.id] ?: 0) - 1
                                if (newQuantity > 0) {
                                    current[product.id] = newQuantity
                                } else {
                                    current.remove(product.id)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total: €${"%.2f".format(total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Finalizar compra")
            }
        }
    }
}

@Composable
fun ProductCard(
    product: SockProduct,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = product.material, style = MaterialTheme.typography.bodyMedium)
            Text(text = "€${"%.2f".format(product.price)}", fontWeight = FontWeight.Medium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRemove, enabled = quantity > 0) {
                        Text("-")
                    }
                    Button(onClick = onAdd) {
                        Text("+")
                    }
                }
                Text("Cantidad: $quantity", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
