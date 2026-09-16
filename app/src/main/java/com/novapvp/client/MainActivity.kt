
package com.novapvp.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showMainMenu()
    }

    private fun showMainMenu() {

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.serversButton).setOnClickListener {
            showServerDialog()
        }

        findViewById<Button>(R.id.playButton).setOnClickListener {
            Toast.makeText(
                this,
                "Игровой режим NovaPvP пока разрабатывается",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            Toast.makeText(
                this,
                "Настройки NovaPvP",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showServerDialog() {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 10, 40, 10)

        val hostInput = EditText(this)

        hostInput.hint = "IP сервера"
        hostInput.singleLine = true

        val portInput = EditText(this)

        portInput.hint = "Порт"
        portInput.setText("19132")
        portInput.inputType = 2
        portInput.singleLine = true

        val status = TextView(this)

        status.text = "Введите адрес Bedrock-сервера"
        status.setPadding(0, 25, 0, 10)

        layout.addView(hostInput)
        layout.addView(portInput)
        layout.addView(status)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Сервер Bedrock")
            .setView(layout)
            .setNegativeButton("НАЗАД", null)
            .setPositiveButton("ПРОВЕРИТЬ", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val host = hostInput.text.toString().trim()

                val port = portInput.text
                    .toString()
                    .toIntOrNull()

                if (host.isEmpty()) {

                    status.text = "Введите IP сервера"
                    return@setOnClickListener
                }

                if (port == null || port !in 1..65535) {

                    status.text = "Неверный порт"
                    return@setOnClickListener
                }

                status.text = "Проверяем соединение..."

                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                ).isEnabled = false

                CoroutineScope(
                    Dispatchers.Main
                ).launch {

                    val result = checkBedrockServer(
                        host,
                        port
                    )

                    status.text = result

                    dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).isEnabled = true
                }
            }
        }

        dialog.show()
    }

    private suspend fun checkBedrockServer(
        host: String,
        port: Int
    ): String {

        return kotlinx.coroutines.withContext(
            Dispatchers.IO
        ) {

            try {

                val address =
                    java.net.InetAddress.getByName(host)

                java.net.DatagramSocket().use { socket ->

                    socket.soTimeout = 3000

                    val data = byteArrayOf(
                        0x01,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        0x00
                    )

                    val packet =
                        java.net.DatagramPacket(
                            data,
                            data.size,
                            address,
                            port
                        )

                    socket.send(packet)

                    val responseData =
                        ByteArray(2048)

                    val response =
                        java.net.DatagramPacket(
                            responseData,
                            responseData.size
                        )

                    socket.receive(response)

                    "Сервер отвечает!\n" +
                            "${address.hostAddress}:$port"
                }

            } catch (e: java.net.SocketTimeoutException) {

                "Сервер не ответил за 3 секунды"

            } catch (e: Exception) {

                "Ошибка: ${e.message ?: "неизвестная ошибка"}"
            }
        }
    }
}
