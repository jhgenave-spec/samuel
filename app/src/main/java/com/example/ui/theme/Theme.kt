package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = StaticPrimaryPurple,
    secondary = StaticSecondaryLavender,
    tertiary = WarningGold,
    background = StaticPolishBackground,
    surface = StaticPureWhite,
    onPrimary = StaticPureWhite,
    onSecondary = StaticHighlightDarkPurple,
    onTertiary = StaticCoreDarkText,
    onBackground = StaticCoreDarkText,
    onSurface = StaticCoreDarkText,
    outline = StaticThinBorderColor,
    surfaceVariant = StaticSoftCardBg,
    onSurfaceVariant = StaticMutedText
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFF4F378B),
    tertiary = WarningGold,
    background = Color(0xFF121212),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onSecondary = Color(0xFFEADDFF),
    onTertiary = Color(0xFFE6E1E5),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    outline = Color(0xFF49454F),
    surfaceVariant = Color(0xFF25232A),
    onSurfaceVariant = Color(0xFFCAB4D0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded colors consistent for automotive styling
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        AppColorPalette(
            primaryPurple = Color(0xFFD0BCFF),
            secondaryLavender = Color(0xFF381E72),
            highlightDarkPurple = Color(0xFFEADDFF),
            polishBackground = Color(0xFF121212),
            pureWhite = Color(0xFF1C1B1F),
            coreDarkText = Color(0xFFE6E1E5),
            mutedText = Color(0xFFCAB4D0),
            thinBorderColor = Color(0xFF49454F),
            inputBorderColor = Color(0xFF938F99),
            softCardBg = Color(0xFF25232A)
        )
    } else {
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

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

