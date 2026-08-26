package com.example.ui.console

import androidx.compose.ui.graphics.Color
import com.example.game.TetrominoType

data class ConsoleSkin(
    val id: Int,
    val name: String,
    val bodyColor: Color,
    val bodyAccentColor: Color,
    val bezelColor: Color,
    val lcdBackground: Color,
    val activePixelColor: Color,
    val inactivePixelColor: Color,
    val dpadColor: Color,
    val actionButtonColorA: Color,
    val actionButtonColorB: Color,
    val actionButtonTextColor: Color,
    val systemButtonColor: Color,
    val brandTextColor: Color,
    val screenBorderColor: Color,
    val isColorBlock: Boolean = false,
    val actionButtonColorHold: Color = systemButtonColor,
    val actionButtonColorDrop: Color = actionButtonColorB,
    val actionButtonColorRotateLeft: Color = actionButtonColorB,
    val actionButtonColorRotateRight: Color = actionButtonColorA
) {
    fun getBlockColor(typeId: Int): Color {
        if (!isColorBlock || typeId == 0) return activePixelColor
        return when (TetrominoType.fromId(typeId)) {
            TetrominoType.I -> Color(0xFF00E5FF) // Cyan
            TetrominoType.J -> Color(0xFF2979FF) // Blue
            TetrominoType.L -> Color(0xFFFF9100) // Orange
            TetrominoType.O -> Color(0xFFFFEA00) // Yellow
            TetrominoType.S -> Color(0xFF00E676) // Green
            TetrominoType.T -> Color(0xFFD500F9) // Purple
            TetrominoType.Z -> Color(0xFFFF1744) // Red
            null -> activePixelColor
        }
    }

    companion object {
        val RETRO_YELLOW = ConsoleSkin(
            id = 0,
            name = "Retro Brick (Yellow)",
            bodyColor = Color(0xFFE5B014),
            bodyAccentColor = Color(0xFFC7970C),
            bezelColor = Color(0xFF2B2B33),
            lcdBackground = Color(0xFF9BAC97),
            activePixelColor = Color(0xFF0F1A12),
            inactivePixelColor = Color(0xFF8B9C87),
            dpadColor = Color(0xFF38373C),
            actionButtonColorA = Color(0xFFBD2626),
            actionButtonColorB = Color(0xFFBD2626),
            actionButtonTextColor = Color.White,
            systemButtonColor = Color(0xFF4A4950),
            brandTextColor = Color(0xFF1E1D24),
            screenBorderColor = Color(0xFF18171E),
            isColorBlock = false,
            actionButtonColorHold = Color(0xFF1E88E5),      // Vibrant Blue
            actionButtonColorDrop = Color(0xFFF57C00),      // Vibrant Amber
            actionButtonColorRotateLeft = Color(0xFF388E3C), // Vibrant Green
            actionButtonColorRotateRight = Color(0xFFD32F2F) // Classic Red
        )

        val CLASSIC_GAMEBOY = ConsoleSkin(
            id = 1,
            name = "Classic Gameboy (1989)",
            bodyColor = Color(0xFFCDCCCA),
            bodyAccentColor = Color(0xFFB5B4B2),
            bezelColor = Color(0xFF353457),
            lcdBackground = Color(0xFF8B956D),
            activePixelColor = Color(0xFF1F240A),
            inactivePixelColor = Color(0xFF7E8862),
            dpadColor = Color(0xFF303036),
            actionButtonColorA = Color(0xFF8F1D52),
            actionButtonColorB = Color(0xFF8F1D52),
            actionButtonTextColor = Color.White,
            systemButtonColor = Color(0xFF7A7982),
            brandTextColor = Color(0xFF181640),
            screenBorderColor = Color(0xFF22213A),
            isColorBlock = false,
            actionButtonColorHold = Color(0xFF00796B),      // Teal
            actionButtonColorDrop = Color(0xFFD81B60),      // Deep Pink
            actionButtonColorRotateLeft = Color(0xFF512DA8), // Deep Purple
            actionButtonColorRotateRight = Color(0xFF8F1D52) // Classic Magenta
        )

        val CYBER_NEON = ConsoleSkin(
            id = 2,
            name = "Cyber Arcade Neon",
            bodyColor = Color(0xFF12131C),
            bodyAccentColor = Color(0xFF1D1F2E),
            bezelColor = Color(0xFF1A1B2A),
            lcdBackground = Color(0xFF0A0C14),
            activePixelColor = Color(0xFF00F0FF),
            inactivePixelColor = Color(0xFF151928),
            dpadColor = Color(0xFF25273A),
            actionButtonColorA = Color(0xFFFF007F),
            actionButtonColorB = Color(0xFF00E5FF),
            actionButtonTextColor = Color.White,
            systemButtonColor = Color(0xFF3A3D59),
            brandTextColor = Color(0xFF00F0FF),
            screenBorderColor = Color(0xFF00F0FF),
            isColorBlock = true,
            actionButtonColorHold = Color(0xFFFFEA00),      // Neon Yellow
            actionButtonColorDrop = Color(0xFF00E676),      // Neon Green
            actionButtonColorRotateLeft = Color(0xFF00E5FF), // Neon Cyan
            actionButtonColorRotateRight = Color(0xFFFF007F) // Neon Pink
        )

        val ATOMIC_PURPLE = ConsoleSkin(
            id = 3,
            name = "90s Atomic Purple",
            bodyColor = Color(0xFF4A3A68),
            bodyAccentColor = Color(0xFF3B2D54),
            bezelColor = Color(0xFF261A3A),
            lcdBackground = Color(0xFFA2B59D),
            activePixelColor = Color(0xFF141810),
            inactivePixelColor = Color(0xFF91A48C),
            dpadColor = Color(0xFF201730),
            actionButtonColorA = Color(0xFFE5B014),
            actionButtonColorB = Color(0xFFFF5252),
            actionButtonTextColor = Color.Black,
            systemButtonColor = Color(0xFF5E4B82),
            brandTextColor = Color(0xFFD4C5F0),
            screenBorderColor = Color(0xFF1A1229),
            isColorBlock = false,
            actionButtonColorHold = Color(0xFF00BCD4),      // Electric Cyan
            actionButtonColorDrop = Color(0xFFFF5252),      // Coral Red
            actionButtonColorRotateLeft = Color(0xFF7C4DFF), // Violet
            actionButtonColorRotateRight = Color(0xFFE5B014) // Yellow Gold
        )

        val MINIMAL_DARK = ConsoleSkin(
            id = 4,
            name = "Stealth Modern OLED",
            bodyColor = Color(0xFF1E1E24),
            bodyAccentColor = Color(0xFF292930),
            bezelColor = Color(0xFF121216),
            lcdBackground = Color(0xFF0E0F12),
            activePixelColor = Color(0xFFE2E2E8),
            inactivePixelColor = Color(0xFF1E2028),
            dpadColor = Color(0xFF2C2D36),
            actionButtonColorA = Color(0xFF43A047),
            actionButtonColorB = Color(0xFF1E88E5),
            actionButtonTextColor = Color.White,
            systemButtonColor = Color(0xFF42434E),
            brandTextColor = Color(0xFF9E9EA8),
            screenBorderColor = Color(0xFF2B2C36),
            isColorBlock = true,
            actionButtonColorHold = Color(0xFFAB47BC),      // Purple
            actionButtonColorDrop = Color(0xFFFF7043),      // Orange
            actionButtonColorRotateLeft = Color(0xFF1E88E5), // Blue
            actionButtonColorRotateRight = Color(0xFF43A047) // Green
        )

        val ALL_SKINS = listOf(
            RETRO_YELLOW,
            CLASSIC_GAMEBOY,
            CYBER_NEON,
            ATOMIC_PURPLE,
            MINIMAL_DARK
        )

        fun getById(id: Int): ConsoleSkin {
            return ALL_SKINS.firstOrNull { it.id == id } ?: RETRO_YELLOW
        }
    }
}
