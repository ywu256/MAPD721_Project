package com.group1.mapd721_project

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.group1.mapd721_project.ui.theme.MAPD721_ProjectTheme
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "settings",
    userPreferencesManager: UserPreferencesManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize BluetoothManager with cleanup
    val bluetoothManager = remember { BluetoothManager(context) }
    DisposableEffect(bluetoothManager) {
        onDispose {
            bluetoothManager.cleanup()
        }
    }

    // Collect Bluetooth state
    val isBluetoothEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()
    val showBluetoothDialog by bluetoothManager.showBluetoothDialog.collectAsState()

    // Check if Bluetooth is supported
    val isBluetoothSupported = remember { bluetoothManager.isBluetoothSupported() }

    // Bluetooth enable request launcher
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Update state after activity result
        bluetoothManager.updateBluetoothState()
    }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Permissions granted, update state
            bluetoothManager.updateBluetoothState()
        } else {
            Toast.makeText(context, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
        }
    }
    // notification part:


// Add a state for notification preference


    var email by remember { mutableStateOf("") }
    val darkModeEnabled by userPreferencesManager.darkModeFlow.collectAsState(initial = false)
    val billingManager = remember { MockBillingManager(context) }
    var showLogoutAlert by remember { mutableStateOf(false) }

    // set notification here
    // Cleanup on dispose
    DisposableEffect(billingManager) {
        onDispose {
            billingManager.cleanup()
        }
    }

    LaunchedEffect(Unit) {
        val userData = userPreferencesManager.getUser()
        email = userData.first ?: "Not logged in"
        // Update Bluetooth state on launch
        bluetoothManager.updateBluetoothState()
    }

    MAPD721_ProjectTheme(darkTheme = darkModeEnabled) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { onNavigate("home") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 16.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "medication_list",
                        onClick = { onNavigate("medication_list") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.medication),
                                contentDescription = "Medication"
                            )
                        },
                        label = { Text("Medication", fontSize = 16.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "settings",
                        onClick = { /* stay here */ },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 16.sp) }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Profile section
                Text(
                    text = "Account Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = "Person",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = email,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Connectivity
                Text(
                    text = "Connectivity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Bluetooth Item
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bluetooth),
                            contentDescription = "bluetooth",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Bluetooth",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        if (isBluetoothSupported) {
                            Switch(
                                checked = isBluetoothEnabled,
                                onCheckedChange = { checked ->
                                    if (!bluetoothManager.hasBluetoothPermission()) {
                                        permissionLauncher.launch(bluetoothManager.getRequiredPermissions())
                                    } else {
                                        if (checked) {
                                            bluetoothManager.enableBluetooth(bluetoothEnableLauncher)
                                        } else {
                                            bluetoothManager.disableBluetooth()
                                        }
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = "Not supported",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Appearance Section
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    // Dark Mode Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_dark_mode_24),
                            contentDescription = "DarkMode",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                scope.launch {
                                    userPreferencesManager.setDarkModeEnabled(it)
                                }
                            }
                        )
                    }
                }
                // Notification Section
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    // notification Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.notifications_48px),
                            contentDescription = "Notification",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Turn on/off",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                scope.launch {
                                    userPreferencesManager.setDarkModeEnabled(it)
                                }
                            }
                        )
                    }
                }
                // Subscription Section
                Text(
                    text = "Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SubscriptionSection(billingManager = billingManager)

                Spacer(Modifier.weight(1f))

                // Logout Button
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Button(
                        onClick = { showLogoutAlert = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Logout Dialog
                if (showLogoutAlert) {
                    AlertDialog(
                        onDismissRequest = { showLogoutAlert = false },
                        title = { Text("Confirm Logout") },
                        text = { Text("Are you sure you want to logout?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        userPreferencesManager.clearUser()
                                        PillboxStateManager.clearConnectedDevices()
                                        bluetoothManager.resetUserConsent()
                                        Toast.makeText(
                                            context,
                                            "Logged out successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onLogout()
                                        showLogoutAlert = false
                                    }
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showLogoutAlert = false }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }

                // Bluetooth Turn Off Dialog
                if (showBluetoothDialog) {
                    AlertDialog(
                        onDismissRequest = { bluetoothManager.dismissDialog() },
                        title = { Text("Turn Off Bluetooth") },
                        text = {
                            Text("On Android 12 and above, apps cannot directly turn off Bluetooth. " +
                                    "Would you like to go to Bluetooth settings to turn it off?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    bluetoothManager.openBluetoothSettings()
                                }
                            ) {
                                Text("Open Settings")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    bluetoothManager.dismissDialog()
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun SubscriptionSection(
    billingManager: MockBillingManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as? Activity
    val subscriptionPlans by billingManager.subscriptionPlans.collectAsState()
    val currentSubscription by billingManager.currentSubscription.collectAsState()
    val loadingState by billingManager.loadingState.collectAsState()
    val subscriptionStatus = billingManager.getSubscriptionStatus()

    // Animation states
    val cardScale = remember { Animatable(1f) }
    val badgeRotation = remember { Animatable(0f) }
    val shimmerEffect = rememberInfiniteTransition(label = "shimmer")
    val shimmerPosition = shimmerEffect.animateFloat(
        initialValue = -200f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPos"
    )

    // Animate badge when subscription is active
    LaunchedEffect(subscriptionStatus) {
        if (subscriptionStatus == SubscriptionStatus.ACTIVE ||
            subscriptionStatus == SubscriptionStatus.ACTIVE_NON_RENEWING) {
            badgeRotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = EaseOutBack
                )
            )
            badgeRotation.snapTo(0f)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(cardScale.value),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Premium Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                // Show badge if subscribed
                if (subscriptionStatus == SubscriptionStatus.ACTIVE ||
                    subscriptionStatus == SubscriptionStatus.ACTIVE_NON_RENEWING) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.rotate(badgeRotation.value)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscription Benefits
            Text(
                text = "Benefits:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            BenefitItem(benefit = "Unlimited medication reminders")
            BenefitItem(benefit = "Multiple pillbox connections")
            BenefitItem(benefit = "Advanced analytics and reports")
            BenefitItem(benefit = "Priority support")

            Spacer(modifier = Modifier.height(16.dp))

            // Subscription Plans
            when (loadingState) {
                BillingLoadingState.LOADING -> {
                    // Show loading shimmer effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            Color.Transparent
                                        ),
                                        startX = shimmerPosition.value - 200,
                                        endX = shimmerPosition.value + 200
                                    )
                                )
                        )
                    }
                }

                BillingLoadingState.LOADED -> {
                    if (subscriptionStatus == SubscriptionStatus.NONE) {
                        // Show subscription plans to choose from
                        Column {
                            Text(
                                text = "Choose a plan:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            subscriptionPlans.forEach { productDetails ->
                                val isMonthly = productDetails.productId == MockBillingManager.PREMIUM_MONTHLY
                                val isYearly = productDetails.productId == MockBillingManager.PREMIUM_YEARLY

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            activity?.let {
                                                scope.launch {
                                                    cardScale.animateTo(
                                                        targetValue = 0.95f,
                                                        animationSpec = tween(100)
                                                    )
                                                    cardScale.animateTo(
                                                        targetValue = 1f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                                        )
                                                    )

                                                    billingManager.launchSubscriptionFlow(it, productDetails)
                                                }
                                            }
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isYearly)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = productDetails.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = "${productDetails.formattedPrice} / ${
                                                    when (productDetails.billingPeriod) {
                                                        "P1M" -> "month"
                                                        "P1Y" -> "year"
                                                        else -> productDetails.billingPeriod
                                                    }
                                                }",
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                            if (isYearly) {
                                                Text(
                                                    text = "Best value!",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Subscribe"
                                        )
                                    }
                                }
                            }

                            // Add restore purchases button
                            TextButton(
                                onClick = { billingManager.restorePurchases() },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Restore Purchases")
                            }

                            Text(
                                text = "Subscriptions will auto-renew until canceled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        // Show active subscription details
                        val purchaseTime = currentSubscription?.purchaseTime?.let {
                            val date = java.util.Date(it)
                            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date)
                        } ?: "Unknown"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Active Subscription",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Purchased on:",
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                            Text(
                                                text = purchaseTime,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Status:",
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                            Text(
                                                text = if (currentSubscription?.isAutoRenewing == true)
                                                    "Auto-renewing" else "Active",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        // Simulate opening subscription management in Play Store
                                        Toast.makeText(
                                            context,
                                            "This would open subscription management in a real app",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Text("Manage Subscription")
                                }

                                OutlinedButton(
                                    onClick = { billingManager.cancelSubscription() }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }

                BillingLoadingState.EMPTY -> {
                    Text(
                        text = "No subscription plans available at this time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                BillingLoadingState.ERROR -> {
                    Text(
                        text = "Error loading subscription information. Please try again later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = { billingManager.connectToPlayBilling() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
@Composable
fun BenefitItem(benefit: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = benefit,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}