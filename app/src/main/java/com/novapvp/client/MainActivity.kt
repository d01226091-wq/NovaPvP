package com.novapvp.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playButton = findViewById<Button>(R.id.playButton)
        val serversButton = findViewById<Button>(R.id.serversButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val exitButton = findViewById<Button>(R.id.exitButton)

        playButton.setOnClickListener {
            openMinecraft()
        }

        serversButton.setOnClickListener {
            showServers()
        }

        settingsButton.setOnClickListener {
            showSettings()
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    private fun openMinecraft() {
        val minecraftPackage = "com.mojang.minecraftpe"

        val intent = packageManager.getLaunchIntentForPackage(minecraftPackage)

        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Minecraft Bedrock не установлен",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showServers() {
        AlertDialog.Builder(this)
            .setTitle("Серверы NovaPvP")
            .setMessage(
                "NovaPvP Server\n\n" +
                "Статус: Онлайн\n" +
                "Игроков: 0\n\n" +
                "Сервер пока не настроен."
            )
            .setPositiveButton("ЗАКРЫТЬ", null)
            .show()
    }

    private fun showSettings() {
        AlertDialog.Builder(this)
            .setTitle("Настройки NovaPvP")
            .setItems(
                arrayOf(
                    "Графика",
                    "Звук",
                    "Управление",
                    "Язык"
                )
            ) { _, which ->
                val items = arrayOf(
                    "Графика",
                    "Звук",
                    "Управление",
                    "Язык"
                )

                Toast.makeText(
                    this,
                    "Выбрано: ${items[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("ЗАКРЫТЬ", null)
            .show()
    }
}
