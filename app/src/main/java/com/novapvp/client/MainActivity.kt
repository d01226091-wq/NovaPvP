package com.novapvp.client

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val playButton = findViewById<Button>(R.id.playButton)
        val serversButton = findViewById<Button>(R.id.serversButton)
        val profileButton = findViewById<Button>(R.id.profileButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val exitButton = findViewById<Button>(R.id.exitButton)

        playButton.setOnClickListener {
            openGame()
        }

        serversButton.setOnClickListener {
            Toast.makeText(
                this,
                "Серверы NovaPvP",
                Toast.LENGTH_SHORT
            ).show()
        }

        profileButton.setOnClickListener {
            Toast.makeText(
                this,
                "Профиль: NovaPlayer",
                Toast.LENGTH_SHORT
            ).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(
                this,
                "Настройки NovaPvP",
                Toast.LENGTH_SHORT
            ).show()
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    private fun openGame() {
        val gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onBackPressed() {
        setContentView(R.layout.activity_main)

        onCreate(null)
    }
}
