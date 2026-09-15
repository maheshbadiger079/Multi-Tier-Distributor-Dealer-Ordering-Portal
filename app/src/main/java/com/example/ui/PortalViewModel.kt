package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class PortalTab(val label: String) {
    DASHBOARD("Dashboard"),
    PRODUCTS("Catalog"),
    CALCULATOR("Bulk Calc"),
    CART_ORDER("Cart & Order"),
    ORDERS("Orders"),
    INVENTORY("Inventory"),
    CREDIT("Credit"),
    ROLLBACK_TEST("Rollback Test"),
    ADMIN_PANEL("Admin"),
    REPORTS_AUDIT("Reports & Logs")
}

data class CartItemDetail(
    val product: ProductEntity,
    val quantity: Int,
    val unitPrice: Double,
    val discountPercent: Double,
    val discountedUnitPrice: Double,
    val lineTotal: Double,
    val savings: Double
)

data class RollbackTestState(
    val isRunning: Boolean = false,
    val step: String = "Idle. Ready to test atomic rollback consistency.",
    val initialStock: Int = 0,
    val initialCreditUsed: Double = 0.0,
    val initialOrderCount: Int = 0,
    val afterSuccessStock: Int = 0,
    val afterSuccessCreditUsed: Double = 0.0,
    val afterRollbackStock: Int = 0,
    val afterRollbackCreditUsed: Double = 0.0,
    val isSuccess: Boolean? = null,
    val message: String = ""
)

class PortalViewModel(private val repository: PortalRepository) : ViewModel() {

    private val _currentRole = MutableStateFlow("DISTRIBUTOR")
    val currentRole = _currentRole.asStateFlow()

    private val _selectedDistributorId = MutableStateFlow("PID-DEMO-MH-001")
    val selectedDistributorId = _selectedDistributorId.asStateFlow()

    private val _selectedDealerId = MutableStateFlow("DLR-MH-01")
    val selectedDealerId = _selectedDealerId.asStateFlow()

    private val _activeTab = MutableStateFlow(PortalTab.DASHBOARD)
    val activeTab = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow("NAME")
    val sortOption = _sortOption.asStateFlow()

    // Cart: Product ID -> Quantity
    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart = _cart.asStateFlow()

    // Calculator state
    private val _calcProductId = MutableStateFlow("PROD-01")
    val calcProductId = _calcProductId.asStateFlow()

    private val _calcQuantity = MutableStateFlow(60)
    val calcQuantity = _calcQuantity.asStateFlow()

    // Status / Feedback message
    private val _orderFeedback = MutableStateFlow<String?>(null)
    val orderFeedback = _orderFeedback.asStateFlow()

    // Rollback test status
    private val _rollbackState = MutableStateFlow(RollbackTestState())
    val rollbackState = _rollbackState.asStateFlow()

    // Data Flows from Repository
    val products = repository.allProducts
    val categories = repository.allCategories
    val distributors = repository.allDistributors
    val dealers = repository.allDealers
    val orders = repository.allOrders
    val inventory = repository.allInventory
    val pricingTiers = repository.allPricingTiers
    val auditLogs = repository.allAuditLogs
    val stockMovements = repository.allStockMovements

    val currentDistributor = _selectedDistributorId.flatMapLatest { id ->
        repository.getDistributor(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentDistributorDealers = _selectedDistributorId.flatMapLatest { id ->
        repository.getDealersForDistributor(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentDistributorOrders = _selectedDistributorId.flatMapLatest { id ->
        repository.getOrdersForDistributor(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentCreditTransactions = _selectedDistributorId.flatMapLatest { id ->
        repository.getCreditTransactions(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentDistributorAllocations = _selectedDistributorId.flatMapLatest { id ->
        repository.getAllocationsForDistributor(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cart detailed items with bulk discounts applied
    val cartItemsDetails: StateFlow<List<CartItemDetail>> = combine(
        _cart,
        products,
        pricingTiers
    ) { cartMap, prodList, tiers ->
        val prodMap = prodList.associateBy { it.id }
        cartMap.mapNotNull { (prodId, qty) ->
            val prod = prodMap[prodId] ?: return@mapNotNull null
            val pricing = repository.calculateBulkPricing(qty, prod.basePrice, tiers)
            CartItemDetail(
                product = prod,
                quantity = qty,
                unitPrice = prod.basePrice,
                discountPercent = pricing.discountPercent,
                discountedUnitPrice = pricing.discountedUnitPrice,
                lineTotal = pricing.totalAmount,
                savings = pricing.totalSavings
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Actions
    fun switchRole(role: String) {
        _currentRole.value = role
        // Adjust default tab per role
        when (role) {
            "ADMIN" -> _activeTab.value = PortalTab.ADMIN_PANEL
            "SALES_MANAGER" -> _activeTab.value = PortalTab.DASHBOARD
            "INVENTORY_MANAGER" -> _activeTab.value = PortalTab.INVENTORY
            "DEALER" -> _activeTab.value = PortalTab.PRODUCTS
            else -> _activeTab.value = PortalTab.DASHBOARD
        }
    }

    fun selectDistributor(id: String) {
        _selectedDistributorId.value = id
        viewModelScope.launch {
            val dealersList = repository.getDealersForDistributor(id).firstOrNull() ?: emptyList()
            if (dealersList.isNotEmpty()) {
                _selectedDealerId.value = dealersList.first().id
            }
        }
    }

    fun selectDealer(id: String) {
        _selectedDealerId.value = id
    }

    fun setActiveTab(tab: PortalTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun setSortOption(sort: String) {
        _sortOption.value = sort
    }

    fun setCalcProductId(productId: String) {
        _calcProductId.value = productId
    }

    fun setCalcQuantity(quantity: Int) {
        _calcQuantity.value = quantity.coerceAtLeast(1)
    }

    fun addToCart(productId: String, quantity: Int) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId] ?: 0
        current[productId] = (existing + quantity).coerceAtLeast(1)
        _cart.value = current
        _orderFeedback.value = "Added $quantity unit(s) to order"
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        val current = _cart.value.toMutableMap()
        if (quantity <= 0) {
            current.remove(productId)
        } else {
            current[productId] = quantity
        }
        _cart.value = current
    }

    fun removeFromCart(productId: String) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun clearFeedback() {
        _orderFeedback.value = null
    }

    fun placeOrder(idempotencyKey: String = UUID.randomUUID().toString()) {
        viewModelScope.launch {
            val details = cartItemsDetails.value
            if (details.isEmpty()) {
                _orderFeedback.value = "Cart is empty. Please add products to order."
                return@launch
            }

            val distId = _selectedDistributorId.value
            val dealerId = _selectedDealerId.value
            val subtotal = details.sumOf { it.unitPrice * it.quantity }
            val total = details.sumOf { it.lineTotal }
            val discountTotal = subtotal - total
            val orderId = "ORD-" + System.currentTimeMillis().toString().takeLast(8)
            val orderNumber = "PID-B2B-" + (1000 + (Math.random() * 9000).toInt())

            val orderEntity = OrderEntity(
                id = orderId,
                orderNumber = orderNumber,
                distributorId = distId,
                dealerId = dealerId,
                idempotencyKey = idempotencyKey,
                subtotal = subtotal,
                discountTotal = discountTotal,
                totalAmount = total,
                status = "CONFIRMED",
                paymentStatus = "APPROVED_CREDIT",
                createdAt = System.currentTimeMillis(),
                notes = "B2B portal order placed by ${_currentRole.value}"
            )

            val orderItems = details.map { item ->
                val tierId = when {
                    item.discountPercent >= 15.0 -> "TIER-05"
                    item.discountPercent >= 10.0 -> "TIER-04"
                    item.discountPercent >= 7.0 -> "TIER-03"
                    item.discountPercent >= 3.0 -> "TIER-02"
                    else -> "TIER-01"
                }
                OrderItemEntity(
                    id = "ITEM-" + UUID.randomUUID().toString().take(8),
                    orderId = orderId,
                    productId = item.product.id,
                    productName = item.product.name,
                    sku = item.product.sku,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    discountPercentage = item.discountPercent,
                    discountAmount = item.savings,
                    lineTotal = item.lineTotal,
                    pricingTierId = tierId
                )
            }

            val result = repository.placeOrderAtomic(
                order = orderEntity,
                items = orderItems,
                simulateRollbackFailure = false,
                role = _currentRole.value
            )

            if (result.isSuccess) {
                _cart.value = emptyMap()
                _orderFeedback.value = "Order #$orderNumber placed successfully! Atomic transaction committed."
                _activeTab.value = PortalTab.ORDERS
            } else {
                _orderFeedback.value = "Order Failed: " + (result.exceptionOrNull()?.message ?: "Transaction error")
            }
        }
    }

    fun runRollbackTest() {
        viewModelScope.launch {
            _rollbackState.value = RollbackTestState(isRunning = true, step = "Starting Section 35 Rollback Verification Test...")

            val testProductId = "PROD-01"
            val distId = _selectedDistributorId.value
            val dealerId = _selectedDealerId.value

            // 1. Initial State Snapshot
            val invBefore = repository.getInventory(testProductId)
            val distBefore = repository.getDistributorDirect(distId)
            val ordersBefore = repository.allOrders.first()

            val initStock = invBefore?.availableStock ?: 0
            val initCreditUsed = distBefore?.creditUsed ?: 0.0
            val initOrderCount = ordersBefore.size

            _rollbackState.value = _rollbackState.value.copy(
                step = "Snapshot recorded: Initial Available Stock = $initStock, Credit Used = ₹$initCreditUsed, Orders = $initOrderCount",
                initialStock = initStock,
                initialCreditUsed = initCreditUsed,
                initialOrderCount = initOrderCount
            )

            kotlinx.coroutines.delay(600)

            // 2. Execute a normal atomic order (100 units)
            val normalOrderId = "ORD-TEST-NORM-" + UUID.randomUUID().toString().take(6)
            val normalOrder = OrderEntity(
                id = normalOrderId,
                orderNumber = "TEST-SUCCESS-100",
                distributorId = distId,
                dealerId = dealerId,
                idempotencyKey = "IDEMP-TEST-NORM-" + UUID.randomUUID().toString().take(6),
                subtotal = 24000.0,
                discountTotal = 1680.0,
                totalAmount = 22320.0,
                status = "CONFIRMED",
                paymentStatus = "APPROVED_CREDIT",
                createdAt = System.currentTimeMillis(),
                notes = "Rollback Step 1: Normal transaction"
            )
            val normalItem = OrderItemEntity(
                id = "ITEM-TEST-NORM-1",
                orderId = normalOrderId,
                productId = testProductId,
                productName = "Fevicol SH Demo Adhesive",
                sku = "PID-FEV-001",
                quantity = 100,
                unitPrice = 240.0,
                discountPercentage = 7.0,
                discountAmount = 1680.0,
                lineTotal = 22320.0,
                pricingTierId = "TIER-03"
            )

            val normalResult = repository.placeOrderAtomic(
                order = normalOrder,
                items = listOf(normalItem),
                simulateRollbackFailure = false,
                role = "QA_ENGINEER"
            )

            if (normalResult.isFailure) {
                _rollbackState.value = _rollbackState.value.copy(
                    isRunning = false,
                    isSuccess = false,
                    message = "Step 1 failed: " + normalResult.exceptionOrNull()?.message
                )
                return@launch
            }

            // Check after success
            val invAfterSuccess = repository.getInventory(testProductId)
            val distAfterSuccess = repository.getDistributorDirect(distId)
            val successStock = invAfterSuccess?.availableStock ?: 0
            val successCredit = distAfterSuccess?.creditUsed ?: 0.0

            _rollbackState.value = _rollbackState.value.copy(
                step = "Step 1 complete: Normal order succeeded! Stock reduced to $successStock, Credit Used increased to ₹$successCredit",
                afterSuccessStock = successStock,
                afterSuccessCreditUsed = successCredit
            )

            kotlinx.coroutines.delay(800)

            // 3. Now execute an order that intentionally triggers SIMULATE_FAILURE
            val failOrderId = "ORD-TEST-FAIL-" + UUID.randomUUID().toString().take(6)
            val failOrder = OrderEntity(
                id = failOrderId,
                orderNumber = "TEST-FAIL-ROLLBACK",
                distributorId = distId,
                dealerId = dealerId,
                idempotencyKey = "IDEMP-TEST-FAIL-" + UUID.randomUUID().toString().take(6),
                subtotal = 24000.0,
                discountTotal = 1680.0,
                totalAmount = 22320.0,
                status = "CONFIRMED",
                paymentStatus = "APPROVED_CREDIT",
                createdAt = System.currentTimeMillis(),
                notes = "Rollback Step 2: Injected failure"
            )
            val failItem = OrderItemEntity(
                id = "ITEM-TEST-FAIL-1",
                orderId = failOrderId,
                productId = testProductId,
                productName = "Fevicol SH Demo Adhesive",
                sku = "PID-FEV-001",
                quantity = 100,
                unitPrice = 240.0,
                discountPercentage = 7.0,
                discountAmount = 1680.0,
                lineTotal = 22320.0,
                pricingTierId = "TIER-03"
            )

            val failResult = repository.placeOrderAtomic(
                order = failOrder,
                items = listOf(failItem),
                simulateRollbackFailure = true,
                role = "QA_ENGINEER"
            )

            kotlinx.coroutines.delay(600)

            // 4. Verify post-failure state matches pre-failure state
            val invAfterRollback = repository.getInventory(testProductId)
            val distAfterRollback = repository.getDistributorDirect(distId)
            val ordersAfterRollback = repository.allOrders.first()

            val rollbackStock = invAfterRollback?.availableStock ?: 0
            val rollbackCredit = distAfterRollback?.creditUsed ?: 0.0
            val rollbackOrdersCount = ordersAfterRollback.size

            val isConsistent = (rollbackStock == successStock) &&
                    (rollbackCredit == successCredit) &&
                    (rollbackOrdersCount == initOrderCount + 1) // Only normal order exists, failOrder rolled back!

            _rollbackState.value = _rollbackState.value.copy(
                isRunning = false,
                step = if (isConsistent) "VERIFICATION PASSED: Transaction rolled back atomically!" else "VERIFICATION FAILED",
                afterRollbackStock = rollbackStock,
                afterRollbackCreditUsed = rollbackCredit,
                isSuccess = isConsistent,
                message = if (isConsistent) {
                    "SUCCESS: Intentional error triggered. Inventory remained $rollbackStock, Credit used remained ₹$rollbackCredit. No duplicate order created. Full atomicity verified!"
                } else {
                    "FAILURE: Database state inconsistency detected during rollback test."
                }
            )
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, _currentRole.value, "Status updated to $newStatus by ${_currentRole.value}")
            _orderFeedback.value = "Order status updated to $newStatus"
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "CANCELLED", _currentRole.value, "Order cancelled by ${_currentRole.value}. Stock and Credit restored.")
            _orderFeedback.value = "Order cancelled. Inventory and Credit restored."
        }
    }

    fun adjustCreditLimit(distributorId: String, newLimit: Double) {
        viewModelScope.launch {
            repository.updateDistributorCreditLimit(distributorId, newLimit, _currentRole.value)
            _orderFeedback.value = "Credit limit updated to ₹$newLimit"
        }
    }

    fun adjustStock(productId: String, addedUnits: Int) {
        viewModelScope.launch {
            repository.adjustInventory(productId, addedUnits, _currentRole.value)
            _orderFeedback.value = "Adjusted stock by +$addedUnits units"
        }
    }
}

class PortalViewModelFactory(private val repository: PortalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PortalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
