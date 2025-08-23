package jp.co.zaico.codingtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import jp.co.zaico.codingtest.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: SecondViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        SecondViewModel.UiState.Initial -> {
                            initView()
                            requireArguments().getString("inventoryId")?.toIntOrNull()?.let { id ->
                                viewModel.getInventory(id)
                            } ?: viewModel.setUiStateError()
                        }

                        SecondViewModel.UiState.Loading -> {
                            // TODO:読み込み中表示
                        }

                        is SecondViewModel.UiState.DataFetched -> {
                            updateView(uiState.data)
                        }

                        is SecondViewModel.UiState.Error -> {
                            // TODO:データ取得エラー表示
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.textViewId.text = ""
        binding.textViewTitle.text = ""
        binding.textViewQuantity.text = ""
    }

    private fun updateView(inventory: Inventory) {
        binding.textViewId.text = inventory.id.toString()
        binding.textViewTitle.text = inventory.title
        binding.textViewQuantity.text = inventory.quantity
    }
}