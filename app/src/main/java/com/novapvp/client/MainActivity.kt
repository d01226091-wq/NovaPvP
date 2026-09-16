package com.novapvp.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val prefsName = "novapvp_worlds"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainMenu()
    }

    private fun showMainMenu() {
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.playButton).setOnClickListener {
            showWorlds()
        }

        findViewById<Button>(R.id.serversButton).setOnClickListener {
            Toast.makeText(this, "Серверы", Toast.LENGTH_SHORT).show()
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

    private fun showWorlds() {
        setContentView(R.layout.worlds_screen)

        updateWorldList()

        findViewById<Button>(R.id.backButton).setOnClickListener {
            showMainMenu()
        }

        findViewById<Button>(R.id.createWorldButton).setOnClickListener {
            createWorldDialog()
        }
    }

    private fun updateWorldList() {
        val container = findViewById<LinearLayout>(R.id.worldList)
        container.removeAllViews()

        val worlds = getWorlds()

        if (worlds.isEmpty()) {
            val emptyText = TextView(this)

            emptyText.text = "Миров пока нет\nСоздай свой первый мир!"
            emptyText.textSize = 18f
            emptyText.setTextColor(0xFFFFFFFF.toInt())
            emptyText.setPadding(20, 40, 20, 40)

            container.addView(emptyText)
            return
        }

        for (world in worlds) {
            val row = LinearLayout(this)

            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(10, 10, 10, 10)

            val playButton = Button(this)

            playButton.text = "$world   ▶"
            playButton.setOnClickListener {
                openWorld(world)
            }

            val deleteButton = Button(this)

            deleteButton.text = "Удалить"

            deleteButton.setOnClickListener {
                deleteWorld(world)
            }

            row.addView(
                playButton,
                LinearLayout.LayoutParams(
                    0,
                    70,
                    1f
                )
            )

            row.addView(
                deleteButton,
                LinearLayout.LayoutParams(
                    160,
                    70
                )
            )

            container.addView(row)
        }
    }

    private fun createWorldDialog() {
        val input = EditText(this)

        input.hint = "Название мира"

        AlertDialog.Builder(this)
            .setTitle("Создать мир")
            .setView(input)
            .setPositiveButton("СОЗДАТЬ") { _, _ ->

                val name = input.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Введите название мира",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val worlds = getWorlds().toMutableList()

                if (worlds.contains(name)) {
                    Toast.makeText(
                        this,
                        "Такой мир уже существует",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                worlds.add(name)
                saveWorlds(worlds)

                Toast.makeText(
                    this,
                    "Мир \"$name\" создан",
                    Toast.LENGTH_SHORT
                ).show()

                showWorlds()
            }
            .setNegativeButton("ОТМЕНА", null)
            .show()
    }

    private fun openWorld(name: String) {
        setContentView(R.layout.game_screen)

        findViewById<TextView>(R.id.worldNameText).text = name

        findViewById<Button>(R.id.backButton).setOnClickListener {
            showWorlds()
        }

        Toast.makeText(
            this,
            "Мир \"$name\" запущен",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteWorld(name: String) {
        AlertDialog.Builder(this)
            .setTitle("Удалить мир?")
            .setMessage("Удалить \"$name\"?")

            .setPositiveButton("УДАЛИТЬ") { _, _ ->

                val worlds = getWorlds().toMutableList()

                worlds.remove(name)

                saveWorlds(worlds)

                showWorlds()
            }

            .setNegativeButton("ОТМЕНА", null)
            .show()
    }

    private fun getWorlds(): List<String> {
        val prefs = getSharedPreferences(
            prefsName,
            MODE_PRIVATE
        )

        val data = prefs.getString(
            "world_list",
            ""
        ) ?: ""

        if (data.isEmpty()) {
            return emptyList()
        }

        return data
            .split("|")
            .filter {
                it.isNotBlank()
            }
    }

    private fun saveWorlds(worlds: List<String>) {
        getSharedPreferences(
            prefsName,
            MODE_PRIVATE
        )
            .edit()
            .putString(
                "world_list",
                worlds.joinToString("|")
            )
            .apply()
    }

    private fun showProfile() {
        AlertDialog.Builder(this)
            .setTitle("Профиль")
            .setMessage(
                "Игрок: NovaPlayer\n\n" +
                "Уровень: 1\n" +
                "NovaPvP 1.0"
            )
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun showSettings() {
        AlertDialog.Builder(this)
            .setTitle("Настройки")
            .setMessage(
                "Видео\n" +
                "Аудио\n" +
                "Управление\n" +
                "Интерфейс\n" +
                "Язык"
            )
            .setPositiveButton("ОК", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showMainMenu()
    }
}h
