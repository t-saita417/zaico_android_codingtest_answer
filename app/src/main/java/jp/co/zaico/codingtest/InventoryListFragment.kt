package jp.co.zaico.codingtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.co.zaico.codingtest.core.model.Inventory
import jp.co.zaico.codingtest.databinding.FragmentInventoryListBinding
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * 在庫一覧画面のFragment
 */
@AndroidEntryPoint
class InventoryListFragment : Fragment() {

    private var _binding: FragmentInventoryListBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: InventoryListViewModel by viewModels()

    private val adapter = MyAdapter(object : MyAdapter.OnItemClickListener {
        override fun itemClick(item: Inventory) {
            val bundle = bundleOf("inventoryId" to item.id.toString())
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // TODO:詳細画面からの戻り時にデータ取得できるようInitialに戻す。毎回取るのも微妙なのでキャッシュで表示するなど後で検討する
        viewModel.setUiStateInitial()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * UiStateを監視
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        InventoryListViewModel.UiState.Initial -> {
                            binding.progress.visibility = View.GONE
                            initView()
                            viewModel.getInventories()
                        }

                        InventoryListViewModel.UiState.Loading -> {
                            binding.progress.visibility = View.VISIBLE
                        }

                        is InventoryListViewModel.UiState.DataFetched -> {
                            binding.progress.visibility = View.GONE
                            adapter.submitList(uiState.data)
                        }

                        is InventoryListViewModel.UiState.Error -> {
                            binding.progress.visibility = View.GONE
                            // TODO:全画面エラーからのPullToRefreshでリトライなどが適当？仮でToast出しておく
                            Toast.makeText(requireContext(), "情報の取得に失敗しました ${uiState.e.text}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Viewの初期化
     */
    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            layoutManager.orientation
        )
        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }
    }
}

val diff_util = object : DiffUtil.ItemCallback<Inventory>() {
    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem == newItem
    }

}

class MyAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<Inventory, MyAdapter.ViewHolder>(diff_util) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun itemClick(item: Inventory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val _view = LayoutInflater.from(parent.context)
            .inflate(R.layout.first_item, parent, false)
        return ViewHolder(_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val _item = getItem(position)
        (holder.itemView.findViewById<View>(R.id.textView_id) as TextView).text = _item.id.toString()
        (holder.itemView.findViewById<View>(R.id.textView_title) as TextView).text = _item.title

        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(_item)
        }
    }
}