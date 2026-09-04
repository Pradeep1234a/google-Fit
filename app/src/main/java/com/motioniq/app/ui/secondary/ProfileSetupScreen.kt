package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    initialProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialProfile.name.ifBlank { "Alex" }) }
    var age by remember { mutableStateOf(initialProfile.age.toString()) }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf(initialProfile.heightCm.toInt().toString()) }
    var weight by remember { mutableStateOf(initialProfile.weightKg.toInt().toString()) }
    var genderExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            if (onBackClick != null) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextHighLight
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = {
                        val updated = initialProfile.copy(
                            name = name,
                            age = age.toIntOrNull() ?: initialProfile.age,
                            heightCm = height.toDoubleOrNull() ?: initialProfile.heightCm,
                            weightKg = weight.toDoubleOrNull() ?: initialProfile.weightKg
                        )
                        onSaveProfile(updated)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header (04_ProfileSetup.png)
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tell Us About Yourself",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This helps us provide better insights\nand more accurate estimates.",
                        fontSize = 14.sp,
                        color = TextMediumLight,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Avatar with camera icon badge
            item {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFBAE6FD), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .background(BrandNavy, CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Name Input
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMediumLight)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = ElectricBlue
                        )
                    )
                }
            }

            // Age & Gender Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Age
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Age", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMediumLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = ElectricBlue
                            )
                        )
                    }

                    // Gender
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Gender", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMediumLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                                .clickable { genderExpanded = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(gender, color = TextHighLight, fontSize = 16.sp)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMediumLight)
                            }
                            DropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false }
                            ) {
                                listOf("Male", "Female", "Other").forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g) },
                                        onClick = { gender = g; genderExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Height & Weight Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Height
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Height", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMediumLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            suffix = { Text("cm", color = TextMediumLight) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = ElectricBlue
                            )
                        )
                    }

                    // Weight
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Weight", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMediumLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            suffix = { Text("kg", color = TextMediumLight) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = ElectricBlue
                            )
                        )
                    }
                }
            }
        }
    }
}
