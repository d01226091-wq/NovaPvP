# NovaPvP Android

Проект Android-приложения для NovaPvP Visual Client.

## Что это
Приложение-оболочка с меню NovaPvP. Оно не заменяет Minecraft и не является модифицированным APK Minecraft.

## Сборка
1. Установи Android Studio.
2. Открой папку `NovaPvP_Android_Project`.
3. Дождись синхронизации Gradle.
4. Выбери `Build → Build APK(s)`.
5. APK появится в `app/build/outputs/apk/debug/`.

## Важно
Для кнопки «Открыть ресурспак» положи файл:
`NovaPvP_Visual_Client_Bedrock.mcpack`
в `app/src/main/assets/` и добавь FileProvider в Manifest при необходимости.

Проект предназначен для собственного визуального клиента/лаунчера и не включает код Minecraft.
