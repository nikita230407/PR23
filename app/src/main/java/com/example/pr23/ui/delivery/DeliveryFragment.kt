package com.example.pr23.ui.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pr23.R
import com.example.pr23.databinding.FragmentDeliveryBinding
import com.example.pr23.utils.collectWhenStarted
import com.example.pr23.viewmodel.SharedViewModel

class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View сообщает о пользовательском действии, ViewModel меняет StateFlow,
        // а Fragment ниже только перерисовывает ProgressBar и текст по новому состоянию.
        binding.restartProgressButton.setOnClickListener {
            viewModel.restartDeliveryProgress()
        }

        viewModel.startDeliveryProgress()

        collectWhenStarted {
            viewModel.deliveryState.collect { state ->
                binding.deliveryProgressBar.progress = state.progress
                binding.progressValueText.text = getString(
                    R.string.delivery_progress_format,
                    state.progress
                )
                binding.deliveryStatusText.text = state.statusText
                binding.successTitleText.text = if (state.isCompleted) {
                    getString(R.string.delivery_success_title)
                } else {
                    getString(R.string.delivery_in_progress_title)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
