package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        DistributorEntity::class,
        DealerEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        InventoryEntity::class,
        InventoryAllocationEntity::class,
        PricingTierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CreditTransactionEntity::class,
        StockMovementEntity::class,
        OrderStatusHistoryEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun portalDao(): PortalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pidilite_portal_demo.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.portalDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: PortalDao) {
                dao.insertCategories(DemoDataSeeder.categories)
                dao.insertProducts(DemoDataSeeder.products)
                dao.insertDistributors(DemoDataSeeder.distributors)
                dao.insertDealers(DemoDataSeeder.dealers)
                dao.insertPricingTiers(DemoDataSeeder.pricingTiers)
                dao.insertUsers(DemoDataSeeder.users)
                dao.insertInventory(DemoDataSeeder.generateInitialInventory())
                dao.insertAllocations(DemoDataSeeder.generateInitialAllocations())

                val (orders, items) = DemoDataSeeder.generateInitialOrders()
                for (o in orders) {
                    dao.insertOrder(o)
                }
                dao.insertOrderItems(items)
                for (ctx in DemoDataSeeder.initialCreditTransactions) {
                    dao.insertCreditTransaction(ctx)
                }
                for (aud in DemoDataSeeder.initialAuditLogs) {
                    dao.insertAuditLog(aud)
                }
            }
        }
    }
}
