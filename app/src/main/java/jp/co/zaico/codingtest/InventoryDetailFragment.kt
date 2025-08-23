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
import jp.co.zaico.codingtest.databinding.FragmentInventoryDetailBinding
import kotlinx.coroutines.launch

/**
 * 在庫詳細画面のFragment
 */
@AndroidEntryPoint
class InventoryDetailFragment : Fragment() {

    private var _binding: FragmentInventoryDetailBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: InventoryDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryDetailBinding.inflate(inflater, container, false)
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
                        InventoryDetailViewModel.UiState.Initial -> {
                            initView()
                            requireArguments().getString("inventoryId")?.toIntOrNull()?.let { id ->
                                viewModel.getInventory(id)
                            } ?: viewModel.setUiStateError()
                        }

                        InventoryDetailViewModel.UiState.Loading -> {
                            // TODO:読み込み中表示
                        }

                        is InventoryDetailViewModel.UiState.DataFetched -> {
                            updateView(uiState.data)
                        }

                        is InventoryDetailViewModel.UiState.Error -> {
                            // TODO:データ取得エラー表示
                        }
                    }
                }
            }
        }
    }

    /**
     * 初期表示
     */
    private fun initView() {
        binding.textViewId.text = ""
        binding.textViewTitle.text = ""
        binding.textViewQuantity.text = ""
    }

    /**
     * 表示を更新
     * @param inventory 在庫データ
     */
    private fun updateView(inventory: Inventory) {
        binding.textViewId.text = inventory.id.toString()
        binding.textViewTitle.text = inventory.title
        binding.textViewQuantity.text = inventory.quantity
    }
}