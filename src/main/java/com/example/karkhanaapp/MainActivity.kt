package com.example.karkhanaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to SetFarmLocation screen
        startActivity(Intent(this, SetFarmLocation::class.java))

        finish()
    }
}