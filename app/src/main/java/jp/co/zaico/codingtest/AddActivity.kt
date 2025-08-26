package jp.co.zaico.codingtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import jp.co.zaico.codingtest.core.model.AddInventoryRequest
import jp.co.zaico.codingtest.databinding.ActivityAddBinding
import kotlinx.coroutines.launch

/**
 * 在庫データ追加画面のActivity
 */
@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding

    private val viewModel: AddViewModel by viewModels()

    companion object {
        fun createIntent(context: Context) = Intent(context, AddActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
    }

    /**
     * UiStateを監視
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        when (uiState) {
                            AddViewModel.UiState.Initial -> {
                                binding.progress.visibility = View.GONE
                                initView()
                            }

                            AddViewModel.UiState.Loading -> {
                                binding.progress.visibility = View.VISIBLE
                            }

                            is AddViewModel.UiState.Success -> {
                                binding.progress.visibility = View.GONE
                                Toast.makeText(this@AddActivity, "登録に成功しました data id = ${uiState.data.dataId}", Toast.LENGTH_LONG).show()
                                clearForms()
                            }

                            is AddViewModel.UiState.Error -> {
                                binding.progress.visibility = View.GONE
                                // TODO:登録エラーの案内 ダイアログ表示→ボタン押下でリトライなどが適当？仮でToast出しておく
                                Toast.makeText(this@AddActivity, "登録に失敗しました ${uiState.e.text}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.isAddButtonEnabled.collect { isEnabled ->
                        binding.addButton.isEnabled = isEnabled
                    }
                }
            }
        }
    }

    /**
     * Viewの初期化
     */
    private fun initView() {
        binding.toolbar.apply {
            this.title = getString(R.string.add_activity_label)
            this.setNavigationIcon(R.drawable.outline_close_24)
            this.setNavigationOnClickListener {
                finish()
            }
        }

        binding.titleEdit.addTextChangedListener {
            viewModel.onTitleChanged(it.toString())
        }

        binding.addButton.setOnClickListener {
            val data = AddInventoryRequest(
                title = binding.titleEdit.text.toString()
            )
            viewModel.addInventory(data)
        }
    }

    /**
     * 入力欄のクリア
     */
    private fun clearForms() {
        binding.titleEdit.text.clear()
        viewModel.clearInput()
    }
}