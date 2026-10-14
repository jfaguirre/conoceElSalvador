package com.conoceelsalvador.kids.data.model

import androidx.compose.ui.graphics.Color

enum class GeographicZone(val displayName: String) {
    OCCIDENTAL("Zona Occidental"),
    CENTRAL("Zona Central"),
    PARACENTRAL("Zona Paracentral"),
    ORIENTAL("Zona Oriental")
}

data class Department(
    val id: String,
    val name: String,
    val capital: String,
    val zone: GeographicZone,
    val funFact: String,
    val color: Color,
    // Coordenadas normalizadas (0.0 a 1.0) dentro del mapa de El Salvador
    val normX: Float,
    val normY: Float,
    // Dimensiones relativas para dibujar y colisión
    val normWidth: Float,
    val normHeight: Float
)

object ElSalvadorData {
    val departments: List<Department> = listOf(
        // ZONA OCCIDENTAL
        Department(
            id = "ahuachapan",
            name = "Ahuachapán",
            capital = "Ahuachapán",
            zone = GeographicZone.OCCIDENTAL,
            funFact = "¡Cuna de los Ausoles termales y el inicio de la colorida Ruta de las Flores!",
            color = Color(0xFF4CAF50), // Verde
            normX = 0.12f,
            normY = 0.44f,
            normWidth = 0.12f,
            normHeight = 0.26f
        ),
        Department(
            id = "santa_ana",
            name = "Santa Ana",
            capital = "Santa Ana",
            zone = GeographicZone.OCCIDENTAL,
            funFact = "¡Hogar del imponente Volcán Ilamatepec y el hermoso Lago de Coatepeque!",
            color = Color(0xFF2E7D32), // Verde oscuro
            normX = 0.24f,
            normY = 0.28f,
            normWidth = 0.16f,
            normHeight = 0.32f
        ),
        Department(
            id = "sonsonate",
            name = "Sonsonate",
            capital = "Sonsonate",
            zone = GeographicZone.OCCIDENTAL,
            funFact = "¡Tierra de cocoteros, ricas playas y el histórico Volcán de Izalco!",
            color = Color(0xFF8BC34A), // Verde lima
            normX = 0.20f,
            normY = 0.62f,
            normWidth = 0.14f,
            normHeight = 0.28f
        ),

        // ZONA CENTRAL & PARACENTRAL
        Department(
            id = "chalatenango",
            name = "Chalatenango",
            capital = "Chalatenango",
            zone = GeographicZone.CENTRAL,
            funFact = "¡Aquí está El Pital, el punto más alto y fresco de todo El Salvador!",
            color = Color(0xFF009688), // Verde azulado
            normX = 0.42f,
            normY = 0.20f,
            normWidth = 0.26f,
            normHeight = 0.26f
        ),
        Department(
            id = "la_libertad",
            name = "La Libertad",
            capital = "Santa Tecla",
            zone = GeographicZone.CENTRAL,
            funFact = "¡Famoso en todo el mundo por Surf City y sus playas de olas perfectas!",
            color = Color(0xFF00BCD4), // Celeste cyan
            normX = 0.34f,
            normY = 0.58f,
            normWidth = 0.15f,
            normHeight = 0.32f
        ),
        Department(
            id = "san_salvador",
            name = "San Salvador",
            capital = "San Salvador",
            zone = GeographicZone.CENTRAL,
            funFact = "¡Nuestra alegre capital y hogar del Monumento al Divino Salvador del Mundo!",
            color = Color(0xFF2196F3), // Azul primario
            normX = 0.42f,
            normY = 0.50f,
            normWidth = 0.10f,
            normHeight = 0.20f
        ),
        Department(
            id = "cuscatlan",
            name = "Cuscatlán",
            capital = "Cojutepeque",
            zone = GeographicZone.CENTRAL,
            funFact = "¡El departamento más chiquito, famoso por los deliciosos chorizos de Cojutepeque!",
            color = Color(0xFF3F51B5), // Índigo
            normX = 0.48f,
            normY = 0.46f,
            normWidth = 0.09f,
            normHeight = 0.20f
        ),
        Department(
            id = "la_paz",
            name = "La Paz",
            capital = "Zacatecoluca",
            zone = GeographicZone.PARACENTRAL,
            funFact = "¡Donde aterrizan los aviones en el Aeropuerto Internacional y ricas costas!",
            color = Color(0xFF03A9F4), // Azul cielo
            normX = 0.49f,
            normY = 0.68f,
            normWidth = 0.13f,
            normHeight = 0.28f
        ),
        Department(
            id = "cabanas",
            name = "Cabañas",
            capital = "Sensuntepeque",
            zone = GeographicZone.PARACENTRAL,
            funFact = "¡Famoso por el arte en barro de Ilobasco y el bello río Lempa!",
            color = Color(0xFF9C27B0), // Púrpura
            normX = 0.57f,
            normY = 0.38f,
            normWidth = 0.14f,
            normHeight = 0.22f
        ),
        Department(
            id = "san_vicente",
            name = "San Vicente",
            capital = "San Vicente",
            zone = GeographicZone.PARACENTRAL,
            funFact = "¡Tierra de la Torre de San Vicente, dulces típicos y el Volcán Chinchontepec!",
            color = Color(0xFF673AB7), // Violeta
            normX = 0.56f,
            normY = 0.58f,
            normWidth = 0.12f,
            normHeight = 0.26f
        ),

        // ZONA ORIENTAL
        Department(
            id = "usulutan",
            name = "Usulután",
            capital = "Usulután",
            zone = GeographicZone.ORIENTAL,
            funFact = "¡El gran granero de El Salvador y hogar de la paradisíaca Bahía de Jiquilisco!",
            color = Color(0xFFFF9800), // Naranja
            normX = 0.67f,
            normY = 0.72f,
            normWidth = 0.15f,
            normHeight = 0.26f
        ),
        Department(
            id = "san_miguel",
            name = "San Miguel",
            capital = "San Miguel",
            zone = GeographicZone.ORIENTAL,
            funFact = "¡La Perla de Oriente, famosa por el gigantesco y alegre Carnaval de San Miguel!",
            color = Color(0xFFFF5722), // Naranja rojizo
            normX = 0.72f,
            normY = 0.54f,
            normWidth = 0.14f,
            normHeight = 0.38f
        ),
        Department(
            id = "morazan",
            name = "Morazán",
            capital = "San Francisco Gotera",
            zone = GeographicZone.ORIENTAL,
            funFact = "¡Famoso por sus montañas de pinos, cascadas de agua pura y la Ruta de Paz!",
            color = Color(0xFF795548), // Marrón montaña
            normX = 0.78f,
            normY = 0.34f,
            normWidth = 0.14f,
            normHeight = 0.28f
        ),
        Department(
            id = "la_union",
            name = "La Unión",
            capital = "La Unión",
            zone = GeographicZone.ORIENTAL,
            funFact = "¡Guardián del majestuoso Golfo de Fonseca y sus bellas islas!",
            color = Color(0xFFE91E63), // Rosa vibrante
            normX = 0.86f,
            normY = 0.58f,
            normWidth = 0.13f,
            normHeight = 0.36f
        )
    )
}
