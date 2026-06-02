package com.example.pr23.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pr23.R
import com.example.pr23.databinding.FragmentAddPaymentBinding
import com.example.pr23.model.PaymentMethod
import com.example.pr23.utils.collectWhenStarted
import com.example.pr23.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class AddPaymentFragment : Fragment() {

    private var _binding: FragmentAddPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.paymentRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.cardRadioButton -> viewModel.selectPaymentMethod("card")
                R.id.cashRadioButton -> viewModel.selectPaymentMethod("cash")
                R.id.walletRadioButton -> viewModel.selectPaymentMethod("wallet")
            }
        }

        binding.backToWalletButton.setOnClickListener {
            val navController = findNavController()
            val returnedByBackStack = navController.popBackStack(R.id.walletFragment, false)
            if (!returnedByBackStack) {
                navController.navigate(R.id.walletFragment)
            }
        }

        collectWhenStarted {
            launch {
                viewModel.paymentMethods.collect { methods ->
                    renderDescriptions(methods)
                }
            }
            launch {
                viewModel.selectedPaymentMethod.collect { method ->
                    checkSelectedMethod(method.id)
                    binding.selectedPaymentText.text = getString(
                        R.string.selected_payment_format,
                        method.title
                    )
                }
            }
        }
    }

    private fun renderDescriptions(methods: List<PaymentMethod>) {
        val methodsById = methods.associateBy { it.id }
        binding.cardDescriptionText.text = methodsById["card"]?.description.orEmpty()
        binding.cashDescriptionText.text = methodsById["cash"]?.description.orEmpty()
        binding.walletDescriptionText.text = methodsById["wallet"]?.description.orEmpty()
    }

    private fun checkSelectedMethod(methodId: String) {
        val button: RadioButton = when (methodId) {
            "cash" -> binding.cashRadioButton
            "wallet" -> binding.walletRadioButton
            else -> binding.cardRadioButton
        }
        if (!button.isChecked) {
            button.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
