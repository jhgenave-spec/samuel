package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

// Prospere Automotive Detailing Premium Theme - "Professional Polish" Palette
val StaticPrimaryPurple = Color(0xFF6750A4)
val StaticSecondaryLavender = Color(0xFFEADDFF)
val StaticHighlightDarkPurple = Color(0xFF21005D)
val StaticPolishBackground = Color(0xFFF7F2FA)
val StaticPureWhite = Color(0xFFFFFFFF)
val StaticCoreDarkText = Color(0xFF1C1B1F)
val StaticMutedText = Color(0xFF49454F)
val StaticThinBorderColor = Color(0xFFCAC4D0)
val StaticInputBorderColor = Color(0xFF79747E)
val StaticSoftCardBg = Color(0xFFFEF7FF)

val SuccessGreen = Color(0xFF136D2F)
val DangerRed = Color(0xFFB3261E)
val WarningGold = Color(0xFFFFB400)

data class AppColorPalette(
    val primaryPurple: Color,
    val secondaryLavender: Color,
    val highlightDarkPurple: Color,
    val polishBackground: Color,
    val pureWhite: Color,
    val coreDarkText: Color,
    val mutedText: Color,
    val thinBorderColor: Color,
    val inputBorderColor: Color,
    val softCardBg: Color,
    val successGreen: Color = SuccessGreen,
    val dangerRed: Color = DangerRed,
    val warningGold: Color = WarningGold
)

val LocalAppColors = staticCompositionLocalOf {
    AppColorPalette(
        primaryPurple = StaticPrimaryPurple,
        secondaryLavender = StaticSecondaryLavender,
        highlightDarkPurple = StaticHighlightDarkPurple,
        polishBackground = StaticPolishBackground,
        pureWhite = StaticPureWhite,
        coreDarkText = StaticCoreDarkText,
        mutedText = StaticMutedText,
        thinBorderColor = StaticThinBorderColor,
        inputBorderColor = StaticInputBorderColor,
        softCardBg = StaticSoftCardBg
    )
}

val PrimaryPurple: Color @Composable get() = LocalAppColors.current.primaryPurple
val SecondaryLavender: Color @Composable get() = LocalAppColors.current.secondaryLavender
val HighlightDarkPurple: Color @Composable get() = LocalAppColors.current.highlightDarkPurple
val PolishBackground: Color @Composable get() = LocalAppColors.current.polishBackground
val PureWhite: Color @Composable get() = LocalAppColors.current.pureWhite
val CoreDarkText: Color @Composable get() = LocalAppColors.current.coreDarkText
val MutedText: Color @Composable get() = LocalAppColors.current.mutedText
val ThinBorderColor: Color @Composable get() = LocalAppColors.current.thinBorderColor
val InputBorderColor: Color @Composable get() = LocalAppColors.current.inputBorderColor
val SoftCardBg: Color @Composable get() = LocalAppColors.current.softCardBg


