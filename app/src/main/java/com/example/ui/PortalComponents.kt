package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SimulationBanner() {
    Surface(
        color = Black,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DEMO / EDUCATIONAL SIMULATION — NOT AN OFFICIAL PIDILITE SYSTEM",
                color = PureWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "B2B DEMO V1.0",
                color = LightBorderGray,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun B2BHeader(
    currentRole: String,
    onRoleSelected: (String) -> Unit,
    distributors: List<com.example.data.DistributorEntity>,
    selectedDistributorId: String,
    onDistributorSelected: (String) -> Unit
) {
    Surface(
        color = PureWhite,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorderGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PIDILITE DISTRIBUTOR & DEALER ORDERING PORTAL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Enterprise B2B Distribution & Bulk Ordering Management System",
                        fontSize = 11.sp,
                        color = MediumGray
                    )
                }

                Surface(
                    color = Black,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = currentRole,
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Switcher Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val roles = listOf("DISTRIBUTOR", "ADMIN", "SALES_MANAGER", "INVENTORY_MANAGER", "DEALER")
                roles.forEach { role ->
                    val isSelected = currentRole == role
                    Surface(
                        color = if (isSelected) Black else LightSurfaceGray,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .clickable { onRoleSelected(role) }
                            .border(1.dp, if (isSelected) Black else LightBorderGray, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = role.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PureWhite else DarkCharcoal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Distributor Selector (if not Admin or if viewing distributor context)
            if (distributors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Active Distributor:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    distributors.take(3).forEach { dist ->
                        val isDistSelected = dist.id == selectedDistributorId
                        Surface(
                            color = if (isDistSelected) DarkCharcoal else PureWhite,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier
                                .clickable { onDistributorSelected(dist.id) }
                                .border(1.dp, if (isDistSelected) Black else LightBorderGray, RoundedCornerShape(2.dp))
                        ) {
                            Text(
                                text = dist.code,
                                fontSize = 10.sp,
                                color = if (isDistSelected) PureWhite else DarkCharcoal,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PureWhite,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier.border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = SubtleGray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                fontFamily = FontFamily.Monospace
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, textColor, border) = when (status) {
        "HEALTHY", "CONFIRMED", "DELIVERED", "SUCCESS" -> Triple(LightSurfaceGray, Black, LightBorderGray)
        "LOW", "PROCESSING", "PACKED", "APPROVED_CREDIT" -> Triple(LightSurfaceGray, DarkCharcoal, MediumGray)
        "CRITICAL", "OUT_OF_STOCK", "CANCELLED", "REJECTED", "ROLLBACK_FAILED" -> Triple(Black, PureWhite, Black)
        else -> Triple(OffWhite, DarkCharcoal, LightBorderGray)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier.border(1.dp, border, RoundedCornerShape(2.dp))
    ) {
        Text(
            text = status.replace("_", " "),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TierProgressBar(
    quantity: Int,
    discountPercent: Double,
    nextTierHint: String?
) {
    Surface(
        color = LightSurfaceGray,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorderGray, RoundedCornerShape(4.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BULK TIER PROGRESS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${discountPercent.toInt()}% DISCOUNT UNLOCKED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tier markers bar (1-49: 0%, 50-99: 3%, 100-249: 7%, 250-499: 10%, 500+: 15%)
            val progressFraction = when {
                quantity < 50 -> (quantity / 50f) * 0.2f
                quantity < 100 -> 0.2f + ((quantity - 50) / 50f) * 0.2f
                quantity < 250 -> 0.4f + ((quantity - 100) / 150f) * 0.2f
                quantity < 500 -> 0.6f + ((quantity - 250) / 250f) * 0.2f
                else -> 1.0f
            }.coerceIn(0.02f, 1.0f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(LightBorderGray, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxHeight()
                        .background(Black, RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tier 1 (0%)", fontSize = 9.sp, color = MediumGray)
                Text("Tier 2 (3%)", fontSize = 9.sp, color = MediumGray)
                Text("Tier 3 (7%)", fontSize = 9.sp, color = MediumGray)
                Text("Tier 4 (10%)", fontSize = 9.sp, color = MediumGray)
                Text("Tier 5 (15%)", fontSize = 9.sp, color = MediumGray)
            }

            if (nextTierHint != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚡ $nextTierHint",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black
                )
            }
        }
    }
}
