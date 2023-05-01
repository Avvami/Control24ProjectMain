package com.example.control24projectmain

object NumberDefinitions {
    val list = listOf(
        NumberDefinition(1, "Дом"),
        NumberDefinition(2, "Машина"),
        NumberDefinition(3, "Грузовой"),
        NumberDefinition(4, "Особый"),
        NumberDefinition(5, "VIP"),
        NumberDefinition(101, "Легковой"),
        NumberDefinition(102, "Миксер"),
        NumberDefinition(103, "Фронтальный погрузчик"),
        NumberDefinition(104, "Фургон"),
        NumberDefinition(105, "Автокран"),
        NumberDefinition(106, "ЭКГ"),
        NumberDefinition(107, "БелАЗ"),
        NumberDefinition(108, "Экскаватор"),
        NumberDefinition(109, "Погрузчик"),
        NumberDefinition(110, "Трактор"),
        NumberDefinition(111, "Трактор-экскаватор"),
        NumberDefinition(112, "Бензовоз"),
        NumberDefinition(113, "Трактор 2"),
        NumberDefinition(114, "Трактор-экскаватор 2"),
        NumberDefinition(115, "Автобус"),
        NumberDefinition(116, "Бульдозер")
    )
}

data class NumberDefinition(val number: Int, val definition: String)