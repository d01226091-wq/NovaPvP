package com.novapvp.client

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.playButton).setOnClickListener {
            Toast.makeText(this, "Играть", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.serversButton).setOnClickListener {
            Toast.makeText(this, "Серверы", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finish()
        }
    }
}
