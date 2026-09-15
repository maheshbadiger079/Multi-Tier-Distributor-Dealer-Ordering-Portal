package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(amount).replace("INR", "₹")
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// -------------------------------------------------------------
// 1. DISTRIBUTOR DASHBOARD
// -------------------------------------------------------------
@Composable
fun DistributorDashboardView(
    viewModel: PortalViewModel,
    distributor: DistributorEntity?,
    dealers: List<DealerEntity>,
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    allocations: List<InventoryAllocationEntity>
) {
    val creditLimit = distributor?.creditLimit ?: 0.0
    val creditUsed = distributor?.creditUsed ?: 0.0
    val availableCredit = (creditLimit - creditUsed).coerceAtLeast(0.0)
    val pendingOrders = orders.count { it.status == "PENDING" || it.status == "PROCESSING" }
    val totalOrdersVal = orders.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Enterprise Distributor Info Card
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = distributor?.name ?: "Loading Distributor...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                            Text(
                                text = "Code: ${distributor?.code ?: "-"} | Region: ${distributor?.region ?: "-"} | State: ${distributor?.state ?: "-"}",
                                fontSize = 11.sp,
                                color = MediumGray,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Contact: ${distributor?.contactPerson ?: "-"} (${distributor?.phone ?: "-"})",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        }
                        StatusBadge(status = "ACTIVE")
                    }
                }
            }
        }

        // Financial KPIs Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Credit Limit",
                    value = formatCurrency(creditLimit),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Credit Used",
                    value = formatCurrency(creditUsed),
                    subtitle = "${((creditUsed / creditLimit.coerceAtLeast(1.0)) * 100).toInt()}% utilized",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Available Credit",
                    value = formatCurrency(availableCredit),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Operational KPIs Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Active Dealers",
                    value = dealers.size.toString(),
                    subtitle = "Assigned network",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Orders",
                    value = orders.size.toString(),
                    subtitle = formatCurrency(totalOrdersVal),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pending Orders",
                    value = pendingOrders.toString(),
                    subtitle = "Fulfillment queue",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Enterprise Actions
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setActiveTab(PortalTab.PRODUCTS) },
                            colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Catalog", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.setActiveTab(PortalTab.CALCULATOR) },
                            colors = ButtonDefaults.buttonColors(containerColor = LightSurfaceGray, contentColor = Black),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                        ) {
                            Text("Bulk Calc", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.setActiveTab(PortalTab.ROLLBACK_TEST) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal, contentColor = PureWhite),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rollback Test", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recent Orders Table
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT ORDERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Text(
                            text = "View All →",
                            fontSize = 11.sp,
                            color = MediumGray,
                            modifier = Modifier.clickable { viewModel.setActiveTab(PortalTab.ORDERS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (orders.isEmpty()) {
                        Text("No orders placed yet.", fontSize = 12.sp, color = MediumGray)
                    } else {
                        orders.take(3).forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .border(0.5.dp, LightBorderGray, RoundedCornerShape(2.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "#${order.orderNumber}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Black
                                    )
                                    Text(
                                        text = formatDate(order.createdAt),
                                        fontSize = 10.sp,
                                        color = MediumGray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatCurrency(order.totalAmount),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Black
                                    )
                                    StatusBadge(status = order.status)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top Products Preview
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "FEATURED DEMO PRODUCTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    products.take(4).forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Black)
                                Text("SKU: ${prod.sku} | MOQ: ${prod.moq} | ${prod.packSize}", fontSize = 10.sp, color = MediumGray)
                            }
                            Text(formatCurrency(prod.basePrice), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Divider(color = LightBorderGray, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. PRODUCT CATALOG
// -------------------------------------------------------------
@Composable
fun ProductCatalogView(
    viewModel: PortalViewModel,
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    pricingTiers: List<PricingTierEntity>
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val cart by viewModel.cart.collectAsState()

    val filteredProducts = remember(products, searchQuery, selectedCategory, sortOption) {
        var list = products.filter { prod ->
            val matchesSearch = prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.sku.contains(searchQuery, ignoreCase = true) ||
                    prod.category.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || prod.category == selectedCategory
            matchesSearch && matchesCategory
        }
        when (sortOption) {
            "PRICE_ASC" -> list = list.sortedBy { it.basePrice }
            "PRICE_DESC" -> list = list.sortedByDescending { it.basePrice }
            "NAME" -> list = list.sortedBy { it.name }
            else -> list = list.sortedBy { it.name }
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by product name, SKU or category...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MediumGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Black,
                unfocusedBorderColor = LightBorderGray
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isAll = selectedCategory == null
                Surface(
                    color = if (isAll) Black else LightSurfaceGray,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clickable { viewModel.setSelectedCategory(null) }
                        .border(1.dp, if (isAll) Black else LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = "ALL",
                        fontSize = 11.sp,
                        fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                        color = if (isAll) PureWhite else DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
            items(categories) { cat ->
                val isSelected = selectedCategory == cat.name
                Surface(
                    color = if (isSelected) Black else LightSurfaceGray,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clickable { viewModel.setSelectedCategory(cat.name) }
                        .border(1.dp, if (isSelected) Black else LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = cat.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results count and Cart summary trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} DEMO PRODUCTS FOUND",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MediumGray,
                fontFamily = FontFamily.Monospace
            )
            if (cart.isNotEmpty()) {
                Surface(
                    color = Black,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.clickable { viewModel.setActiveTab(PortalTab.CART_ORDER) }
                ) {
                    Text(
                        text = "🛒 CART (${cart.values.sum()} UNITS)",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product Cards List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProducts) { product ->
                ProductCardItem(
                    product = product,
                    inCartQuantity = cart[product.id] ?: 0,
                    onAddToCart = { qty -> viewModel.addToCart(product.id, qty) },
                    onOpenCalculator = {
                        viewModel.setCalcProductId(product.id)
                        viewModel.setCalcQuantity(product.moq.coerceAtLeast(50))
                        viewModel.setActiveTab(PortalTab.CALCULATOR)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductCardItem(
    product: ProductEntity,
    inCartQuantity: Int,
    onAddToCart: (Int) -> Unit,
    onOpenCalculator: () -> Unit
) {
    var orderQty by remember { mutableIntStateOf(product.moq) }

    Surface(
        color = PureWhite,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${product.category} • SKU: ${product.sku} • Pack: ${product.packSize}",
                        fontSize = 11.sp,
                        color = MediumGray
                    )
                    Text(
                        text = product.description,
                        fontSize = 11.sp,
                        color = DarkCharcoal
                    )
                }

                Surface(
                    color = LightSurfaceGray,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.border(1.dp, LightBorderGray, RoundedCornerShape(2.dp))
                ) {
                    Text(
                        text = "DEMO PRODUCT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & MOQ details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BASE PRICE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray
                    )
                    Text(
                        text = formatCurrency(product.basePrice),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Black
                    )
                }
                Column {
                    Text(
                        text = "MIN ORDER (MOQ)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray
                    )
                    Text(
                        text = "${product.moq} units",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }
                Column {
                    Text(
                        text = "MAX BULK DISCOUNT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray
                    )
                    Text(
                        text = "Up to 15%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quantity selector and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (orderQty > product.moq) orderQty -= 5 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "$orderQty",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { orderQty += 10 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { onAddToCart(orderQty) },
                    colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (inCartQuantity > 0) "Add More ($inCartQuantity in cart)" else "Add to Order",
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onOpenCalculator,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Black),
                    modifier = Modifier.border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Text("Calc", fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. REAL-TIME BULK PRICING CALCULATOR (Section 15)
// -------------------------------------------------------------
@Composable
fun BulkCalculatorView(
    viewModel: PortalViewModel,
    products: List<ProductEntity>,
    tiers: List<PricingTierEntity>
) {
    val selectedProdId by viewModel.calcProductId.collectAsState()
    val quantity by viewModel.calcQuantity.collectAsState()

    val selectedProduct = products.find { it.id == selectedProdId } ?: products.firstOrNull()

    val pricing = remember(selectedProduct, quantity, tiers) {
        if (selectedProduct != null) {
            val sortedTiers = tiers.sortedBy { it.minQty }
            var discount = 0.0
            var nextTier: PricingTierEntity? = null

            for (t in sortedTiers) {
                if (quantity >= t.minQty && (quantity <= t.maxQty || t.maxQty == 0)) {
                    discount = t.discountPercentage
                }
            }
            for (t in sortedTiers) {
                if (t.minQty > quantity) {
                    nextTier = t
                    break
                }
            }
            val discUnit = selectedProduct.basePrice * (1.0 - (discount / 100.0))
            val total = discUnit * quantity
            val savings = (selectedProduct.basePrice * quantity) - total
            val hint = if (nextTier != null) {
                val more = nextTier.minQty - quantity
                "Add $more more units to unlock the ${nextTier.discountPercentage.toInt()}% pricing tier."
            } else {
                "Maximum 15% tier unlocked!"
            }
            Triple(discount, total, hint to (savings to discUnit))
        } else {
            Triple(0.0, 0.0, "" to (0.0 to 0.0))
        }
    }

    val currentDiscount = pricing.first
    val totalAmount = pricing.second
    val nextTierHint = pricing.third.first
    val totalSavings = pricing.third.second.first
    val discountedUnitPrice = pricing.third.second.second

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "REAL-TIME BULK PRICING CALCULATOR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    Text(
                        text = "Simulates tiered volume discounts stored in database rules (Section 14 & 15).",
                        fontSize = 11.sp,
                        color = MediumGray
                    )
                }
            }
        }

        // Product Selector
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("SELECT PRODUCT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(products.take(8)) { prod ->
                            val isSelected = prod.id == selectedProdId
                            Surface(
                                color = if (isSelected) Black else LightSurfaceGray,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .clickable { viewModel.setCalcProductId(prod.id) }
                                    .border(1.dp, if (isSelected) Black else LightBorderGray, RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    text = prod.name.take(18) + "...",
                                    fontSize = 11.sp,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quantity Stepper & Direct Input
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ORDER QUANTITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quantity.toString(),
                            onValueChange = { str ->
                                val n = str.filter { it.isDigit() }.toIntOrNull() ?: 1
                                viewModel.setCalcQuantity(n)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Units") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Black,
                                unfocusedBorderColor = LightBorderGray
                            )
                        )

                        // Quick buttons
                        listOf(40, 60, 120, 260, 520).forEach { preset ->
                            Surface(
                                color = if (quantity == preset) Black else LightSurfaceGray,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .clickable { viewModel.setCalcQuantity(preset) }
                                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    text = "$preset",
                                    fontSize = 11.sp,
                                    color = if (quantity == preset) PureWhite else DarkCharcoal,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Bulk Tier Progress
        item {
            TierProgressBar(
                quantity = quantity,
                discountPercent = currentDiscount,
                nextTierHint = nextTierHint
            )
        }

        // Calculation Breakdown Metrics
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("CALCULATED SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Base Unit Price:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(selectedProduct?.basePrice ?: 0.0), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bulk Discount Tier:", fontSize = 12.sp, color = MediumGray)
                        Text("${currentDiscount.toInt()}% Off", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Effective Unit Price:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(discountedUnitPrice), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Volume Savings:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(totalSavings), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = LightBorderGray)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("FINAL TOTAL:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Black)
                        Text(formatCurrency(totalAmount), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace, color = Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            selectedProduct?.let {
                                viewModel.addToCart(it.id, quantity)
                                viewModel.setActiveTab(PortalTab.CART_ORDER)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add $quantity units to Cart →", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. CART & ORDER CONFIRMATION
// -------------------------------------------------------------
@Composable
fun CartAndOrderView(
    viewModel: PortalViewModel,
    cartItems: List<CartItemDetail>,
    distributor: DistributorEntity?,
    dealers: List<DealerEntity>,
    selectedDealerId: String
) {
    val creditLimit = distributor?.creditLimit ?: 0.0
    val creditUsed = distributor?.creditUsed ?: 0.0
    val availableCredit = (creditLimit - creditUsed).coerceAtLeast(0.0)

    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val totalAmount = cartItems.sumOf { it.lineTotal }
    val totalSavings = subtotal - totalAmount

    val hasInsufficientCredit = totalAmount > availableCredit

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("SELECT RECIPIENT DEALER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (dealers.isEmpty()) {
                        Text("No dealers assigned to this distributor.", fontSize = 12.sp, color = MediumGray)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(dealers) { dealer ->
                                val isSelected = dealer.id == selectedDealerId
                                Surface(
                                    color = if (isSelected) Black else LightSurfaceGray,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .clickable { viewModel.selectDealer(dealer.id) }
                                        .border(1.dp, if (isSelected) Black else LightBorderGray, RoundedCornerShape(4.dp))
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                        Text(
                                            text = dealer.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PureWhite else DarkCharcoal
                                        )
                                        Text(
                                            text = "${dealer.city}, ${dealer.state}",
                                            fontSize = 9.sp,
                                            color = if (isSelected) LightBorderGray else MediumGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cart Items List
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ORDER ITEMS (${cartItems.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                        if (cartItems.isNotEmpty()) {
                            Text(
                                text = "Clear All",
                                fontSize = 11.sp,
                                color = MediumGray,
                                modifier = Modifier.clickable { viewModel.clearCart() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (cartItems.isEmpty()) {
                        Text(
                            text = "Cart is empty. Browse the catalog to add items.",
                            fontSize = 12.sp,
                            color = MediumGray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        cartItems.forEach { item ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
                                        Text(
                                            "SKU: ${item.product.sku} | Base: ${formatCurrency(item.unitPrice)} | Tier: ${item.discountPercent.toInt()}% Off",
                                            fontSize = 10.sp,
                                            color = MediumGray
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item.product.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = MediumGray)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Qty: ", fontSize = 11.sp, color = MediumGray)
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 5) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("-", fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            "${item.quantity}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity + 5) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("+", fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(formatCurrency(item.lineTotal), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        if (item.savings > 0) {
                                            Text("Saved ${formatCurrency(item.savings)}", fontSize = 10.sp, color = MediumGray)
                                        }
                                    }
                                }
                                Divider(color = LightBorderGray, thickness = 0.5.dp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }

        // Credit Verification Card
        item {
            Surface(
                color = if (hasInsufficientCredit) LightSurfaceGray else PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (hasInsufficientCredit) Black else LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("CREDIT VALIDATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                        StatusBadge(status = if (hasInsufficientCredit) "INSUFFICIENT_CREDIT" else "CREDIT_APPROVED")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Credit Limit:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(creditLimit), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Currently Used Credit:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(creditUsed), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Available Credit:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(availableCredit), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    if (hasInsufficientCredit) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠ Order total exceeds available credit. Order will be blocked by backend credit rule.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                    }
                }
            }
        }

        // Order Summary & Submission
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ORDER TOTALS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:", fontSize = 12.sp, color = MediumGray)
                        Text(formatCurrency(subtotal), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bulk Tier Discounts:", fontSize = 12.sp, color = MediumGray)
                        Text("-${formatCurrency(totalSavings)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = LightBorderGray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NET ORDER AMOUNT:", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Black)
                        Text(formatCurrency(totalAmount), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace, color = Black)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.placeOrder() },
                        enabled = cartItems.isNotEmpty() && !hasInsufficientCredit,
                        colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Place B2B Order (Atomic Transaction) →", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ORDER HISTORY & STATUS TIMELINE
// -------------------------------------------------------------
@Composable
fun OrderHistoryView(
    viewModel: PortalViewModel,
    orders: List<OrderEntity>,
    role: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ORDER TRACKING & FULFILLMENT HISTORY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text("Lifecycle: PENDING → CONFIRMED → PROCESSING → PACKED → SHIPPED → DELIVERED", fontSize = 10.sp, color = MediumGray, fontFamily = FontFamily.Monospace)
                }
            }
        }

        if (orders.isEmpty()) {
            item {
                Text("No orders recorded yet.", fontSize = 12.sp, color = MediumGray)
            }
        } else {
            items(orders) { order ->
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("#${order.orderNumber}", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Black)
                                Text("Distributor: ${order.distributorId} • Dealer: ${order.dealerId}", fontSize = 10.sp, color = MediumGray)
                                Text("Placed: ${formatDate(order.createdAt)}", fontSize = 10.sp, color = MediumGray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                StatusBadge(status = order.status)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(formatCurrency(order.totalAmount), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Progression Buttons (Admins / Managers)
                        if (order.status != "DELIVERED" && order.status != "CANCELLED") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val nextStatus = when (order.status) {
                                    "PENDING" -> "CONFIRMED"
                                    "CONFIRMED" -> "PROCESSING"
                                    "PROCESSING" -> "PACKED"
                                    "PACKED" -> "SHIPPED"
                                    "SHIPPED" -> "DELIVERED"
                                    else -> null
                                }
                                if (nextStatus != null) {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, nextStatus) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark $nextStatus", fontSize = 10.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.cancelOrder(order.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Black),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.border(1.dp, LightBorderGray, RoundedCornerShape(2.dp))
                                ) {
                                    Text("Cancel & Refund", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. INVENTORY MANAGEMENT (Section 16 & 17)
// -------------------------------------------------------------
@Composable
fun InventoryView(
    viewModel: PortalViewModel,
    inventory: List<InventoryEntity>,
    products: List<ProductEntity>,
    allocations: List<InventoryAllocationEntity>,
    distributorId: String
) {
    val prodMap = remember(products) { products.associateBy { it.id } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CENTRAL & ALLOCATED INVENTORY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text("Tracks Total, Allocated, Used, and Available stock with multi-tier reorder levels.", fontSize = 11.sp, color = MediumGray)
                }
            }
        }

        items(inventory) { item ->
            val product = prodMap[item.productId]
            val alloc = allocations.find { it.productId == item.productId && it.distributorId == distributorId }

            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(product?.name ?: item.productId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
                            Text("SKU: ${product?.sku ?: "-"} | Category: ${product?.category ?: "-"}", fontSize = 10.sp, color = MediumGray)
                        }
                        StatusBadge(status = item.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOTAL STOCK", fontSize = 9.sp, color = SubtleGray)
                            Text("${item.totalStock}", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("AVAILABLE", fontSize = 9.sp, color = SubtleGray)
                            Text("${item.availableStock}", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("USED", fontSize = 9.sp, color = SubtleGray)
                            Text("${item.usedStock}", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("DIST ALLOC", fontSize = 9.sp, color = SubtleGray)
                            Text(
                                if (alloc != null) "${alloc.allocatedQuantity - alloc.usedQuantity} / ${alloc.allocatedQuantity}" else "N/A",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. CREDIT MANAGEMENT (Section 18)
// -------------------------------------------------------------
@Composable
fun CreditView(
    viewModel: PortalViewModel,
    distributor: DistributorEntity?,
    transactions: List<CreditTransactionEntity>
) {
    val limit = distributor?.creditLimit ?: 0.0
    val used = distributor?.creditUsed ?: 0.0
    val available = (limit - used).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("DISTRIBUTOR CREDIT FACILITY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricCard(title = "Credit Limit", value = formatCurrency(limit), modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(6.dp))
                        MetricCard(title = "Credit Used", value = formatCurrency(used), modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(6.dp))
                        MetricCard(title = "Available", value = formatCurrency(available), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Text("CREDIT TRANSACTIONS AUDIT LEDGER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
        }

        if (transactions.isEmpty()) {
            item {
                Text("No credit transactions recorded.", fontSize = 12.sp, color = MediumGray)
            }
        } else {
            items(transactions) { tx ->
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.reason, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Black)
                            Text(formatDate(tx.timestamp), fontSize = 10.sp, color = MediumGray)
                            Text("Balance After: ${formatCurrency(tx.balanceAfter)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = DarkCharcoal)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (tx.type.startsWith("DEBIT")) "-" else "+") + formatCurrency(tx.amount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Black
                            )
                            StatusBadge(status = tx.type)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 8. TRANSACTION ROLLBACK TEST (Section 35)
// -------------------------------------------------------------
@Composable
fun RollbackTestView(
    viewModel: PortalViewModel,
    rollbackState: RollbackTestState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SECTION 35: TRANSACTION ROLLBACK TEST", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Demonstrates ACID atomicity in the ordering transaction. Executes a valid order, then intentionally injects a failure during an order attempt. Asserts that the database rolls back completely with zero corrupted inventory, zero phantom orders, and zero credit leakage.",
                        fontSize = 11.sp,
                        color = MediumGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.runRollbackTest() },
                        enabled = !rollbackState.isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (rollbackState.isRunning) "Running Atomicity Verification..." else "Run Section 35 Rollback Test →", fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Surface(
                color = LightSurfaceGray,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("TEST EXECUTION LOG", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rollbackState.step,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Black
                    )
                    if (rollbackState.message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rollbackState.message,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rollbackState.isSuccess == true) Black else DarkCharcoal
                        )
                    }
                }
            }
        }

        // Test Assertion Results Table
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("DATABASE STATE COMPARISON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Metric", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkCharcoal)
                        Text("Pre-Fail State", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkCharcoal)
                        Text("Post-Rollback State", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkCharcoal)
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = LightBorderGray)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Available Stock:", fontSize = 11.sp, color = MediumGray)
                        Text("${rollbackState.afterSuccessStock}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("${rollbackState.afterRollbackStock}", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Credit Used:", fontSize = 11.sp, color = MediumGray)
                        Text(formatCurrency(rollbackState.afterSuccessCreditUsed), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(formatCurrency(rollbackState.afterRollbackCreditUsed), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Integrity Result:", fontSize = 11.sp, color = MediumGray)
                        Text("-", fontSize = 11.sp)
                        StatusBadge(status = if (rollbackState.isSuccess == true) "PASSED_ATOMIC" else if (rollbackState.isSuccess == false) "FAILED" else "IDLE")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. ADMIN PANEL (Section 25-28)
// -------------------------------------------------------------
@Composable
fun AdminPanelView(
    viewModel: PortalViewModel,
    distributors: List<DistributorEntity>,
    products: List<ProductEntity>,
    inventory: List<InventoryEntity>
) {
    var selectedDistForCredit by remember { mutableStateOf(distributors.firstOrNull()?.id ?: "") }
    var newCreditLimitInput by remember { mutableStateOf("1200000") }
    var selectedProdForStock by remember { mutableStateOf(products.firstOrNull()?.id ?: "") }
    var addStockInput by remember { mutableStateOf("500") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ADMINISTRATIVE MANAGEMENT CONSOLE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Black)
                    Text("Configure distributor credit limits, central stock adjustments, and pricing rules.", fontSize = 11.sp, color = MediumGray)
                }
            }
        }

        // Credit Limit Adjustment Form
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("DISTRIBUTOR CREDIT LIMIT MANAGEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(distributors) { dist ->
                            val isSelected = dist.id == selectedDistForCredit
                            Surface(
                                color = if (isSelected) Black else LightSurfaceGray,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .clickable {
                                        selectedDistForCredit = dist.id
                                        newCreditLimitInput = dist.creditLimit.toInt().toString()
                                    }
                                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    dist.code,
                                    fontSize = 11.sp,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCreditLimitInput,
                            onValueChange = { newCreditLimitInput = it },
                            label = { Text("New Credit Limit (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Button(
                            onClick = {
                                val limit = newCreditLimitInput.toDoubleOrNull() ?: 1000000.0
                                viewModel.adjustCreditLimit(selectedDistForCredit, limit)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Update", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Inventory Restock Form
        item {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CENTRAL STOCK RESTOCK & INJECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(products.take(6)) { prod ->
                            val isSelected = prod.id == selectedProdForStock
                            Surface(
                                color = if (isSelected) Black else LightSurfaceGray,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .clickable { selectedProdForStock = prod.id }
                                    .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    prod.name.take(15) + "...",
                                    fontSize = 11.sp,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = addStockInput,
                            onValueChange = { addStockInput = it },
                            label = { Text("Units to Add") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Button(
                            onClick = {
                                val units = addStockInput.toIntOrNull() ?: 100
                                viewModel.adjustStock(selectedProdForStock, units)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = PureWhite),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Restock", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 10. REPORTS & AUDIT LOGS (Section 39 & Audit)
// -------------------------------------------------------------
@Composable
fun ReportsAndAuditView(
    viewModel: PortalViewModel,
    orders: List<OrderEntity>,
    auditLogs: List<AuditLogEntity>,
    stockMovements: List<StockMovementEntity>
) {
    var selectedSubTab by remember { mutableStateOf("AUDIT_LOGS") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("AUDIT_LOGS", "STOCK_MOVEMENTS", "SALES_SUMMARY").forEach { tab ->
                val isSelected = selectedSubTab == tab
                Surface(
                    color = if (isSelected) Black else LightSurfaceGray,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clickable { selectedSubTab = tab }
                        .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = tab.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureWhite else DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSubTab) {
            "AUDIT_LOGS" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(auditLogs) { log ->
                        Surface(
                            color = PureWhite,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Black, fontFamily = FontFamily.Monospace)
                                    StatusBadge(status = log.status)
                                }
                                Text(log.details, fontSize = 11.sp, color = DarkCharcoal)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Role: ${log.role} | By: ${log.performedBy} | ${formatDate(log.timestamp)}",
                                    fontSize = 9.sp,
                                    color = MediumGray
                                )
                            }
                        }
                    }
                }
            }
            "STOCK_MOVEMENTS" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(stockMovements) { sm ->
                        Surface(
                            color = PureWhite,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(sm.note, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Black)
                                    Text("Product: ${sm.productId} | ${formatDate(sm.timestamp)}", fontSize = 10.sp, color = MediumGray)
                                }
                                StatusBadge(status = "${sm.movementType} (${sm.quantity})")
                            }
                        }
                    }
                }
            }
            "SALES_SUMMARY" -> {
                val totalSales = orders.sumOf { it.totalAmount }
                val totalDiscount = orders.sumOf { it.discountTotal }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(title = "Total Gross Revenue", value = formatCurrency(totalSales + totalDiscount))
                    MetricCard(title = "Total Bulk Discounts Granted", value = formatCurrency(totalDiscount))
                    MetricCard(title = "Net Revenue", value = formatCurrency(totalSales))
                    MetricCard(title = "Total Orders Completed", value = orders.size.toString())
                }
            }
        }
    }
}
