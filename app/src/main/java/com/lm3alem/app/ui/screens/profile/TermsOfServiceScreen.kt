package com.lm3alem.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar

@Composable
fun TermsOfServiceScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.terms_of_service),
                onBackClick = { navController.popBackStack() },
                useBrandedColors = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = """
                    Welcome to Lm3alem. By using our application, you agree to the following terms:
                    
                    1. Acceptance of Terms
                    By accessing or using Lm3alem, you agree to be bound by these Terms of Service.
                    
                    2. User Conduct
                    You agree to use the application only for lawful purposes and in a way that does not infringe the rights of others.
                    
                    3. Account Security
                    You are responsible for maintaining the confidentiality of your account information.
                    
                    4. Limitation of Liability
                    Lm3alem is not liable for any damages arising from the use or inability to use the application.
                    
                    5. Changes to Terms
                    We reserve the right to modify these terms at any time. Continued use of the application constitutes acceptance of the new terms.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
