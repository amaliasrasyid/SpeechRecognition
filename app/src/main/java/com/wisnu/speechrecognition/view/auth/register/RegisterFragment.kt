package com.wisnu.speechrecognition.view.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.databinding.FragmentRegisterBinding
import com.wisnu.speechrecognition.view.auth.AuthViewModel
import com.wisnu.speechrecognition.view.auth.login.LoginFragment
import com.wisnu.speechrecognition.view.auth.login.LoginFragmentArgs

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()
    private var roleId: Int? = 0

    companion object {
        private const val FULLNAME_NOT_NULL = "Nama tidak boleh kosong!"
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        val args = RegisterFragmentArgs.fromBundle(arguments as Bundle)
        roleId = args.role
        with(binding){
            edtFullname.addTextChangedListener(object : TextWatcher {
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
                        binding.tiFullname.error = FULLNAME_NOT_NULL
                    } else {
                        binding.tiFullname.error = null
                    }
                }
            })
            edtUsername.addTextChangedListener(object : TextWatcher {
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
            })
            edtPassword.addTextChangedListener(object : TextWatcher {
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
            })

            btnRegister.setOnClickListener{
                val fullName = edtFullname.text.toString().trim()
                val email = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when{
                    fullName.isEmpty() -> tiFullname.error = FULLNAME_NOT_NULL
                    email.isEmpty() -> tiUsername.error = USERNAME_NOT_NULL
                    password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                    else -> {
                        val params = HashMap<String,Any>()
                        params.put("nama",fullName)
                        params.put("email",email)
                        params.put("password",password)
                        params.put("role",roleId ?: 3) //Defaultnya buat ke siswa biar ndak error (??is it good practice)
                        params.put("gambar","")
                        register(params)
                    }
                }
            }
            tvBackToLogin.setOnClickListener{
                val toLogin = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(args.role)
                findNavController().navigate(toLogin)
            }
        }
    }

    private fun register(params: HashMap<String, Any>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}