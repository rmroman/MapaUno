package mx.rmr.mapauno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import mx.rmr.mapauno.ui.theme.MapaUnoTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapaUnoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppPrincipal(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppPrincipal(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(
                text = "Usa mapas de Google",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Box(modifier = modifier
            .fillMaxWidth()
            .weight(1f)) {
            MapScreen()
        }
    }
}


@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val zocalo = LatLng(19.4330263, -99.1358623)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(zocalo, 15f)
    }

    val coroutineScope = rememberCoroutineScope()

    // Escuchar cuando la cámara deje de moverse (Equivalente a OnCameraIdle / dragend)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            // Validar que el movimiento fue por un gesto del usuario
            if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                val centroActual = cameraPositionState.position.target
                val zoomActual = cameraPositionState.position.zoom

                println("El usuario movió el mapa a: ${centroActual.latitude}, ${centroActual.longitude}")
                // SOLICITAR LA DIRECCIÓN EN TEXTO PARA ACTUALIZARLA
                println("Zoom actual: $zoomActual")
            }
        }
    }

    Box (modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                val pos = it
                println("Click en el mapa: ${it.latitude}, ${it.longitude}")
                // Mover la cámara de forma asíncrona hacia las nuevas coordenadas
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLng(pos),
                        durationMs = 1000
                    )
                }
            }
        ) {
            // MARCADOR
            Marker(
                state = MarkerState(position = zocalo),
                title = "Zócalo",
                snippet = "Zócalo de la CDMX"
            )
        }
        // CENTRO
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Centro del mapa",
            tint = Color.Red,
            modifier = modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppPrincipalPreview() {
    MapaUnoTheme {
        AppPrincipal()
    }
}