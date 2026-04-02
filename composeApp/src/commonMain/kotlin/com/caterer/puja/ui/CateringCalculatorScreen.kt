package com.caterer.puja.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caterer.puja.viewmodel.MainUiState

@Composable
fun CateringCalculatorScreen(
    state: MainUiState,
    onPeopleInputChange: (String) -> Unit,
    onToggleDish: (String) -> Unit,
    onCalculate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "Catering Calculator",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.peopleInput,
            onValueChange = onPeopleInputChange,
            label = { Text("Number of people") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select dishes",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        state.dishes.forEach { dish ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = dish.name, fontSize = 18.sp)
                Checkbox(
                    checked = dish.id in state.selectedDishIds,
                    onCheckedChange = { onToggleDish(dish.id) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCalculate,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Calculate")
        }

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 16.sp,
            )
        }

        if (state.results.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Ingredient List",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            state.results.forEach { item ->
                Text(
                    text = "- ${item.name}: ${item.quantityText}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

