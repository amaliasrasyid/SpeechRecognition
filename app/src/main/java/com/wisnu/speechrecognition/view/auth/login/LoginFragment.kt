package com.wisnu.speechrecognition.view.auth.login

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.local_db.Login
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentLoginBinding
import com.wisnu.speechrecognition.local_db.User
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_GURU
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_SISWA
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.auth.AuthViewModel
import com.wisnu.speechrecognition.view.main.ui.student.MainActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.TeacherActivity
import www.sanju.motiontoast.MotionToast
import com.google.android.material.textfield.TextInputEditText
import com.wisnu.speechrecognition.data.request.LoginRequest
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.utils.Status
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_ADMIN
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val loginValid = true
    private var roleId :Int  = 0

    private lateinit var edtUsername :TextInputEditText
    private lateinit var edtPassword :TextInputEditText

    private val TAG = LoginFragment::class.java.simpleName

    companion object {
        private const val SIGN_IN = 100
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        roleId = LoginFragmentArgs.fromBundle(arguments as Bundle).role
        binding.tvRegister.visibility = if(roleId != ROLE_SISWA) View.GONE else View.VISIBLE
        // Build a GoogleSignInClient with the options specified by gso.
        with(binding) {
            edtUsername.addTextChangedListener(textWatcherUsername)
            edtPassword.addTextChangedListener(textWatcherPsw)
            tvRegister.setOnClickListener(this@LoginFragment)
            btnLogin.setOnClickListener(this@LoginFragment)
        }
    }
    val textWatcherUsername = object : TextWatcher {
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
            if (s?.length!! == 0) {
                binding.tiUsername.error = USERNAME_NOT_NULL
            } else {
                binding.tiUsername.error = null
            }
        }
    }
    val textWatcherPsw = object : TextWatcher {
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
                binding.tiPassword.error = MIN_COUNTER_LENGTH_PASS
            } else if (s.isNullOrEmpty()) {
                binding.tiPassword.error = PASSWORD_NOT_NULL
            } else {
                binding.tiPassword.error = null
            }
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                tvRegister -> {
                    val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment().apply {
                        role = roleId!!
                    }
                    findNavController().navigate(toRegister)
                }
                btnLogin -> {
                    val email = edtUsername.text.toString().trim()
                    val password = edtPassword.text.toString().trim()

                    when {
                        email.isEmpty() -> tiUsername.error = USERNAME_NOT_NULL
                        password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                        else -> {
                            loader(true)
                            login(LoginRequest(email,password))
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun login(request: LoginRequest) {
        val hasObserver = viewModel.login(request).observe(viewLifecycleOwner){ result ->
            when(result.status) {
                Status.LOADING -> loader(true)
                Status.SUCCESS -> {
                    loader(false)
                    result.data.let {
                        val role = it?.data?.role
                        UserPreference(requireContext()).apply {
                            setUser(
                                User(
                                    id = it?.data?.id,
                                    nama = it?.data?.nama,
                                    role = role,
                                    gambar = it?.data?.gambar,
                                    email = it?.data?.email,
                                    password = it?.data?.password
                                )
                            )
                            setLogin(Login(loginValid))
                            showMessage(
                                requireActivity(),
                                TITLE_SUCESS,
                                message = "berhasil login",
                                style = MotionToast.TOAST_SUCCESS
                            )
                            Log.d(TAG, result?.message ?: "")
                            when (role) {
                                ROLE_ADMIN -> {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            TeacherActivity::class.java
                                        )
                                    )
                                }
                                ROLE_SISWA -> {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            MainActivity::class.java
                                        )
                                    )
                                }
                                ROLE_GURU -> Toast.makeText(
                                    requireActivity(),
                                    "hak akses untuk guru tidak diizinkan masuk ke aplikasi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    loader(false)
                    showMessage(
                        requireActivity(),
                        TITLE_ERROR,
                        result.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        with(binding){
            edtUsername.removeTextChangedListener(textWatcherUsername)
            edtPassword.removeTextChangedListener(textWatcherPsw)
        }
    }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                progressBar.visibility = android.view.View.VISIBLE
            } else {
                progressBar.visibility = android.view.View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}