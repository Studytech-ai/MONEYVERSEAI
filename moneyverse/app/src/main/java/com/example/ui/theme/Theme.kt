package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MoneyVerseColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = ChipBackground,
    onPrimaryContainer = PrimaryPurple,
    secondary = GoldCoin,
    onSecondary = DeepPurple,
    secondaryContainer = GoldCoinLight,
    onSecondaryContainer = GoldCoinDark,
    tertiary = MintGreen,
    onTertiary = Color.White,
    tertiaryContainer = MintGreenLight,
    onTertiaryContainer = MintGreenDark,
    background = LavenderBackground,
    onBackground = TextDark,
    surface = CardBackgroundWhite,
    onSurface = TextDark,
    surfaceVariant = LavenderBackground,
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    error = CoralRed,
    errorContainer = CoralRedLight,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MoneyVerseColorScheme,
        typography = AppTypography,
        content = content
    )
}
