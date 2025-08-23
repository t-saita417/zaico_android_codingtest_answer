package jp.co.zaico.codingtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddActivity : AppCompatActivity() {

    private val viewModel: AddViewModel by viewModels()

    companion object {
        fun createIntent(context: Context) = Intent(context, AddActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        observeViewModel()
    }

    private fun observeViewModel(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        AddViewModel.UiState.Initial -> {
//                            initView()
                        }

                        AddViewModel.UiState.Loading -> TODO()
                        AddViewModel.UiState.Success -> {
                            // TODO: 登録成功の案内
                            // Toast表示など
                        }

                        is AddViewModel.UiState.Error -> {
                            // TODO:登録エラーの案内
                            // ダイアログ表示→リトライなど
                        }
                    }
                }
            }
        }
    }
}