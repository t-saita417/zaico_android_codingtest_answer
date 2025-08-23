package jp.co.zaico.codingtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import jp.co.zaico.codingtest.core.model.Inventory
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
                            binding.progress.visibility = View.GONE
                            initView()
                            requireArguments().getString("inventoryId")?.toIntOrNull()?.let { id ->
                                viewModel.getInventory(id)
                            } ?: viewModel.setUiStateError()
                        }

                        InventoryDetailViewModel.UiState.Loading -> {
                            binding.progress.visibility = View.VISIBLE
                        }

                        is InventoryDetailViewModel.UiState.DataFetched -> {
                            binding.progress.visibility = View.GONE
                            updateView(uiState.data)
                        }

                        is InventoryDetailViewModel.UiState.Error -> {
                            binding.progress.visibility = View.GONE
                            // TODO:全画面エラーからのPullToRefreshでリトライなどが適当？仮でToast出しておく
                            Toast.makeText(requireContext(), "情報の取得に失敗しました ${uiState.e}", Toast.LENGTH_LONG).show()
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