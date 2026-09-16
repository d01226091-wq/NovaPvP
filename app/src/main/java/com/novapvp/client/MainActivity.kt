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

    // Главное меню
    private fun showMainMenu() {
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.playButton).setOnClickListener {
            showWorlds()
        }

        findViewById<Button>(R.id.serversButton).setOnClickListener {
            showServers()
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            showProfile()
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            showSettings()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finish()
        }
    }

    // Миры
    private fun showWorlds() {
        setContentView(R.layout.worlds_screen)

        findViewById<Button>(R.id.createWorldButton).setOnClickListener {
            Toast.makeText(
                this,
                "Создание мира",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.importWorldButton).setOnClickListener {
            Toast.makeText(
                this,
                "Импорт мира",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.worldPlayButton).setOnClickListener {
            Toast.makeText(
                this,
                "Запуск мира NovaPvP",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.worldBackButton).setOnClickListener {
            showMainMenu()
        }
    }

    // Серверы
    private fun showServers() {
        setContentView(R.layout.servers_screen)

        findViewById<Button>(R.id.addServerButton).setOnClickListener {
            Toast.makeText(
                this,
                "Добавление сервера",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.refreshServersButton).setOnClickListener {
            Toast.makeText(
                this,
                "Список серверов обновлён",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.serverConnectButton).setOnClickListener {
            Toast.makeText(
                this,
                "Подключение к NovaPvP Network",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.serverBackButton).setOnClickListener {
            showMainMenu()
        }
    }

    // Профиль
    private fun showProfile() {
        setContentView(R.layout.profile_screen)

        findViewById<Button>(R.id.profileBackButton).setOnClickListener {
            showMainMenu()
        }

        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            Toast.makeText(
                this,
                "Редактирование профиля",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Настройки
    private fun showSettings() {
        setContentView(R.layout.settings_screen)

        findViewById<Button>(R.id.videoSettingsButton).setOnClickListener {
            showSettingMessage("Видео")
        }

        findViewById<Button>(R.id.audioSettingsButton).setOnClickListener {
            showSettingMessage("Аудио")
        }

        findViewById<Button>(R.id.controlsSettingsButton).setOnClickListener {
            showSettingMessage("Управление")
        }

        findViewById<Button>(R.id.interfaceSettingsButton).setOnClickListener {
            showSettingMessage("Интерфейс")
        }

        findViewById<Button>(R.id.languageSettingsButton).setOnClickListener {
            showLanguageMenu()
        }

        findViewById<Button>(R.id.settingsBackButton).setOnClickListener {
            showMainMenu()
        }
    }

    private fun showSettingMessage(name: String) {
        AlertDialog.Builder(this)
            .setTitle(name)
            .setMessage("$name NovaPvP\n\nНастройки этого раздела будут добавлены позже.")
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun showLanguageMenu() {
        val languages = arrayOf(
            "Русский",
            "English",
            "Deutsch"
        )

        AlertDialog.Builder(this)
            .setTitle("Язык")
            .setItems(languages) { _, which ->
                Toast.makeText(
                    this,
                    "Выбран: ${languages[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("НАЗАД", null)
            .show()
    }

    override fun onBackPressed() {
        showMainMenu()
    }
}
