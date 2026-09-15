package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.PortalRepository
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = PortalRepository(database.portalDao())
        val viewModelFactory = PortalViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: PortalViewModel = viewModel(factory = viewModelFactory)
                MainPortalApp(viewModel)
            }
        }
    }
}

@Composable
fun MainPortalApp(viewModel: PortalViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val distributors by viewModel.distributors.collectAsState(initial = emptyList())
    val selectedDistributorId by viewModel.selectedDistributorId.collectAsState()
    val selectedDealerId by viewModel.selectedDealerId.collectAsState()

    val currentDistributor by viewModel.currentDistributor.collectAsState()
    val currentDealers by viewModel.currentDistributorDealers.collectAsState()
    val currentDistributorOrders by viewModel.currentDistributorOrders.collectAsState()
    val allOrders by viewModel.orders.collectAsState(initial = emptyList())
    val allProducts by viewModel.products.collectAsState(initial = emptyList())
    val allCategories by viewModel.categories.collectAsState(initial = emptyList())
    val allPricingTiers by viewModel.pricingTiers.collectAsState(initial = emptyList())
    val allInventory by viewModel.inventory.collectAsState(initial = emptyList())
    val currentAllocations by viewModel.currentDistributorAllocations.collectAsState()
    val currentCreditTransactions by viewModel.currentCreditTransactions.collectAsState()
    val allAuditLogs by viewModel.auditLogs.collectAsState(initial = emptyList())
    val allStockMovements by viewModel.stockMovements.collectAsState(initial = emptyList())

    val cartItemsDetails by viewModel.cartItemsDetails.collectAsState()
    val rollbackState by viewModel.rollbackState.collectAsState()
    val feedbackMessage by viewModel.orderFeedback.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("portal_main_scaffold"),
        containerColor = OffWhite,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mandatory Educational Simulation Banner
            SimulationBanner()

            // Enterprise Header
            B2BHeader(
                currentRole = currentRole,
                onRoleSelected = { viewModel.switchRole(it) },
                distributors = distributors,
                selectedDistributorId = selectedDistributorId,
                onDistributorSelected = { viewModel.selectDistributor(it) }
            )

            // Top Navigation Tab Bar
            Surface(
                color = PureWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, LightBorderGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PortalTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        val badgeCount = if (tab == PortalTab.CART_ORDER) cartItemsDetails.size else 0

                        Surface(
                            color = if (isSelected) Black else LightSurfaceGray,
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .clickable { viewModel.setActiveTab(tab) }
                                .border(1.dp, if (isSelected) Black else LightBorderGray, RoundedCornerShape(3.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tab.label.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (badgeCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = if (isSelected) PureWhite else Black,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "$badgeCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Black else PureWhite,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notification / Feedback Banner
            if (feedbackMessage != null) {
                Surface(
                    color = Black,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = feedbackMessage ?: "",
                            color = PureWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            color = LightBorderGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.clearFeedback() }
                        )
                    }
                }
            }

            // Screen Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (activeTab) {
                    PortalTab.DASHBOARD -> DistributorDashboardView(
                        viewModel = viewModel,
                        distributor = currentDistributor,
                        dealers = currentDealers,
                        orders = currentDistributorOrders,
                        products = allProducts,
                        allocations = currentAllocations
                    )
                    PortalTab.PRODUCTS -> ProductCatalogView(
                        viewModel = viewModel,
                        products = allProducts,
                        categories = allCategories,
                        pricingTiers = allPricingTiers
                    )
                    PortalTab.CALCULATOR -> BulkCalculatorView(
                        viewModel = viewModel,
                        products = allProducts,
                        tiers = allPricingTiers
                    )
                    PortalTab.CART_ORDER -> CartAndOrderView(
                        viewModel = viewModel,
                        cartItems = cartItemsDetails,
                        distributor = currentDistributor,
                        dealers = currentDealers,
                        selectedDealerId = selectedDealerId
                    )
                    PortalTab.ORDERS -> OrderHistoryView(
                        viewModel = viewModel,
                        orders = if (currentRole == "ADMIN" || currentRole == "SALES_MANAGER") allOrders else currentDistributorOrders,
                        role = currentRole
                    )
                    PortalTab.INVENTORY -> InventoryView(
                        viewModel = viewModel,
                        inventory = allInventory,
                        products = allProducts,
                        allocations = currentAllocations,
                        distributorId = selectedDistributorId
                    )
                    PortalTab.CREDIT -> CreditView(
                        viewModel = viewModel,
                        distributor = currentDistributor,
                        transactions = currentCreditTransactions
                    )
                    PortalTab.ROLLBACK_TEST -> RollbackTestView(
                        viewModel = viewModel,
                        rollbackState = rollbackState
                    )
                    PortalTab.ADMIN_PANEL -> AdminPanelView(
                        viewModel = viewModel,
                        distributors = distributors,
                        products = allProducts,
                        inventory = allInventory
                    )
                    PortalTab.REPORTS_AUDIT -> ReportsAndAuditView(
                        viewModel = viewModel,
                        orders = allOrders,
                        auditLogs = allAuditLogs,
                        stockMovements = allStockMovements
                    )
                }
            }
        }
    }
}
