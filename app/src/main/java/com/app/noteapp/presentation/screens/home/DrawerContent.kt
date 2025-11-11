package com.app.noteapp.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.noteapp.presentation.model.AvatarType
import com.app.noteapp.presentation.model.iconRes

@Composable
fun DrawerContent(
    currentAvatar: AvatarType,
    onAvatarSelected: (AvatarType) -> Unit,
) {
    ModalDrawerSheet {
        AvatarPickerSection(
            selected = currentAvatar, onSelect = onAvatarSelected
        )
    }
}

@Composable
fun AvatarPickerSection(
    selected: AvatarType,
    onSelect: (AvatarType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Choose Avatar Image", style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarOption(
                type = AvatarType.FEMALE,
                selected = selected == AvatarType.FEMALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
            AvatarOption(
                type = AvatarType.MALE,
                selected = selected == AvatarType.MALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AvatarRadio(
                label = "Female",
                checked = selected == AvatarType.FEMALE,
                onChecked = { onSelect(AvatarType.FEMALE) },
                modifier = Modifier.weight(1f)
            )
            AvatarRadio(
                label = "Male",
                checked = selected == AvatarType.MALE,
                onChecked = { onSelect(AvatarType.MALE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AvatarOption(
    modifier: Modifier, type: AvatarType, selected: Boolean, onSelect: (AvatarType) -> Unit
) {
    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val borderWidth = if (selected) 2.dp else 1.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onSelect(type) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(type.iconRes()),
            contentDescription = type.name,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun AvatarRadio(
    modifier: Modifier, label: String, checked: Boolean, onChecked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onChecked() }) {
        RadioButton(
            selected = checked, onClick = onChecked
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
