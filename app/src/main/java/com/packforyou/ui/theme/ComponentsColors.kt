package com.packforyou.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun CustomTextFieldColors(): TextFieldColors =
    TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        disabledTextColor = MaterialTheme.colorScheme.onBackground,
        cursorColor = MaterialTheme.colorScheme.onBackground,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
        disabledBorderColor = MaterialTheme.colorScheme.onBackground,
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        disabledLabelColor = MaterialTheme.colorScheme.onBackground,
        errorLabelColor = MaterialTheme.colorScheme.error,
        placeholderColor = MaterialTheme.colorScheme.onBackground,
        disabledPlaceholderColor = MaterialTheme.colorScheme.primary,
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomExposedDropdownMenu(): TextFieldColors =
    ExposedDropdownMenuDefaults.textFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.background

    )
