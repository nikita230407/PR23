package com.example.pr23.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr23.data.MockRepository
import com.example.pr23.model.DeliveryState
import com.example.pr23.model.PaymentMethod
import com.example.pr23.model.RoutePoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val repository: MockRepository = MockRepository()
) : ViewModel() {

    private val _paymentMethods = MutableStateFlow(repository.getPaymentMethods())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(_paymentMethods.value.first())
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _routePoints = MutableStateFlow(repository.getRoutePoints())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints.asStateFlow()

    private val _deliveryState = MutableStateFlow(
        DeliveryState(
            progress = 0,
            statusText = "Доставка готовится к отправке",
            isCompleted = false
        )
    )
    val deliveryState: StateFlow<DeliveryState> = _deliveryState.asStateFlow()

    private var progressJob: Job? = null

    fun selectPaymentMethod(methodId: String) {
        val selected = _paymentMethods.value.firstOrNull { it.id == methodId }
        if (selected != null) {
            _selectedPaymentMethod.value = selected
        }
    }

    fun startDeliveryProgress() {
        if (progressJob?.isActive == true) return

        progressJob = viewModelScope.launch {
            for (progress in 0..100 step 5) {
                _deliveryState.value = DeliveryState(
                    progress = progress,
                    statusText = buildStatusText(progress),
                    isCompleted = progress == 100
                )
                delay(250)
            }
        }
    }

    fun restartDeliveryProgress() {
        progressJob?.cancel()
        progressJob = null
        _deliveryState.value = DeliveryState(
            progress = 0,
            statusText = "Доставка готовится к отправке",
            isCompleted = false
        )
        startDeliveryProgress()
    }

    private fun buildStatusText(progress: Int): String {
        return when (progress) {
            in 0..24 -> "Посылка оформлена"
            in 25..49 -> "Курьер забрал отправление"
            in 50..74 -> "Посылка движется по маршруту"
            in 75..99 -> "Курьер рядом с получателем"
            else -> "Доставка успешно завершена"
        }
    }
}
