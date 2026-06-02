package com.example.pr23.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pr23.R
import com.example.pr23.databinding.FragmentWalletBinding
import com.example.pr23.utils.collectWhenStarted
import com.example.pr23.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPaymentButton.setOnClickListener {
            findNavController().navigate(R.id.addPaymentFragment)
        }
        binding.trackPackageButton.setOnClickListener {
            findNavController().navigate(R.id.trackingFragment)
        }
        binding.deliveryStatusButton.setOnClickListener {
            findNavController().navigate(R.id.deliveryFragment)
        }

        collectWhenStarted {
            launch {
                viewModel.selectedPaymentMethod.collect { method ->
                    binding.paymentMethodText.text = method.title
                    binding.paymentDescriptionText.text = method.description
                }
            }
            launch {
                viewModel.deliveryState.collect { state ->
                    binding.deliveryProgressText.text = "${state.progress}%"
                    binding.deliveryStatusText.text = state.statusText
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
