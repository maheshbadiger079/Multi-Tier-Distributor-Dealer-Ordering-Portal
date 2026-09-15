package com.example.data

object DemoDataSeeder {

    val categories = listOf(
        CategoryEntity("CAT-ADH", "ADHESIVES", "Wood, paper, instant and synthetic adhesives"),
        CategoryEntity("CAT-WPR", "WATERPROOFING", "Roof, wall and concrete waterproofing systems"),
        CategoryEntity("CAT-CCH", "CONSTRUCTION CHEMICALS", "Tile adhesives, grouts, admixtures and structural repair"),
        CategoryEntity("CAT-SLN", "SEALANTS", "Silicone, epoxy and acrylic building sealants"),
        CategoryEntity("CAT-IND", "INDUSTRIAL PRODUCTS", "Specialized engineering adhesives, tapes and lubricants"),
        CategoryEntity("CAT-WOD", "WOOD & FURNITURE SOLUTIONS", "PUR adhesives, wood preservatives and furniture finishes"),
        CategoryEntity("CAT-OTH", "OTHER", "Art materials, DIY solutions and maintenance products")
    )

    val products = listOf(
        ProductEntity("PROD-01", "Fevicol SH Demo Adhesive", "ADHESIVES", "PID-FEV-001", "1 kg", 240.0, 10, true, "Standard synthetic resin adhesive (Demo Product)"),
        ProductEntity("PROD-02", "Fevicol Marine Demo Waterproof Adhesive", "ADHESIVES", "PID-FEV-002", "5 kg", 1150.0, 5, true, "Waterproof specialized adhesive for furniture (Demo Product)"),
        ProductEntity("PROD-03", "Fevikwik Demo Instant Adhesive", "ADHESIVES", "PID-FEV-003", "20 g (Box of 24)", 480.0, 12, true, "Instant cyanoacrylate adhesive pack (Demo Product)"),
        ProductEntity("PROD-04", "Fevicol MR Demo Craft Adhesive", "ADHESIVES", "PID-FEV-004", "500 g", 110.0, 20, true, "White craft and bonding solution (Demo Product)"),
        ProductEntity("PROD-05", "Fevicol HeatX Demo Heat Proof Adhesive", "ADHESIVES", "PID-FEV-005", "1 L", 420.0, 6, true, "Heat-resistant contact bonding adhesive (Demo Product)"),

        ProductEntity("PROD-06", "Dr. Fixit Demo Waterproofing LW+", "WATERPROOFING", "PID-DFX-001", "5 L", 720.0, 4, true, "Integral liquid waterproofing compound for concrete (Demo Product)"),
        ProductEntity("PROD-07", "Dr. Fixit Demo Roofseal Classic", "WATERPROOFING", "PID-DFX-002", "20 L", 4800.0, 2, true, "Elastomeric waterproof coating for flat roofs (Demo Product)"),
        ProductEntity("PROD-08", "Dr. Fixit Demo Raincoat Waterproof Coat", "WATERPROOFING", "PID-DFX-003", "10 L", 2600.0, 2, true, "High build acrylic exterior waterproofing (Demo Product)"),
        ProductEntity("PROD-09", "Dr. Fixit Demo Crack-X Paste", "WATERPROOFING", "PID-DFX-004", "1 kg", 290.0, 10, true, "Acrylic crack filling compound for plaster (Demo Product)"),
        ProductEntity("PROD-10", "Dr. Fixit Demo Bathseal Tape", "WATERPROOFING", "PID-DFX-005", "10 m Roll", 380.0, 8, true, "Self-adhesive waterproofing tape for corners (Demo Product)"),

        ProductEntity("PROD-11", "Roff Demo Tile Adhesive Cera Clean", "CONSTRUCTION CHEMICALS", "PID-ROF-001", "20 kg", 560.0, 10, true, "Polymer modified cementitious tile adhesive (Demo Product)"),
        ProductEntity("PROD-12", "Roff Demo Rainbow Tile Grout", "CONSTRUCTION CHEMICALS", "PID-ROF-002", "1 kg", 150.0, 25, true, "Joint filler powder with water repellent properties (Demo Product)"),
        ProductEntity("PROD-13", "Roff Demo Master Fix Tile Adhesive", "CONSTRUCTION CHEMICALS", "PID-ROF-003", "25 kg", 680.0, 10, true, "Heavy-duty ceramic tile fixing adhesive (Demo Product)"),
        ProductEntity("PROD-14", "Dr. Fixit Demo Micro Concrete", "CONSTRUCTION CHEMICALS", "PID-DFX-006", "25 kg", 820.0, 5, true, "Non-shrink repair micro concrete mortar (Demo Product)"),

        ProductEntity("PROD-15", "M-Seal Demo Epoxy Compound", "SEALANTS", "PID-MSL-001", "100 g (Box of 12)", 360.0, 12, true, "Multi-purpose sealing and joining epoxy compound (Demo Product)"),
        ProductEntity("PROD-16", "M-Seal Demo Clear Silicone Sealant", "SEALANTS", "PID-MSL-002", "280 ml Cartridge", 210.0, 12, true, "High flexibility clear architectural silicone (Demo Product)"),
        ProductEntity("PROD-17", "M-Seal Demo Gap Seal Acrylic", "SEALANTS", "PID-MSL-003", "450 g", 180.0, 10, true, "Interior perimeter gap sealant for skirting (Demo Product)"),
        ProductEntity("PROD-18", "M-Seal Demo Sanitary Silicone White", "SEALANTS", "PID-MSL-004", "300 ml", 250.0, 12, true, "Anti-fungal white kitchen and bath silicone (Demo Product)"),

        ProductEntity("PROD-19", "Steelgrip Demo PVC Electrical Tape", "INDUSTRIAL PRODUCTS", "PID-STG-001", "10 Rolls Pack", 190.0, 20, true, "Self-extinguishing electrical insulation tape (Demo Product)"),
        ProductEntity("PROD-20", "Araldite Demo Standard Epoxy", "INDUSTRIAL PRODUCTS", "PID-ARD-001", "180 g Kit", 340.0, 10, true, "High-strength two-component structural resin (Demo Product)"),
        ProductEntity("PROD-21", "Araldite Demo Klear Quick Setting", "INDUSTRIAL PRODUCTS", "PID-ARD-002", "90 g Kit", 220.0, 12, true, "5-minute transparent curing epoxy kit (Demo Product)"),
        ProductEntity("PROD-22", "Industrial Threadlocker Demo 242", "INDUSTRIAL PRODUCTS", "PID-IND-001", "50 ml", 450.0, 5, true, "Medium strength anaerobic fastener sealant (Demo Product)"),

        ProductEntity("PROD-23", "Fevicol Probond Demo Laminate Adhesive", "WOOD & FURNITURE SOLUTIONS", "PID-FEV-006", "5 kg", 1280.0, 4, true, "Specialized adhesive for PVC and acrylic sheets (Demo Product)"),
        ProductEntity("PROD-24", "Fevicol 1K PUR Demo Wood Adhesive", "WOOD & FURNITURE SOLUTIONS", "PID-FEV-007", "500 g", 390.0, 6, true, "Moisture-curing polyurethane exterior adhesive (Demo Product)"),
        ProductEntity("PROD-25", "Terminator Demo Wood Preservative", "WOOD & FURNITURE SOLUTIONS", "PID-TRM-001", "1 L", 310.0, 8, true, "Eco-friendly anti-termite wood treatment spray (Demo Product)"),
        ProductEntity("PROD-26", "Wood Polish Demo Clear Gloss", "WOOD & FURNITURE SOLUTIONS", "PID-WOD-001", "4 L", 980.0, 4, true, "Protective high-gloss timber polyurethane coating (Demo Product)"),

        ProductEntity("PROD-27", "Fevicryl Demo Fabric Acrylic Set", "OTHER", "PID-FCY-001", "10 Shades Box", 260.0, 15, true, "Professional hobby fabric and canvas color kit (Demo Product)"),
        ProductEntity("PROD-28", "Rangeela Demo Tempera Color Pack", "OTHER", "PID-RNG-001", "12 Tubes Pack", 140.0, 20, true, "Vibrant water-soluble educational paint pack (Demo Product)"),
        ProductEntity("PROD-29", "WD-40 Demo Multi-Use Lubricant Spray", "OTHER", "PID-WD4-001", "420 ml", 399.0, 6, true, "Anti-rust and penetrating displacement formula (Demo Product)"),
        ProductEntity("PROD-30", "Masking Tape Demo Heavy Duty", "OTHER", "PID-MSK-001", "6 Rolls Pack", 280.0, 10, true, "Crepe paper residue-free protective masking tape (Demo Product)")
    )

    val distributors = listOf(
        DistributorEntity("PID-DEMO-MH-001", "PID-MH-001", "Western Build Supplies Pvt. Ltd. — Demo Distributor", "Rajesh Sharma", "+91 98201 12345", "dist.west@example.com", "West Zone", "Maharashtra", 1000000.0, 450000.0),
        DistributorEntity("PID-DEMO-KA-002", "PID-KA-002", "Southern Infra Supplies — Demo Distributor", "Venkatesh Rao", "+91 98450 54321", "dist.south@example.com", "South Zone", "Karnataka", 1500000.0, 620000.0),
        DistributorEntity("PID-DEMO-GJ-003", "PID-GJ-003", "Gujarat Industrial Solutions — Demo Distributor", "Bhavin Patel", "+91 98790 67890", "dist.west2@example.com", "West Zone", "Gujarat", 800000.0, 210000.0),
        DistributorEntity("PID-DEMO-TN-004", "PID-TN-004", "Coromandel Buildtech — Demo Distributor", "Murugan Swamy", "+91 94440 13579", "dist.south2@example.com", "South Zone", "Tamil Nadu", 1200000.0, 780000.0),
        DistributorEntity("PID-DEMO-UP-005", "PID-UP-005", "North Central Hardware Hub — Demo Distributor", "Alok Srivastava", "+91 94150 98765", "dist.north@example.com", "North Zone", "Uttar Pradesh", 900000.0, 340000.0)
    )

    val dealers = listOf(
        // For PID-DEMO-MH-001
        DealerEntity("DLR-MH-01", "MH-D01", "Mumbai Hardware Demo", "PID-DEMO-MH-001", "Kailash Gada", "+91 98200 11111", "Mumbai", "Maharashtra"),
        DealerEntity("DLR-MH-02", "MH-D02", "Pune Construction Demo", "PID-DEMO-MH-001", "Sanjay Deshmukh", "+91 98220 22222", "Pune", "Maharashtra"),
        DealerEntity("DLR-MH-03", "MH-D03", "Thane Builders Hardware Demo", "PID-DEMO-MH-001", "Dinesh Mehta", "+91 98210 33333", "Thane", "Maharashtra"),

        // For PID-DEMO-KA-002
        DealerEntity("DLR-KA-01", "KA-D01", "Bengaluru Adhesive Demo", "PID-DEMO-KA-002", "Naveen Kumar", "+91 98451 44444", "Bengaluru", "Karnataka"),
        DealerEntity("DLR-KA-02", "KA-D02", "Mysuru Timber & Hardware Demo", "PID-DEMO-KA-002", "Raghavendra Hegde", "+91 98452 55555", "Mysuru", "Karnataka"),
        DealerEntity("DLR-KA-03", "KA-D03", "Hubballi Infra Traders Demo", "PID-DEMO-KA-002", "Anand Kulkarni", "+91 98453 66666", "Hubballi", "Karnataka"),

        // For PID-DEMO-GJ-003
        DealerEntity("DLR-GJ-01", "GJ-D01", "Ahmedabad Build Mart Demo", "PID-DEMO-GJ-003", "Chirag Shah", "+91 98791 77777", "Ahmedabad", "Gujarat"),
        DealerEntity("DLR-GJ-02", "GJ-D02", "Surat Chemical & Tools Demo", "PID-DEMO-GJ-003", "Hardik Parekh", "+91 98792 88888", "Surat", "Gujarat"),
        DealerEntity("DLR-GJ-03", "GJ-D03", "Vadodara Supply Center Demo", "PID-DEMO-GJ-003", "Jignesh Joshi", "+91 98793 99999", "Vadodara", "Gujarat"),

        // For PID-DEMO-TN-004
        DealerEntity("DLR-TN-01", "TN-D01", "Chennai Hardware Demo", "PID-DEMO-TN-004", "Karthik Subramanian", "+91 94441 12121", "Chennai", "Tamil Nadu"),
        DealerEntity("DLR-TN-02", "TN-D02", "Coimbatore Buildtech Demo", "PID-DEMO-TN-004", "Suresh Balaji", "+91 94442 23232", "Coimbatore", "Tamil Nadu"),
        DealerEntity("DLR-TN-03", "TN-D03", "Madurai Traders Demo", "PID-DEMO-TN-004", "Selva Kumar", "+91 94443 34343", "Madurai", "Tamil Nadu"),

        // For PID-DEMO-UP-005
        DealerEntity("DLR-UP-01", "UP-D01", "Lucknow Paint & Adhesive Demo", "PID-DEMO-UP-005", "Rameshwar Tiwari", "+91 94151 45454", "Lucknow", "Uttar Pradesh"),
        DealerEntity("DLR-UP-02", "UP-D02", "Kanpur Hardware Store Demo", "PID-DEMO-UP-005", "Vipin Agrawal", "+91 94152 56565", "Kanpur", "Uttar Pradesh"),
        DealerEntity("DLR-UP-03", "UP-D03", "Varanasi Build Supplies Demo", "PID-DEMO-UP-005", "Pramod Mishra", "+91 94153 67676", "Varanasi", "Uttar Pradesh")
    )

    val pricingTiers = listOf(
        PricingTierEntity("TIER-01", null, 1, 49, 0.0),
        PricingTierEntity("TIER-02", null, 50, 99, 3.0),
        PricingTierEntity("TIER-03", null, 100, 249, 7.0),
        PricingTierEntity("TIER-04", null, 250, 499, 10.0),
        PricingTierEntity("TIER-05", null, 500, 99999, 15.0)
    )

    val users = listOf(
        UserEntity("USR-ADM-01", "demo@example.com", "Admin Demo Operator", "ADMIN"),
        UserEntity("USR-SM-01", "sales.manager@example.com", "Sunil Verma", "SALES_MANAGER"),
        UserEntity("USR-DST-01", "dist.west@example.com", "Rajesh Sharma (Western Build)", "DISTRIBUTOR", "PID-DEMO-MH-001"),
        UserEntity("USR-DLR-01", "mumbai.hw@example.com", "Kailash Gada (Mumbai HW)", "DEALER", "PID-DEMO-MH-001", "DLR-MH-01"),
        UserEntity("USR-INV-01", "inventory.mgr@example.com", "Pooja Hegde", "INVENTORY_MANAGER")
    )

    fun generateInitialInventory(): List<InventoryEntity> {
        return products.mapIndexed { index, product ->
            val total = when {
                index < 5 -> 10000
                index < 15 -> 5000
                index < 25 -> 3000
                else -> 1500
            }
            val allocated = (total * 0.45).toInt()
            val used = (allocated * 0.35).toInt()
            val available = total - used
            val reorder = (total * 0.15).toInt()
            val status = when {
                available <= 0 -> "OUT_OF_STOCK"
                available <= reorder / 2 -> "CRITICAL"
                available <= reorder -> "LOW"
                else -> "HEALTHY"
            }
            InventoryEntity(
                productId = product.id,
                totalStock = total,
                allocatedStock = allocated,
                usedStock = used,
                availableStock = available,
                reservedStock = 100,
                reorderLevel = reorder,
                status = status
            )
        }
    }

    fun generateInitialAllocations(): List<InventoryAllocationEntity> {
        val list = mutableListOf<InventoryAllocationEntity>()
        distributors.forEach { dist ->
            products.forEach { prod ->
                list.add(
                    InventoryAllocationEntity(
                        id = "ALLOC-${dist.id}-${prod.id}",
                        distributorId = dist.id,
                        productId = prod.id,
                        allocatedQuantity = 2000,
                        usedQuantity = 700
                    )
                )
            }
        }
        return list
    }

    fun generateInitialOrders(): Pair<List<OrderEntity>, List<OrderItemEntity>> {
        val orders = mutableListOf<OrderEntity>()
        val items = mutableListOf<OrderItemEntity>()
        val now = System.currentTimeMillis()

        // Order 1: Recent Confirmed Bulk Order
        val o1 = OrderEntity(
            id = "ORD-2026-001",
            orderNumber = "PID-B2B-1001",
            distributorId = "PID-DEMO-MH-001",
            dealerId = "DLR-MH-01",
            idempotencyKey = "IDEMP-INIT-001",
            subtotal = 144000.0,
            discountTotal = 10080.0,
            totalAmount = 133920.0,
            status = "CONFIRMED",
            paymentStatus = "APPROVED_CREDIT",
            createdAt = now - (3600 * 1000 * 24 * 2),
            notes = "Bulk monthly replenishment for hardware showroom"
        )
        orders.add(o1)
        items.add(
            OrderItemEntity(
                id = "ITEM-001",
                orderId = o1.id,
                productId = "PROD-01",
                productName = "Fevicol SH Demo Adhesive",
                sku = "PID-FEV-001",
                quantity = 600,
                unitPrice = 240.0,
                discountPercentage = 7.0,
                discountAmount = 10080.0,
                lineTotal = 133920.0,
                pricingTierId = "TIER-03"
            )
        )

        // Order 2: Processing Order
        val o2 = OrderEntity(
            id = "ORD-2026-002",
            orderNumber = "PID-B2B-1002",
            distributorId = "PID-DEMO-MH-001",
            dealerId = "DLR-MH-02",
            idempotencyKey = "IDEMP-INIT-002",
            subtotal = 72000.0,
            discountTotal = 2160.0,
            totalAmount = 69840.0,
            status = "PROCESSING",
            paymentStatus = "APPROVED_CREDIT",
            createdAt = now - (3600 * 1000 * 12),
            notes = "Waterproofing supplies for Pune site distribution"
        )
        orders.add(o2)
        items.add(
            OrderItemEntity(
                id = "ITEM-002",
                orderId = o2.id,
                productId = "PROD-06",
                productName = "Dr. Fixit Demo Waterproofing LW+",
                sku = "PID-DFX-001",
                quantity = 100,
                unitPrice = 720.0,
                discountPercentage = 3.0,
                discountAmount = 2160.0,
                lineTotal = 69840.0,
                pricingTierId = "TIER-02"
            )
        )

        return Pair(orders, items)
    }

    val initialCreditTransactions = listOf(
        CreditTransactionEntity(
            id = "CTX-INIT-001",
            distributorId = "PID-DEMO-MH-001",
            orderId = "ORD-2026-001",
            type = "DEBIT_ORDER",
            amount = 133920.0,
            balanceAfter = 550000.0,
            timestamp = System.currentTimeMillis() - (3600 * 1000 * 24 * 2),
            reason = "Bulk order placement PID-B2B-1001"
        ),
        CreditTransactionEntity(
            id = "CTX-INIT-002",
            distributorId = "PID-DEMO-MH-001",
            orderId = "ORD-2026-002",
            type = "DEBIT_ORDER",
            amount = 69840.0,
            balanceAfter = 550000.0 - 69840.0,
            timestamp = System.currentTimeMillis() - (3600 * 1000 * 12),
            reason = "Order placement PID-B2B-1002"
        )
    )

    val initialAuditLogs = listOf(
        AuditLogEntity(
            id = "AUD-INIT-001",
            action = "SYSTEM_INITIALIZED",
            entityType = "SYSTEM",
            entityId = "ROOT",
            performedBy = "SYSTEM",
            role = "ADMIN",
            timestamp = System.currentTimeMillis() - (3600 * 1000 * 24 * 5),
            details = "Initialized Pidilite-inspired demo B2B distribution database with 30 products and 5 distributors.",
            status = "SUCCESS"
        ),
        AuditLogEntity(
            id = "AUD-INIT-002",
            action = "CREDIT_LIMIT_SET",
            entityType = "DISTRIBUTOR",
            entityId = "PID-DEMO-MH-001",
            performedBy = "ADMIN",
            role = "ADMIN",
            timestamp = System.currentTimeMillis() - (3600 * 1000 * 24 * 4),
            details = "Allocated ₹10,00,000 credit limit to Western Build Supplies Pvt. Ltd.",
            status = "SUCCESS"
        )
    )
}
