package com.packforyou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.packforyou.R

val Poppins = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_regular, FontWeight.Light),
    Font(R.font.poppins_thin, FontWeight.Thin)
)

val Inter = FontFamily(
    Font(R.font.inter_medium, FontWeight.Medium)
)

val Roboto = FontFamily(
    Font(R.font.roboto_black),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

val Matroska = FontFamily(
    Font(R.font.matroska)
)

val Sailec = FontFamily(
    Font(R.font.sailec_regular),
    Font(R.font.sailec_medium, FontWeight.SemiBold),
    Font(R.font.sailec_bold, FontWeight.Bold)
)

val Grotesk = FontFamily(
    Font(R.font.grotesk_light, FontWeight.Light),
    Font(R.font.grotesk_regular),
    Font(R.font.grotesk_medium, FontWeight.Medium),
    Font(R.font.grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.grotesk_bold, FontWeight.Bold)
)

val PackForYouTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
)