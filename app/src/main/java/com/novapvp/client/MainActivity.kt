package com.novapvp.client

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainMenu()
    }

    private fun showMainMenu() {
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.playButton).setOnClickListener {
            showGameScreen()
        }

        findViewById<Button>(R.id.serversButton).setOnClickListener {
            showServers()
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            showSettings()
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            showProfile()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finish()
        }
    }

    private fun showGameScreen() {
        setContentView(R.layout.game_screen)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            showMainMenu()
        }
    }

    private fun showServers() {
        AlertDialog.Builder(this)
            .setTitle("Серверы NovaPvP")
            .setMessage("Сервер пока не настроен.")
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun showSettings() {
        Toast.makeText(this, "Настройки NovaPvP", Toast.LENGTH_SHORT).show()
    }

    private fun showProfile() {
        AlertDialog.Builder(this)
            .setTitle("Профиль")
            .setMessage("Игрок: NovaPlayer\nNovaPvP 1.0")
            .setPositiveButton("ОК", null)
            .show()
    }

    override fun onBackPressed() {
        showMainMenu()
    }
}
