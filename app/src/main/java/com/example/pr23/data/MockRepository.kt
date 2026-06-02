package com.example.pr23.data

import com.example.pr23.model.PaymentMethod
import com.example.pr23.model.RoutePoint

class MockRepository {

    // Репозиторий имитирует слой данных. UI не знает, откуда пришли данные:
    // из mock-списка, базы данных или сети. Это сохраняет MVVM-разделение.
    fun getPaymentMethods(): List<PaymentMethod> = listOf(
        PaymentMethod(
            id = PAYMENT_CARD,
            title = "Банковская карта",
            description = "Visa, MasterCard или Мир"
        ),
        PaymentMethod(
            id = PAYMENT_CASH,
            title = "Наличные",
            description = "Оплата курьеру при получении"
        ),
        PaymentMethod(
            id = PAYMENT_WALLET,
            title = "Delivery Wallet",
            description = "Оплата с баланса кошелька"
        )
    )

    fun getRoutePoints(): List<RoutePoint> = listOf(
        RoutePoint(55.030204, 82.920430, "Склад"),
        RoutePoint(55.035600, 82.934900, "Сортировочный центр"),
        RoutePoint(55.041500, 82.948300, "Курьер в пути"),
        RoutePoint(55.050100, 82.966700, "Адрес доставки")
    )

    companion object {
        const val PAYMENT_CARD = "card"
        const val PAYMENT_CASH = "cash"
        const val PAYMENT_WALLET = "wallet"
    }
}
