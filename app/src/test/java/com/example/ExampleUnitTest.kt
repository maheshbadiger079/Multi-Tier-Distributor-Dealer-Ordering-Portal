package com.example

import com.example.data.DemoDataSeeder
import com.example.data.PricingTierEntity
import com.example.data.PortalRepository
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDemoProductsSeeding() {
        val products = DemoDataSeeder.products
        assertEquals(30, products.size)
        assertTrue(products.all { it.isDemo })
        assertTrue(products.all { it.moq > 0 })
    }

    @Test
    fun testDemoDistributorsSeeding() {
        val distributors = DemoDataSeeder.distributors
        assertEquals(5, distributors.size)
        assertTrue(distributors.all { it.creditLimit >= 800000.0 })
    }

    @Test
    fun testBulkPricingTiers() {
        val tiers = DemoDataSeeder.pricingTiers
        val mockDao = object : com.example.data.PortalDao {
            override fun getAllUsers() = emptyFlow<List<com.example.data.UserEntity>>()
            override suspend fun getUserById(userId: String) = null
            override suspend fun insertUsers(users: List<com.example.data.UserEntity>) {}
            override fun getAllDistributors() = emptyFlow<List<com.example.data.DistributorEntity>>()
            override suspend fun getDistributorById(id: String) = null
            override fun getDistributorFlow(id: String) = emptyFlow<com.example.data.DistributorEntity?>()
            override suspend fun insertDistributors(distributors: List<com.example.data.DistributorEntity>) {}
            override suspend fun updateDistributor(distributor: com.example.data.DistributorEntity) {}
            override fun getAllDealers() = emptyFlow<List<com.example.data.DealerEntity>>()
            override fun getDealersByDistributor(distributorId: String) = emptyFlow<List<com.example.data.DealerEntity>>()
            override suspend fun insertDealers(dealers: List<com.example.data.DealerEntity>) {}
            override fun getAllCategories() = emptyFlow<List<com.example.data.CategoryEntity>>()
            override suspend fun insertCategories(categories: List<com.example.data.CategoryEntity>) {}
            override fun getAllProducts() = emptyFlow<List<com.example.data.ProductEntity>>()
            override suspend fun getProductById(id: String) = null
            override suspend fun insertProducts(products: List<com.example.data.ProductEntity>) {}
            override fun getAllInventory() = emptyFlow<List<com.example.data.InventoryEntity>>()
            override suspend fun getInventoryByProductId(productId: String) = null
            override suspend fun insertInventory(items: List<com.example.data.InventoryEntity>) {}
            override suspend fun updateInventory(inventory: com.example.data.InventoryEntity) {}
            override fun getAllocationsForDistributor(distributorId: String) = emptyFlow<List<com.example.data.InventoryAllocationEntity>>()
            override suspend fun getAllocation(distributorId: String, productId: String) = null
            override suspend fun insertAllocations(allocations: List<com.example.data.InventoryAllocationEntity>) {}
            override suspend fun updateAllocation(allocation: com.example.data.InventoryAllocationEntity) {}
            override fun getAllPricingTiers() = emptyFlow<List<PricingTierEntity>>()
            override suspend fun getTiersForProduct(productId: String) = tiers
            override suspend fun insertPricingTiers(tiers: List<PricingTierEntity>) {}
            override fun getAllOrders() = emptyFlow<List<com.example.data.OrderEntity>>()
            override fun getOrdersForDistributor(distributorId: String) = emptyFlow<List<com.example.data.OrderEntity>>()
            override suspend fun getOrderById(orderId: String) = null
            override suspend fun getOrderByOrderKey(key: String) = null
            override suspend fun insertOrder(order: com.example.data.OrderEntity) {}
            override suspend fun updateOrder(order: com.example.data.OrderEntity) {}
            override fun getItemsForOrder(orderId: String) = emptyFlow<List<com.example.data.OrderItemEntity>>()
            override suspend fun getItemsForOrderList(orderId: String) = emptyList<com.example.data.OrderItemEntity>()
            override suspend fun insertOrderItems(items: List<com.example.data.OrderItemEntity>) {}
            override fun getCreditTransactions(distributorId: String) = emptyFlow<List<com.example.data.CreditTransactionEntity>>()
            override fun getAllCreditTransactions() = emptyFlow<List<com.example.data.CreditTransactionEntity>>()
            override suspend fun insertCreditTransaction(tx: com.example.data.CreditTransactionEntity) {}
            override fun getAllStockMovements() = emptyFlow<List<com.example.data.StockMovementEntity>>()
            override suspend fun insertStockMovement(sm: com.example.data.StockMovementEntity) {}
            override fun getStatusHistory(orderId: String) = emptyFlow<List<com.example.data.OrderStatusHistoryEntity>>()
            override suspend fun insertStatusHistory(history: com.example.data.OrderStatusHistoryEntity) {}
            override fun getAllAuditLogs() = emptyFlow<List<com.example.data.AuditLogEntity>>()
            override suspend fun insertAuditLog(log: com.example.data.AuditLogEntity) {}
            override suspend fun executeOrderAtomicTransaction(order: com.example.data.OrderEntity, items: List<com.example.data.OrderItemEntity>, simulateRollbackFailure: Boolean, performedByRole: String) = order
            override suspend fun updateOrderStatusAtomic(orderId: String, newStatus: String, changedByRole: String, comments: String) {}
        }

        val repo = PortalRepository(mockDao)

        // Test Tier 1: 30 units -> 0%
        val res1 = repo.calculateBulkPricing(30, 240.0, tiers)
        assertEquals(0.0, res1.discountPercent, 0.001)
        assertEquals(7200.0, res1.totalAmount, 0.001)
        assertEquals(0.0, res1.totalSavings, 0.001)
        assertTrue(res1.nextTierHint?.contains("Add 20 more units") == true)

        // Test Tier 2: 60 units -> 3%
        val res2 = repo.calculateBulkPricing(60, 240.0, tiers)
        assertEquals(3.0, res2.discountPercent, 0.001)
        assertEquals(232.8, res2.discountedUnitPrice, 0.001)
        assertEquals(13968.0, res2.totalAmount, 0.001)

        // Test Tier 3: 150 units -> 7%
        val res3 = repo.calculateBulkPricing(150, 240.0, tiers)
        assertEquals(7.0, res3.discountPercent, 0.001)

        // Test Tier 4: 300 units -> 10%
        val res4 = repo.calculateBulkPricing(300, 240.0, tiers)
        assertEquals(10.0, res4.discountPercent, 0.001)

        // Test Tier 5: 600 units -> 15%
        val res5 = repo.calculateBulkPricing(600, 240.0, tiers)
        assertEquals(15.0, res5.discountPercent, 0.001)
        assertEquals(204.0, res5.discountedUnitPrice, 0.001)
        assertEquals(122400.0, res5.totalAmount, 0.001)
        assertEquals(21600.0, res5.totalSavings, 0.001)
    }
}
