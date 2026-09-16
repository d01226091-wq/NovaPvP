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
            .setMessage(
                "NovaPvP Network\n\n" +
                "Статус: Онлайн\n" +
                "Игроков: 0\n\n" +
                "Сервер будет доступен после подключения."
            )
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun showSettings() {
        AlertDialog.Builder(this)
            .setTitle("Настройки")
            .setItems(
                arrayOf(
                    "Графика",
                    "Звук",
                    "Управление",
                    "Язык"
                )
            ) { _, which ->
                val names = arrayOf(
                    "Графика",
                    "Звук",
                    "Управление",
                    "Язык"
                )

                Toast.makeText(
                    this,
                    "Выбрано: ${names[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("ЗАКРЫТЬ", null)
            .show()
    }

    private fun showProfile() {
        AlertDialog.Builder(this)
            .setTitle("Профиль")
            .setMessage(
                "Игрок: NovaPlayer\n" +
                "Уровень: 1\n" +
                "NovaPvP 1.0"
            )
            .setPositiveButton("ОК", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showMainMenu()
    }
}
