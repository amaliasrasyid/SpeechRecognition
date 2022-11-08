package com.wisnu.speechrecognition.view.main.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.wisnu.speechrecognition.databinding.ActivityChangePasswordBinding
import com.wisnu.speechrecognition.databinding.ActivityUserProfileBinding
import com.wisnu.speechrecognition.local_db.User
import com.wisnu.speechrecognition.model.user.Data
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.Status
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel by viewModels<AuthViewModel>()

    private val TAG = UserProfileActivity::class.simpleName

    companion object {
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val NEW_PASSWORD_NOT_NULL = "Password baru tidak boleh kosong!"
        private const val CONFIRM_PASSWORD_NOT_NULL = "Password yang diinputkan tidak sama!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()
    }

    private fun prepareView() {
        with(binding){
            edtOldPsw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! < 5) {
                    binding.tiOldPsw.error = MIN_COUNTER_LENGTH_PASS
                } else if (s.isNullOrEmpty()) {
                    binding.tiOldPsw.error = PASSWORD_NOT_NULL
                } else {
                    binding.tiOldPsw.error = null
                }
            }
        })

            edtNewPsw.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! < 5) {
                        binding.tiNewPsw.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        binding.tiNewPsw.error = PASSWORD_NOT_NULL
                    } else {
                        binding.tiNewPsw.error = null
                    }
                }
            })


            edtConfirPsw.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! < 5) {
                        binding.tiConfirmPsw.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        binding.tiConfirmPsw.error = PASSWORD_NOT_NULL
                    } else {
                        binding.tiConfirmPsw.error = null
                    }
                }
            })
            btnBack.setOnClickListener(this@ChangePasswordActivity)
            btnUpdatePsw.setOnClickListener(this@ChangePasswordActivity)
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnBack -> finish()
                btnUpdatePsw -> updatePassword()
            }
        }
    }

    private fun updatePassword() {
        loader(true)
        with(binding){
            val oldPsw = edtOldPsw.text.toString().trim()
            val newPsw = edtNewPsw.text.toString().trim()
            val confirmPsw = edtConfirPsw.text.toString().trim()

            val userId = UserPreference(this@ChangePasswordActivity).getUser().id

            if(newPsw != confirmPsw){
                showMessage(
                    this@ChangePasswordActivity,
                    TITLE_WARNING,
                    "kata sandi baru dan konfirmasi kata sandi harus sama",
                    style = MotionToast.TOAST_WARNING
                )
                loader(false)
                return@with
            }

            var params = HashMap<String, Any>()
            params.put("user_id",userId!!)
            params.put("old_psw",oldPsw)
            params.put("new_psw",newPsw)
            params.put("confirm_psw",confirmPsw)

            changePassword(params)
        }

    }

    private fun changePassword(params: HashMap<String, Any>) {
        viewModel.updatePassword(params).observe(this) { result ->
            when(result.status) {
                Status.LOADING -> loader(true)
                Status.SUCCESS -> {
                    loader(false)
                    showMessage(
                        this@ChangePasswordActivity,
                        UtilsCode.TITLE_SUCESS,
                        result.message ?: "",
                        style = MotionToast.TOAST_SUCCESS
                    )
                    result.data.let {
                        saveDataToPreference(it?.data!!)
                    }
                    finish()
                }
                Status.ERROR -> {
                    loader(false)
                    showMessage(
                        this@ChangePasswordActivity,
                        UtilsCode.TITLE_ERROR,
                        result.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            }
        }
    }

    private fun saveDataToPreference(data: Data) {
        UserPreference(this@ChangePasswordActivity).apply {
            setUser(
                User(
                    id = data?.id,
                    nama = data?.nama,
                    role = data.role,
                    gambar = data?.gambar,
                    email = data?.email,
                    password = data?.password
                )
            )
        }
    }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }
}