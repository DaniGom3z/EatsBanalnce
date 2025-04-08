package com.dani.eatsbalance.ui.screen

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import necessary filled icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dani.eatsbalance.model.data.MealRequest
import com.dani.eatsbalance.viewmodel.MealsViewModel
import java.text.SimpleDateFormat // For date formatting
import java.util.* // For Date and Locale
import androidx.compose.ui.platform.LocalContext

// Helper function to format timestamp
fun formatMillisToDateTime(millis: Long): String {
    val date = Date(millis)
    // Example format: "MMM dd, yyyy HH:mm" (e.g., "Jul 26, 2023 15:30")
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Scaffold and TopAppBar
@Composable
fun DashboardScreen(
    navController: NavController,
    mealViewModel: MealsViewModel
) {
    PermissionRequester()
    var name by rememberSaveable { mutableStateOf("") }
    var calories by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var nutritionRating by rememberSaveable { mutableStateOf(0f) }

    val mealList by mealViewModel.meals.collectAsState()
    val totalCalories = mealList.sumOf { it.calories }

    LaunchedEffect(Unit) {
        mealViewModel.fetchMeals()
    }

    // Use Scaffold for standard layout structure (TopAppBar, content area)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EatsBalance Dashboard") }, // App/Screen Title
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Use theme background
    ) { paddingValues -> // Content padding provided by Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp) // Add horizontal padding for content
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // --- Add Meal Form Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Add subtle shadow
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Log New Meal", // Clearer title
                        style = MaterialTheme.typography.titleLarge, // Larger title for section
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Increased spacing

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = "" },
                        label = { Text("Meal Name") }, // Shorter labels
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        leadingIcon = { Icon(Icons.Filled.RestaurantMenu, contentDescription = "Meal Name Icon") }, // Add leading icon
                        isError = errorMessage.isNotEmpty() && name.isEmpty() // Show error only when attempting submit
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Consistent spacing

                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it.filter { it.isDigit() }; errorMessage = "" }, // Filter non-digits
                        label = { Text("Estimated Calories (kcal)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        leadingIcon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Calories Icon") }, // Add leading icon
                        isError = errorMessage.isNotEmpty() && calories.isEmpty()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it; errorMessage = "" },
                        label = { Text("Description / Ingredients") },
                        singleLine = false,
                        maxLines = 4, // Slightly more space if needed
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        leadingIcon = { Icon(Icons.Filled.Notes, contentDescription = "Description Icon") }, // Add leading icon
                        isError = errorMessage.isNotEmpty() && description.isEmpty()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Nutrition Rating ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nutrition Rating: ${nutritionRating.toInt()} / 5 Stars",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = nutritionRating,
                            onValueChange = { nutritionRating = it },
                            valueRange = 0f..5f,
                            steps = 4, // 0, 1, 2, 3, 4, 5
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Media Input Section ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround, // Space out icons
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Placeholder for Image (Optional: Show actual image if implemented)
                        Icon(
                            imageVector = Icons.Filled.Image, // Generic image icon
                            contentDescription = "Meal Image Placeholder",
                            modifier = Modifier.size(60.dp), // Adjust size as needed
                            tint = MaterialTheme.colorScheme.outline
                        )

                        // Action Buttons with Icons
                        IconButton(onClick = {
                            Log.d("DashboardScreen", "Lanzar cámara para tomar foto")
                            /* TODO: Implement camera logic */
                        }) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = "Take Photo", modifier = Modifier.size(32.dp))
                        }
                        IconButton(onClick = {
                            Log.d("DashboardScreen", "Grabar audio para descripción")
                            /* TODO: Implement audio recording logic */
                        }) {
                            Icon(Icons.Filled.Mic, contentDescription = "Record Audio", modifier = Modifier.size(32.dp))
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp)) // Space before error/button

                    // --- Error Message ---
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium, // Slightly larger error text
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // --- Save Button ---
                    Button(
                        onClick = {
                            val caloriesInt = calories.toIntOrNull() // Safely convert to Int
                            if (name.isNotBlank() && caloriesInt != null && description.isNotBlank()) {
                                if (caloriesInt <= 0) {
                                    errorMessage = "Calories must be a positive number"
                                    return@Button
                                }
                                try {
                                    val meal = MealRequest(
                                        name = name.trim(), // Trim whitespace
                                        calories = caloriesInt,
                                        description = description.trim(),
                                        date = System.currentTimeMillis()
                                        // Consider adding nutritionRating to MealRequest if needed
                                    )
                                    mealViewModel.addMeal(meal)
                                    // Reset form
                                    name = ""
                                    calories = ""
                                    description = ""
                                    nutritionRating = 0f
                                    errorMessage = "" // Clear error on success
                                } catch (e: Exception) {
                                    errorMessage = "Error saving meal: ${e.message}"
                                    Log.e("DashboardScreen", errorMessage, e)
                                }
                            } else {
                                errorMessage = "Please fill in all required fields correctly."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        // Use default Button colors or customize if needed
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Save Icon", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Save Meal", color = MaterialTheme.colorScheme.onPrimary) // Use theme color for text on primary
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // More space before summary/list

            // --- Calorie Summary ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center // Center the summary text
                ) {
                    Icon(Icons.Filled.Summarize, contentDescription = "Summary Icon", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Total Today: $totalCalories kcal",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.titleMedium // Good prominence
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // --- Meal List Header ---
            Text(
                text = "Logged Meals",
                style = MaterialTheme.typography.headlineSmall, // Clear header for the list
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp)) // Space before the list items

            // --- Meal List ---
            if (mealList.isEmpty()) {
                Text(
                    text = "No meals logged yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                mealList.forEach { meal ->
                    MealItemCard(meal = meal, onDelete = { mealViewModel.deleteMeal(meal.id) })
                    Spacer(modifier = Modifier.height(12.dp)) // Space between meal cards
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Padding at the bottom
        }
    }
}

// Separate Composable for Meal Item Card for better organization
@Composable
fun MealItemCard(meal: MealRequest, onDelete: () -> Unit) { // Use the actual Meal model
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // Slightly rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Different background for list items
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp), // Adjust padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Meal Details Column
            Column(modifier = Modifier.weight(1f)) { // Takes up available space
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium, // More prominent name
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Row for Calories
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        contentDescription = "Calories",
                        tint = MaterialTheme.colorScheme.primary, // Use primary color for emphasis
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${meal.calories} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Row for Description (Optional: truncate if too long)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Notes,
                        contentDescription = "Description",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        meal.description,
                        style = MaterialTheme.typography.bodySmall, // Smaller for description
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2 // Limit lines if description can be long
                        // overflow = TextOverflow.Ellipsis // Add ellipsis if text overflows
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Row for Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp) // Slightly smaller icon
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formatMillisToDateTime(meal.date), // Use formatted date
                        style = MaterialTheme.typography.labelSmall, // Even smaller for metadata
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Delete Button Column
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.DeleteOutline, // Use outline variant for delete
                    contentDescription = "Delete Meal",
                    tint = MaterialTheme.colorScheme.error // Use error color for delete action
                )
            }
        }
    }
}

@Composable
fun PermissionRequester() {
    val context = LocalContext.current

    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionsMap.entries.forEach {
            Log.d("Permissions", "${it.key} = ${it.value}")
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions.toTypedArray())
    }
}
