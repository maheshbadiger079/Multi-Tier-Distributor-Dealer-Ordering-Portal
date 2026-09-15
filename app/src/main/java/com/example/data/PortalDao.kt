package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PortalDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // --- Distributors ---
    @Query("SELECT * FROM distributors")
    fun getAllDistributors(): Flow<List<DistributorEntity>>

    @Query("SELECT * FROM distributors WHERE id = :id LIMIT 1")
    suspend fun getDistributorById(id: String): DistributorEntity?

    @Query("SELECT * FROM distributors WHERE id = :id LIMIT 1")
    fun getDistributorFlow(id: String): Flow<DistributorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistributors(distributors: List<DistributorEntity>)

    @Update
    suspend fun updateDistributor(distributor: DistributorEntity)

    // --- Dealers ---
    @Query("SELECT * FROM dealers")
    fun getAllDealers(): Flow<List<DealerEntity>>

    @Query("SELECT * FROM dealers WHERE distributorId = :distributorId")
    fun getDealersByDistributor(distributorId: String): Flow<List<DealerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDealers(dealers: List<DealerEntity>)

    // --- Categories ---
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    // --- Products ---
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    // --- Inventory ---
    @Query("SELECT * FROM inventory")
    fun getAllInventory(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE productId = :productId LIMIT 1")
    suspend fun getInventoryByProductId(productId: String): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(items: List<InventoryEntity>)

    @Update
    suspend fun updateInventory(inventory: InventoryEntity)

    // --- Allocations ---
    @Query("SELECT * FROM inventory_allocations WHERE distributorId = :distributorId")
    fun getAllocationsForDistributor(distributorId: String): Flow<List<InventoryAllocationEntity>>

    @Query("SELECT * FROM inventory_allocations WHERE distributorId = :distributorId AND productId = :productId LIMIT 1")
    suspend fun getAllocation(distributorId: String, productId: String): InventoryAllocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocations(allocations: List<InventoryAllocationEntity>)

    @Update
    suspend fun updateAllocation(allocation: InventoryAllocationEntity)

    // --- Pricing Tiers ---
    @Query("SELECT * FROM pricing_tiers ORDER BY minQty ASC")
    fun getAllPricingTiers(): Flow<List<PricingTierEntity>>

    @Query("SELECT * FROM pricing_tiers WHERE productId IS NULL OR productId = :productId ORDER BY minQty ASC")
    suspend fun getTiersForProduct(productId: String): List<PricingTierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPricingTiers(tiers: List<PricingTierEntity>)

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE distributorId = :distributorId ORDER BY createdAt DESC")
    fun getOrdersForDistributor(distributorId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getOrderByOrderKey(key: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // --- Order Items ---
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrderList(orderId: String): List<OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // --- Credit Transactions ---
    @Query("SELECT * FROM credit_transactions WHERE distributorId = :distributorId ORDER BY timestamp DESC")
    fun getCreditTransactions(distributorId: String): Flow<List<CreditTransactionEntity>>

    @Query("SELECT * FROM credit_transactions ORDER BY timestamp DESC")
    fun getAllCreditTransactions(): Flow<List<CreditTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditTransaction(tx: CreditTransactionEntity)

    // --- Stock Movements ---
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllStockMovements(): Flow<List<StockMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(sm: StockMovementEntity)

    // --- Order Status History ---
    @Query("SELECT * FROM order_status_history WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getStatusHistory(orderId: String): Flow<List<OrderStatusHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: OrderStatusHistoryEntity)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // --- CRITICAL ATOMIC TRANSACTION: PLACE ORDER ---
    @Transaction
    suspend fun executeOrderAtomicTransaction(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        simulateRollbackFailure: Boolean = false,
        performedByRole: String = "DISTRIBUTOR"
    ): OrderEntity {
        // 1. Idempotency Check
        val existing = getOrderByOrderKey(order.idempotencyKey)
        if (existing != null) {
            return existing
        }

        // 2. Lock and Check Distributor Credit
        val distributor = getDistributorById(order.distributorId)
            ?: throw IllegalStateException("DISTRIBUTOR_NOT_FOUND: ${order.distributorId}")

        val availableCredit = distributor.creditLimit - distributor.creditUsed
        if (order.totalAmount > availableCredit) {
            throw IllegalStateException("INSUFFICIENT_CREDIT: Order total ₹${order.totalAmount} exceeds available credit ₹$availableCredit (Limit: ₹${distributor.creditLimit}, Used: ₹${distributor.creditUsed})")
        }

        // 3. Verify Product Inventory & Allocations
        for (item in items) {
            val inv = getInventoryByProductId(item.productId)
                ?: throw IllegalStateException("PRODUCT_INVENTORY_NOT_FOUND: ${item.productId}")

            if (inv.availableStock < item.quantity) {
                throw IllegalStateException("INSUFFICIENT_STOCK: Item '${item.productName}' requested ${item.quantity} units, but central inventory only has ${inv.availableStock} available")
            }

            val alloc = getAllocation(order.distributorId, item.productId)
            if (alloc != null) {
                val availableAlloc = alloc.allocatedQuantity - alloc.usedQuantity
                if (item.quantity > availableAlloc) {
                    throw IllegalStateException("INSUFFICIENT_ALLOCATION: Item '${item.productName}' requested ${item.quantity} units, but distributor allocation only has $availableAlloc available")
                }
            }
        }

        // 4. Intentional Rollback Trigger (For testing atomicity per Section 35)
        if (simulateRollbackFailure) {
            throw IllegalStateException("SIMULATED_FAILURE_FOR_ROLLBACK_TEST: Intentional transaction failure injected to prove atomic rollback consistency")
        }

        // 5. Update Inventory and Allocations
        val now = System.currentTimeMillis()
        for (item in items) {
            val inv = getInventoryByProductId(item.productId)!!
            val newAvailable = inv.availableStock - item.quantity
            val newUsed = inv.usedStock + item.quantity
            val newStatus = when {
                newAvailable <= 0 -> "OUT_OF_STOCK"
                newAvailable <= inv.reorderLevel / 2 -> "CRITICAL"
                newAvailable <= inv.reorderLevel -> "LOW"
                else -> "HEALTHY"
            }
            updateInventory(
                inv.copy(
                    availableStock = newAvailable,
                    usedStock = newUsed,
                    status = newStatus
                )
            )

            // Update Allocation if exists
            val alloc = getAllocation(order.distributorId, item.productId)
            if (alloc != null) {
                updateAllocation(alloc.copy(usedQuantity = alloc.usedQuantity + item.quantity))
            }

            // Create Stock Movement record
            insertStockMovement(
                StockMovementEntity(
                    id = "SM-" + java.util.UUID.randomUUID().toString().take(8),
                    productId = item.productId,
                    distributorId = order.distributorId,
                    orderId = order.id,
                    movementType = "OUTFLOW_ORDER",
                    quantity = item.quantity,
                    timestamp = now,
                    note = "Deduction for Order #${order.orderNumber}"
                )
            )
        }

        // 6. Update Distributor Credit Used
        val updatedCreditUsed = distributor.creditUsed + order.totalAmount
        updateDistributor(distributor.copy(creditUsed = updatedCreditUsed))

        // 7. Insert Credit Transaction
        val remainingCredit = distributor.creditLimit - updatedCreditUsed
        insertCreditTransaction(
            CreditTransactionEntity(
                id = "CTX-" + java.util.UUID.randomUUID().toString().take(8),
                distributorId = order.distributorId,
                orderId = order.id,
                type = "DEBIT_ORDER",
                amount = order.totalAmount,
                balanceAfter = remainingCredit,
                timestamp = now,
                reason = "Order placement for Order #${order.orderNumber}"
            )
        )

        // 8. Insert Order & Order Items
        insertOrder(order)
        insertOrderItems(items)

        // 9. Insert Order Status History
        insertStatusHistory(
            OrderStatusHistoryEntity(
                id = "OSH-" + java.util.UUID.randomUUID().toString().take(8),
                orderId = order.id,
                status = order.status,
                changedBy = performedByRole,
                timestamp = now,
                comments = "Order placed and confirmed via atomic database transaction"
            )
        )

        // 10. Insert Audit Log
        insertAuditLog(
            AuditLogEntity(
                id = "AUD-" + java.util.UUID.randomUUID().toString().take(8),
                action = "ORDER_CREATED",
                entityType = "ORDER",
                entityId = order.id,
                performedBy = order.distributorId,
                role = performedByRole,
                timestamp = now,
                details = "Order #${order.orderNumber} created with ${items.size} items. Total: ₹${order.totalAmount}. Credit used updated to ₹$updatedCreditUsed.",
                status = "SUCCESS"
            )
        )

        return order
    }

    // --- Update Order Status with Audit Log ---
    @Transaction
    suspend fun updateOrderStatusAtomic(
        orderId: String,
        newStatus: String,
        changedByRole: String,
        comments: String
    ) {
        val order = getOrderById(orderId) ?: return
        val now = System.currentTimeMillis()

        // If cancelling, restore inventory and credit!
        if (newStatus == "CANCELLED" && order.status != "CANCELLED") {
            val items = getItemsForOrderList(orderId)
            for (item in items) {
                val inv = getInventoryByProductId(item.productId)
                if (inv != null) {
                    val restoredStock = inv.availableStock + item.quantity
                    val restoredUsed = (inv.usedStock - item.quantity).coerceAtLeast(0)
                    updateInventory(
                        inv.copy(
                            availableStock = restoredStock,
                            usedStock = restoredUsed,
                            status = if (restoredStock > inv.reorderLevel) "HEALTHY" else "LOW"
                        )
                    )
                }
                val alloc = getAllocation(order.distributorId, item.productId)
                if (alloc != null) {
                    updateAllocation(alloc.copy(usedQuantity = (alloc.usedQuantity - item.quantity).coerceAtLeast(0)))
                }
                insertStockMovement(
                    StockMovementEntity(
                        id = "SM-" + java.util.UUID.randomUUID().toString().take(8),
                        productId = item.productId,
                        distributorId = order.distributorId,
                        orderId = order.id,
                        movementType = "CANCEL_RESTORE",
                        quantity = item.quantity,
                        timestamp = now,
                        note = "Inventory restored due to cancellation of Order #${order.orderNumber}"
                    )
                )
            }

            // Restore Credit
            val dist = getDistributorById(order.distributorId)
            if (dist != null) {
                val newCreditUsed = (dist.creditUsed - order.totalAmount).coerceAtLeast(0.0)
                updateDistributor(dist.copy(creditUsed = newCreditUsed))
                insertCreditTransaction(
                    CreditTransactionEntity(
                        id = "CTX-" + java.util.UUID.randomUUID().toString().take(8),
                        distributorId = order.distributorId,
                        orderId = order.id,
                        type = "CREDIT_REFUND",
                        amount = order.totalAmount,
                        balanceAfter = dist.creditLimit - newCreditUsed,
                        timestamp = now,
                        reason = "Credit restored on Order #${order.orderNumber} cancellation"
                    )
                )
            }
        }

        updateOrder(order.copy(status = newStatus))
        insertStatusHistory(
            OrderStatusHistoryEntity(
                id = "OSH-" + java.util.UUID.randomUUID().toString().take(8),
                orderId = orderId,
                status = newStatus,
                changedBy = changedByRole,
                timestamp = now,
                comments = comments
            )
        )
        insertAuditLog(
            AuditLogEntity(
                id = "AUD-" + java.util.UUID.randomUUID().toString().take(8),
                action = "ORDER_STATUS_CHANGED",
                entityType = "ORDER",
                entityId = orderId,
                performedBy = changedByRole,
                role = changedByRole,
                timestamp = now,
                details = "Order #${order.orderNumber} status changed from ${order.status} to $newStatus. $comments",
                status = "SUCCESS"
            )
        )
    }
}
