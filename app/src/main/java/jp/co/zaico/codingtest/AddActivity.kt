package jp.co.zaico.codingtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        when (uiState) {
                            AddViewModel.UiState.Initial -> {
                                initView()
                            }

                            AddViewModel.UiState.Loading -> {
                                // TODO:通信中表示
                            }

                            is AddViewModel.UiState.Success -> {
                                Toast.makeText(this@AddActivity, "登録に成功しました data id = ${uiState.data.dataId}", Toast.LENGTH_LONG).show()
                                clearForms()
                            }

                            is AddViewModel.UiState.Error -> {
                                // TODO:登録エラーの案内 ダイアログ表示→ボタン押下でリトライなどが適当？仮でToast出しておく
                                Toast.makeText(this@AddActivity, "登録に失敗しました ${uiState.e}", Toast.LENGTH_LONG).show()
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

    private fun initView() {
        binding.toolbar.apply {
            this.title = getString(R.string.add_activity_label)
            this.setNavigationIcon(R.drawable.no_image) //TODO:アイコンは仮置き。バツボタンか戻るボタンにする
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