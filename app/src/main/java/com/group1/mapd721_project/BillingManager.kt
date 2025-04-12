package com.group1.mapd721_project

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A mock implementation of BillingManager for testing purposes.
 * This allows in-app billing features to be tested without connecting to Google Play.
 */
class MockBillingManager(private val context: Context) {

    private val TAG = "MockBillingManager"

    // State flows for UI consumption
    private val _subscriptionPlans = MutableStateFlow<List<MockProductDetails>>(emptyList())
    val subscriptionPlans: StateFlow<List<MockProductDetails>> = _subscriptionPlans

    private val _currentSubscription = MutableStateFlow<MockPurchase?>(null)
    val currentSubscription: StateFlow<MockPurchase?> = _currentSubscription

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _loadingState = MutableStateFlow(BillingLoadingState.LOADING)
    val loadingState: StateFlow<BillingLoadingState> = _loadingState

    // Define subscription product IDs
    companion object {
        const val PREMIUM_MONTHLY = "premium_monthly_subscription"
        const val PREMIUM_YEARLY = "premium_yearly_subscription"
    }

    init {
        // Simulate connection to billing service
        connectToPlayBilling()
    }

    fun connectToPlayBilling() {
        _loadingState.value = BillingLoadingState.LOADING

        // Simulate a small delay to mimic network request
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500) // 1.5 second delay
            _isConnected.value = true
            Log.d(TAG, "Billing setup finished successfully")

            // Query mock subscription products
            querySubscriptionProducts()

            // Query mock current subscriptions
            queryPurchases()
        }
    }

    private fun querySubscriptionProducts() {
        // Create mock product details
        val mockProducts = listOf(
            MockProductDetails(
                productId = PREMIUM_MONTHLY,
                title = "Monthly Premium",
                description = "Premium subscription billed monthly",
                formattedPrice = "$4.99",
                billingPeriod = "P1M"
            ),
            MockProductDetails(
                productId = PREMIUM_YEARLY,
                title = "Yearly Premium",
                description = "Premium subscription billed yearly (20% off)",
                formattedPrice = "$47.99",
                billingPeriod = "P1Y"
            )
        )

        _subscriptionPlans.value = mockProducts
        _loadingState.value = BillingLoadingState.LOADED
        Log.d(TAG, "Product details loaded: ${mockProducts.size}")
    }

    private fun queryPurchases() {
        // By default, no active subscription
        _currentSubscription.value = null
    }

    fun launchSubscriptionFlow(activity: Activity, productDetails: MockProductDetails) {
        Log.d(TAG, "Purchase flow launched for ${productDetails.productId}")

        // Simulate delay and successful purchase
        CoroutineScope(Dispatchers.Main).launch {
            _loadingState.value = BillingLoadingState.LOADING
            delay(2000) // 2 second delay

            // Create a mock purchase
            val purchase = MockPurchase(
                purchaseToken = "mock-token-${System.currentTimeMillis()}",
                purchaseTime = System.currentTimeMillis(),
                productId = productDetails.productId,
                isAutoRenewing = true,
                purchaseState = MockPurchaseState.PURCHASED
            )

            _currentSubscription.value = purchase
            _loadingState.value = BillingLoadingState.LOADED

            // Notify user of successful purchase
            Toast.makeText(
                context,
                "Purchase successful: ${productDetails.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getSubscriptionStatus(): SubscriptionStatus {
        val purchase = _currentSubscription.value

        return if (purchase != null &&
            purchase.purchaseState == MockPurchaseState.PURCHASED &&
            !purchase.isAutoRenewing) {
            SubscriptionStatus.ACTIVE_NON_RENEWING
        } else if (purchase != null &&
            purchase.purchaseState == MockPurchaseState.PURCHASED) {
            SubscriptionStatus.ACTIVE
        } else {
            SubscriptionStatus.NONE
        }
    }

    fun restorePurchases() {
        Log.d(TAG, "Restore purchases called")
        _loadingState.value = BillingLoadingState.LOADING

        // Simulate delay for restore process
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)

            // 50% chance of having a previous purchase to restore
            if (Math.random() > 0.5) {
                val purchase = MockPurchase(
                    purchaseToken = "mock-restore-token-${System.currentTimeMillis()}",
                    purchaseTime = System.currentTimeMillis() - 86400000, // 1 day ago
                    productId = PREMIUM_YEARLY,
                    isAutoRenewing = true,
                    purchaseState = MockPurchaseState.PURCHASED
                )

                _currentSubscription.value = purchase

                Toast.makeText(
                    context,
                    "Previous subscription restored!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "No previous subscriptions found",
                    Toast.LENGTH_SHORT
                ).show()
            }

            _loadingState.value = BillingLoadingState.LOADED
        }
    }

    fun cancelSubscription() {
        Log.d(TAG, "Cancel subscription called")

        // Simulate cancellation
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            _currentSubscription.value = null

            Toast.makeText(
                context,
                "Subscription cancelled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cleanup() {
        // Nothing to clean up in mock implementation
        Log.d(TAG, "Billing manager cleaned up")
    }
}

// Mock data classes to replace Google Play Billing classes
data class MockProductDetails(
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val billingPeriod: String
)

data class MockPurchase(
    val purchaseToken: String,
    val purchaseTime: Long,
    val productId: String,
    val isAutoRenewing: Boolean,
    val purchaseState: MockPurchaseState
)

enum class MockPurchaseState {
    PURCHASED,
    PENDING,
    UNSPECIFIED
}

// Include the BillingLoadingState and SubscriptionStatus enums directly here
// Since they were defined in the original BillingManager
enum class BillingLoadingState {
    LOADING,
    LOADED,
    EMPTY,
    ERROR
}

enum class SubscriptionStatus {
    NONE,
    ACTIVE,
    ACTIVE_NON_RENEWING
}