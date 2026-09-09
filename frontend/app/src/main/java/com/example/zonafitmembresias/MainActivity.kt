package com.example.zonafitmembresias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.zonafitmembresias.navigation.ZonaFitNavGraph
import com.example.zonafitmembresias.ui.theme.ZonaFitMembresiasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZonaFitMembresiasTheme {
                ZonaFitNavGraph()
            }
        }
    }
}
