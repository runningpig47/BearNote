package cloud.runningpig.bearnote.ui.personal

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityLoginBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    //铭文显示密码切换，默认不显示密码
    private var showPassword = false

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        binding.textView1.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.textView2.setOnClickListener {
            val username = binding.editText1.text.toString()
            val password = binding.editText2.text.toString()
            if (viewModel.loginEntryValid(username, password)) {
                // TODO 登录
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val user = viewModel.login(username, password)
                        if (user.id != -1) {
                            viewModel.insertUser(user)
                        }
                        withContext(Dispatchers.Main) {
                            Log.d("test20211017", "user: $user")
                            if (user.id == -1) {
                                "登录失败".showToast()
                            } else {
                                "登录成功".showToast()
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
        binding.relativeLayout2.setOnClickListener {
            finish()
        }
        binding.relativeLayout3.setOnClickListener {
            if (showPassword) { //如果当前是显示密码的状态，点击后改为不显示密码明文
                binding.imageview2.setImageResource(R.drawable.icon_close_password)
                binding.editText2.transformationMethod = PasswordTransformationMethod.getInstance() //密码隐藏
                showPassword = false
            } else { //如果当前是密码状态，点击后显示密码
                binding.imageview2.setImageResource(R.drawable.icon_open_password)
                binding.editText2.transformationMethod = HideReturnsTransformationMethod.getInstance() //密码显示
                showPassword = true
            }
            binding.editText2.setSelection(binding.editText2.text.toString().length) //将光标移至文字末尾
        }
        setContentView(binding.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            controlKeyboardLayout(binding.root, binding.relativeLayout1)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.textView3.visibility = View.VISIBLE
        binding.root.scrollTo(0, 0)
    }

    private fun controlKeyboardLayout(root: View, scrollToView: View) {
        val location = IntArray(2)
        // 获取scrollToView在窗体的坐标
        scrollToView.getLocationInWindow(location)
        //注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数
        root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            // 获取root在窗体的可视区域
            root.getWindowVisibleDisplayFrame(rect)
            // 当前视图最外层的高度减去现在所看到的视图的最底部的y坐标
            val rootInvisibleHeight = root.rootView.height - rect.bottom //不可见高度
            if (rootInvisibleHeight > 300) {
                binding.textView3.visibility = View.INVISIBLE
                //软键盘弹出来的时候
                // 计算root滚动高度，使scrollToView在可见区域的底部
                val target1 = location[1] + scrollToView.height - rect.bottom //上移目标1
                val target2 = (location[1] - target1) / 4 //上移目标2，为了更加居中
                val scrollHeight = target1 + target2
                root.scrollTo(0, scrollHeight)
            } else {
                binding.textView3.visibility = View.VISIBLE
                root.scrollTo(0, 0)
            }
        }
    }

}