package com.example.smartcitizensystem.ui.presentation.main.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White)
        ) {
            // Emergency Alert Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE8E8)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Emergency",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFFFF4444)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SOS Emergency",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4444)
                    )
                    Text(
                        text = "Press for immediate assistance",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Emergency Contacts
            Text(
                text = "Emergency Contacts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmergencyContactRow(
                name = "Police",
                number = "999",
                icon = Icons.Default.LocalPolice
            )

            EmergencyContactRow(
                name = "Fire Service",
                number = "999",
                icon = Icons.Default.LocalFireDepartment
            )

            EmergencyContactRow(
                name = "Ambulance",
                number = "999",
                icon = Icons.Default.LocalHospital
            )

            EmergencyContactRow(
                name = "National Helpline",
                number = "333",
                icon = Icons.Default.Phone
            )
        }
    }
}

@Composable
fun EmergencyContactRow(
    name: String,
    number: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7FF)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF7D7DFF)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = number,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { /* Call emergency number */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7D7DFF)
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Call")
            }
        }
    }
}