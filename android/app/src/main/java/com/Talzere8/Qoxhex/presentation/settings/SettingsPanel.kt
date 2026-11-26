package com.Talzere8.Qoxhex.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.core.theme.BackgroundRaised
import com.Talzere8.Qoxhex.core.theme.CardWhite
import com.Talzere8.Qoxhex.core.theme.Cobalt
import com.Talzere8.Qoxhex.core.theme.Graphite
import com.Talzere8.Qoxhex.core.theme.Orange
import com.Talzere8.Qoxhex.core.theme.SuccessGreen
import com.Talzere8.Qoxhex.core.theme.SurfaceDeep
import com.Talzere8.Qoxhex.core.theme.SurfaceSoft
import com.Talzere8.Qoxhex.core.theme.TextSecondary
import com.Talzere8.Qoxhex.core.ui.PrimaryButton
import com.Talzere8.Qoxhex.core.ui.ScreenHeader
import com.Talzere8.Qoxhex.core.ui.SecondaryButton
import com.Talzere8.Qoxhex.core.ui.TrailBackground
import com.Talzere8.Qoxhex.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsPanel(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }

    if (state.confirmVisible) {
        ConfirmResetDialog(
            onConfirm = { settingsViewModel.confirmReset() },
            onCancel = { settingsViewModel.cancelReset() }
        )
    }

    TrailBackground(
        overlay = listOf(
            BackgroundBase.copy(alpha = 0.93f),
            BackgroundRaised.copy(alpha = 0.95f),
            SurfaceSoft.copy(alpha = 0.96f)
        ),
        imageAlpha = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ScreenHeader(
                title = stringResource(R.string.settings_title),
                subtitle = stringResource(R.string.settings_subtitle)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingRow(
                    title = stringResource(R.string.opt_relaxed),
                    note = stringResource(R.string.opt_relaxed_note),
                    checked = state.relaxedTimer,
                    onChange = { value -> settingsViewModel.setRelaxedTimer(value) }
                )
                SettingRow(
                    title = stringResource(R.string.opt_colour_codes),
                    note = stringResource(R.string.opt_colour_codes_note),
                    checked = state.colourCodes,
                    onChange = { value -> settingsViewModel.setColourCodes(value) }
                )
                SettingRow(
                    title = stringResource(R.string.opt_route_hints),
                    note = stringResource(R.string.opt_route_hints_note),
                    checked = state.routeHints,
                    onChange = { value -> settingsViewModel.setRouteHints(value) }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            SecondaryButton(
                text = stringResource(R.string.reset_progress),
                onClick = { settingsViewModel.askReset() },
                leading = painterResource(R.drawable.ic_settings),
                accent = Orange,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.resetDone) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.progress_reset),
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                height = 60.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    note: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val onText = stringResource(R.string.state_on)
    val offText = stringResource(R.string.state_off)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .semantics { stateDescription = if (checked) onText else offText }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Graphite
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CardWhite,
                    checkedTrackColor = Cobalt,
                    uncheckedThumbColor = CardWhite,
                    uncheckedTrackColor = SurfaceDeep
                )
            )
        }
    }
}
