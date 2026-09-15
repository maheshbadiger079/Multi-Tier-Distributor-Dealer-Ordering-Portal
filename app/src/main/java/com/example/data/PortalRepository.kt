package com.example.data

import kotlinx.coroutines.flow.Flow

data class TierCalculationResult(
    val quantity: Int,
    val unitPrice: Double,
    val discountPercent: Double,
    val discountedUnitPrice: Double,
    val totalAmount: Double,
    val totalSavings: Double,
    val nextTierHint: String?
)

class PortalRepository(private val dao: PortalDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val allDistributors: Flow<List<DistributorEntity>> = dao.getAllDistributors()
    val allDealers: Flow<List<DealerEntity>> = dao.getAllDealers()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allInventory: Flow<List<InventoryEntity>> = dao.getAllInventory()
    val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    val allPricingTiers: Flow<List<PricingTierEntity>> = dao.getAllPricingTiers()
    val allStockMovements: Flow<List<StockMovementEntity>> = dao.getAllStockMovements()

    fun getDistributor(distributorId: String): Flow<DistributorEntity?> =
        dao.getDistributorFlow(distributorId)

    fun getDealersForDistributor(distributorId: String): Flow<List<DealerEntity>> =
        dao.getDealersByDistributor(distributorId)

    fun getOrdersForDistributor(distributorId: String): Flow<List<OrderEntity>> =
        dao.getOrdersForDistributor(distributorId)

    fun getCreditTransactions(distributorId: String): Flow<List<CreditTransactionEntity>> =
        dao.getCreditTransactions(distributorId)

    fun getAllocationsForDistributor(distributorId: String): Flow<List<InventoryAllocationEntity>> =
        dao.getAllocationsForDistributor(distributorId)

    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> =
        dao.getItemsForOrder(orderId)

    fun getOrderStatusHistory(orderId: String): Flow<List<OrderStatusHistoryEntity>> =
        dao.getStatusHistory(orderId)

    suspend fun getInventory(productId: String): InventoryEntity? =
        dao.getInventoryByProductId(productId)

    suspend fun getAllocation(distributorId: String, productId: String): InventoryAllocationEntity? =
        dao.getAllocation(distributorId, productId)

    suspend fun getDistributorDirect(distributorId: String): DistributorEntity? =
        dao.getDistributorById(distributorId)

    suspend fun getProductDirect(productId: String): ProductEntity? =
        dao.getProductById(productId)

    fun calculateBulkPricing(
        quantity: Int,
        basePrice: Double,
        tiers: List<PricingTierEntity>
    ): TierCalculationResult {
        val sortedTiers = tiers.sortedBy { it.minQty }
        var currentDiscount = 0.0
        var nextTier: PricingTierEntity? = null

        for (tier in sortedTiers) {
            if (quantity >= tier.minQty && (quantity <= tier.maxQty || tier.maxQty == 0)) {
                currentDiscount = tier.discountPercentage
            }
        }

        for (tier in sortedTiers) {
            if (tier.minQty > quantity) {
                nextTier = tier
                break
            }
        }

        val discountedUnitPrice = basePrice * (1.0 - (currentDiscount / 100.0))
        val totalAmount = discountedUnitPrice * quantity
        val totalSavings = (basePrice * quantity) - totalAmount

        val nextTierHint = if (nextTier != null) {
            val needed = nextTier.minQty - quantity
            "Add $needed more units to unlock the ${nextTier.discountPercentage}% pricing tier"
        } else {
            "Maximum tier (${currentDiscount}%) unlocked"
        }

        return TierCalculationResult(
            quantity = quantity,
            unitPrice = basePrice,
            discountPercent = currentDiscount,
            discountedUnitPrice = discountedUnitPrice,
            totalAmount = totalAmount,
            totalSavings = totalSavings,
            nextTierHint = nextTierHint
        )
    }

    suspend fun placeOrderAtomic(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        simulateRollbackFailure: Boolean = false,
        role: String = "DISTRIBUTOR"
    ): Result<OrderEntity> {
        return try {
            val placedOrder = dao.executeOrderAtomicTransaction(
                order = order,
                items = items,
                simulateRollbackFailure = simulateRollbackFailure,
                performedByRole = role
            )
            Result.success(placedOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        role: String,
        comments: String
    ) {
        dao.updateOrderStatusAtomic(orderId, newStatus, role, comments)
    }

    suspend fun updateDistributorCreditLimit(
        distributorId: String,
        newLimit: Double,
        adminRole: String
    ) {
        val dist = dao.getDistributorById(distributorId) ?: return
        dao.updateDistributor(dist.copy(creditLimit = newLimit))
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUD-" + java.util.UUID.randomUUID().toString().take(8),
                action = "CREDIT_LIMIT_UPDATED",
                entityType = "DISTRIBUTOR",
                entityId = distributorId,
                performedBy = adminRole,
                role = adminRole,
                timestamp = System.currentTimeMillis(),
                details = "Credit limit for ${dist.name} adjusted from ₹${dist.creditLimit} to ₹$newLimit",
                status = "SUCCESS"
            )
        )
    }

    suspend fun adjustInventory(
        productId: String,
        addedStock: Int,
        adminRole: String
    ) {
        val inv = dao.getInventoryByProductId(productId) ?: return
        val newTotal = inv.totalStock + addedStock
        val newAvailable = inv.availableStock + addedStock
        val newStatus = if (newAvailable > inv.reorderLevel) "HEALTHY" else "LOW"
        dao.updateInventory(
            inv.copy(
                totalStock = newTotal,
                availableStock = newAvailable,
                status = newStatus
            )
        )
        val now = System.currentTimeMillis()
        dao.insertStockMovement(
            StockMovementEntity(
                id = "SM-" + java.util.UUID.randomUUID().toString().take(8),
                productId = productId,
                distributorId = null,
                orderId = null,
                movementType = "INFLOW_RESTOCK",
                quantity = addedStock,
                timestamp = now,
                note = "Manual inventory adjustment by $adminRole"
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUD-" + java.util.UUID.randomUUID().toString().take(8),
                action = "INVENTORY_ADJUSTED",
                entityType = "PRODUCT",
                entityId = productId,
                performedBy = adminRole,
                role = adminRole,
                timestamp = now,
                details = "Stock adjusted by +$addedStock units. Available is now $newAvailable.",
                status = "SUCCESS"
            )
        )
    }
}
