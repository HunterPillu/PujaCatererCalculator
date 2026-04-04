package com.caterer.puja.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caterer.puja.data.model.Dish
import com.caterer.puja.domain.model.CalculatedIngredient
import com.caterer.puja.viewmodel.MainUiState

@Composable
fun CateringCalculatorScreen(
    state: MainUiState,
    onPeopleInputChange: (String) -> Unit,
    onToggleDish: (Int) -> Unit,
    onCalculate: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResultScreen by remember { mutableStateOf(false) }
    val filteredDishes = state.dishes.filter {
        it.name.contains(searchQuery.trim(), ignoreCase = true)
    }
    val selectedDishes = state.dishes.filter { it.id in state.selectedDishIds }
    val dishesByCategory = filteredDishes.groupBy { it.category }//.toSortedMap()
    val expandedByCategory = remember { mutableStateMapOf<String, Boolean>() }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Calculation") },
            text = {
                Column {
                    Text("People: ${state.peopleInput.ifBlank { "-" }}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Selected dishes (${selectedDishes.size}):")
                    Spacer(modifier = Modifier.height(4.dp))
                    selectedDishes.sortedBy { it.name }.forEach { dish ->
                        Text("- ${dish.name}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onCalculate()
                        if (state.peopleInput.toIntOrNull()?.let { it > 0 } == true && selectedDishes.isNotEmpty()) {
                            showResultScreen = true
                        }
                    },
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (!showResultScreen) {
                Surface(
                    shadowElevation = 4.dp,
                    modifier = Modifier.navigationBarsPadding(),
                ) {
                    Button(
                        onClick = {
                            if (state.peopleInput.toIntOrNull()?.let { it > 0 } == true && selectedDishes.isNotEmpty()) {
                                showConfirmDialog = true
                            } else {
                                onCalculate()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text("Calculate")
                    }
                }
            }
        },
    ) { contentPadding ->
        Crossfade(
            targetState = showResultScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) { isResultVisible ->
            if (isResultVisible) {
                ResultScreen(
                    results = state.results,
                    onBackToEdit = { showResultScreen = false },
                )
            } else {
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

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search dish") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selected dishes: ${selectedDishes.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    AnimatedVisibility(
                        visible = selectedDishes.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        ) {
                            selectedDishes.sortedBy { it.name }.forEach { dish ->
                                key(dish.id) {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier
                                                .padding(end = 8.dp, bottom = 4.dp)
                                                .wrapContentWidth(),
                                        ) {
                                            Text(
                                                text = dish.name,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontSize = 14.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Select dishes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredDishes.isEmpty()) {
                        Text(
                            text = "No dish found",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    dishesByCategory.forEach { (category, dishesInCategory) ->
                        val isExpanded = expandedByCategory.getOrPut(category) { true }
                        val chevronRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth()
                                .animateContentSize(),
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedByCategory[category] = !isExpanded },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = if (isExpanded) "v" else ">",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = (chevronRotation / 90f).dp),
                                    )
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut(),
                                ) {
                                    Column {
                                        dishesInCategory.forEach { dish ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = dish.name,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.weight(1f),
                                                )
                                                Checkbox(
                                                    checked = dish.id in state.selectedDishIds,
                                                    onCheckedChange = { onToggleDish(dish.id) },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    state.errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun ResultScreen(
    results: List<CalculatedIngredient>,
    onBackToEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ingredient List",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Button(onClick = onBackToEdit) {
                Text("Back to Edit")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (results.isEmpty()) {
            Text(
                text = "No ingredients calculated yet.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            results.forEach { item ->
                Text(
                    text = "- ${item.name}: ${item.quantityText}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CateringCalculatorScreenPreviewEmpty() {
    MaterialTheme {
        CateringCalculatorScreen(
            state = MainUiState(
                dishes = listOf(
                    Dish(id = 240, name = "Chicken Curry", category = "Chicken - Gravy"),
                    Dish(id = 270, name = "Chicken Biryani", category = "Non Veg Biryani"),
                    Dish(id = 105, name = "Daal Makhani", category = "Daal"),
                ),
            ),
            onPeopleInputChange = {},
            onToggleDish = {},
            onCalculate = {},
        )
    }
}

@Preview
@Composable
private fun CateringCalculatorScreenPreviewWithSelection() {
    MaterialTheme {
        CateringCalculatorScreen(
            state = MainUiState(
                peopleInput = "100",
                dishes = listOf(
                    Dish(id = 240, name = "Chicken Curry", category = "Chicken - Gravy"),
                    Dish(id = 270, name = "Chicken Biryani", category = "Non Veg Biryani"),
                    Dish(id = 252, name = "Mutton Curry", category = "Mutton"),
                    Dish(id = 105, name = "Daal Makhani", category = "Daal"),
                    Dish(id = 114, name = "Jeera Rice", category = "Rice"),
                    Dish(id = 304, name = "Roti", category = "Bread"),
                ),
                selectedDishIds = setOf(240, 114, 304),
                results = listOf(
                    CalculatedIngredient(name = "Chicken", quantityText = "25 kg"),
                    CalculatedIngredient(name = "Rice", quantityText = "9 kg"),
                    CalculatedIngredient(name = "Wheat Flour", quantityText = "8 kg"),
                ),
            ),
            onPeopleInputChange = {},
            onToggleDish = {},
            onCalculate = {},
        )
    }
}

@Preview
@Composable
private fun ResultScreenPreviewEmpty() {
    MaterialTheme {
        ResultScreen(
            results = emptyList(),
            onBackToEdit = {},
        )
    }
}

@Preview
@Composable
private fun ResultScreenPreviewWithData() {
    MaterialTheme {
        ResultScreen(
            results = listOf(
                CalculatedIngredient(name = "Chicken", quantityText = "25 kg"),
                CalculatedIngredient(name = "Rice", quantityText = "9 kg"),
                CalculatedIngredient(name = "Oil", quantityText = "1.8 liter"),
                CalculatedIngredient(name = "Wheat Flour", quantityText = "8 kg"),
            ),
            onBackToEdit = {},
        )
    }
}

