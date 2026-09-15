package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: String, // ADMIN, SALES_MANAGER, DISTRIBUTOR, DEALER, INVENTORY_MANAGER
    val distributorId: String? = null,
    val dealerId: String? = null
)

@Entity(tableName = "distributors")
data class DistributorEntity(
    @PrimaryKey val id: String, // e.g. PID-DEMO-MH-001
    val code: String,
    val name: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val region: String,
    val state: String,
    val creditLimit: Double,
    val creditUsed: Double
)

@Entity(tableName = "dealers")
data class DealerEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val distributorId: String,
    val contactPerson: String,
    val phone: String,
    val city: String,
    val state: String
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val sku: String,
    val packSize: String,
    val basePrice: Double,
    val moq: Int,
    val isDemo: Boolean = true,
    val description: String
)

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey val productId: String,
    val totalStock: Int,
    val allocatedStock: Int,
    val usedStock: Int,
    val availableStock: Int,
    val reservedStock: Int,
    val reorderLevel: Int,
    val status: String // HEALTHY, LOW, CRITICAL, OUT_OF_STOCK
)

@Entity(tableName = "inventory_allocations")
data class InventoryAllocationEntity(
    @PrimaryKey val id: String,
    val distributorId: String,
    val productId: String,
    val allocatedQuantity: Int,
    val usedQuantity: Int
)

@Entity(tableName = "pricing_tiers")
data class PricingTierEntity(
    @PrimaryKey val id: String,
    val productId: String?, // null applies as company standard tier
    val minQty: Int,
    val maxQty: Int,
    val discountPercentage: Double
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val distributorId: String,
    val dealerId: String,
    val idempotencyKey: String,
    val subtotal: Double,
    val discountTotal: Double,
    val totalAmount: Double,
    val status: String, // PENDING, CONFIRMED, PROCESSING, PACKED, SHIPPED, DELIVERED, CANCELLED, REJECTED
    val paymentStatus: String, // APPROVED_CREDIT, SETTLED
    val createdAt: Long,
    val notes: String
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val sku: String,
    val quantity: Int,
    val unitPrice: Double,
    val discountPercentage: Double,
    val discountAmount: Double,
    val lineTotal: Double,
    val pricingTierId: String?
)

@Entity(tableName = "credit_transactions")
data class CreditTransactionEntity(
    @PrimaryKey val id: String,
    val distributorId: String,
    val orderId: String?,
    val type: String, // DEBIT_ORDER, CREDIT_PAYMENT, ADJUSTMENT
    val amount: Double,
    val balanceAfter: Double,
    val timestamp: Long,
    val reason: String
)

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val distributorId: String?,
    val orderId: String?,
    val movementType: String, // OUTFLOW_ORDER, INFLOW_RESTOCK, ALLOCATION, CANCEL_RESTORE
    val quantity: Int,
    val timestamp: Long,
    val note: String
)

@Entity(tableName = "order_status_history")
data class OrderStatusHistoryEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val status: String,
    val changedBy: String,
    val timestamp: Long,
    val comments: String
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val performedBy: String,
    val role: String,
    val timestamp: Long,
    val details: String,
    val status: String // SUCCESS, ROLLBACK_FAILED, REJECTED
)
