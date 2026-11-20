BodyCraft — мод для Minecraft 26.3 (Fabric)
===========================================

Это готовый проект с исходниками, обновлённый под реальную сборку Fabric 26.3:
- Minecraft: 26.3 (unobfuscated)
- Fabric Loom: 1.18.2 (net.fabricmc.fabric-loom)
- Fabric Loader: 0.19.5
- Fabric API: 0.166.2+26.3
- Java: 25

КАК ПОЛУЧИТЬ ГОТОВЫЙ .JAR ДЛЯ ПАПКИ MODS:

Способ 1 — На своём ПК (через IntelliJ IDEA или Gradle):
1. Установи JDK 25 (например, Eclipse Temurin 25).
2. Открой папку `bodycraft` в IntelliJ IDEA (или установи Gradle 9.6.0 и выполни `gradle build` в терминале внутри папки `bodycraft`).
3. Готовый файл `bodycraft-1.0.0.jar` появится в `bodycraft/build/libs/`.
4. Кинь `bodycraft-1.0.0.jar` и `fabric-api-0.166.2+26.3.jar` в `.minecraft/mods`.

Способ 2 — Без установки Java на ПК (через GitHub Actions):
1. Создай новый пустой репозиторий на GitHub и залей туда содержимое папки `bodycraft`.
2. Перейди во вкладку Actions -> "Build BodyCraft Mod (.jar)".
3. Скачай готовый .jar из раздела Artifacts.
