package com.hepakkes.flickrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hepakkes.flickrapp.ui.screens.PhotoGridScreen
import com.hepakkes.flickrapp.ui.theme.FlickrAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlickrAppTheme {
                PhotoGridScreen()
            }
        }
    }
}
