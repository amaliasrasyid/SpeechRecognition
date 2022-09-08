package com.wisnu.speechrecognition.view.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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


class LoginFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()
    private val loginValid = true
    private lateinit var googleApiClient :GoogleApiClient

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
//        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        googleApiClient = GoogleApiClient.Builder(
            requireContext())
            .enableAutoManage(requireActivity(),this)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build()
        binding.signInButton.setOnClickListener{
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent,SIGN_IN)
        }
        prepareView()
    }

    private fun prepareView() {
        val roleId = LoginFragmentArgs.fromBundle(arguments as Bundle).role
        // Build a GoogleSignInClient with the options specified by gso.
        with(binding) {
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
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
            tvRegister.setOnClickListener{
                val toRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment().apply {
                    role = roleId
                }
                findNavController().navigate(toRegister)
            }
            btnLogin.setOnClickListener {
                val email = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when {
                    email.isEmpty() -> tiUsername.error = USERNAME_NOT_NULL
                    password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                    else -> {
                        progressBar.visibility = View.VISIBLE

                        val params = HashMap<String, Any>()
                        params["email"] = email
                        params["password"] = password

                        viewModel.login(params).observe(viewLifecycleOwner, { result ->
                            progressBar.visibility = View.GONE
                            if (result != null) {
                                if(result.data != null){
                                    if (result.code == 200) {
                                        val role = result.data?.role
                                        UserPreference(requireContext()).apply {
                                            setUser(
                                                User(
                                                    id = result.data?.id,
                                                    nama = result.data?.nama,
                                                    role = role,
                                                    gambar = result.data?.gambar,
                                                    email = result.data?.email,
                                                    password = result.data?.password
                                                )
                                            )
                                            setLogin(Login(loginValid))
                                            when(role){
                                                ROLE_GURU -> {startActivity(Intent(requireContext(),TeacherActivity::class.java))}
                                                ROLE_SISWA -> {startActivity(Intent(requireContext(), MainActivity::class.java))}
                                            }
                                        }
                                    } else {
                                        showMessage(
                                            requireActivity(),
                                            TITLE_ERROR,
                                            message = result.message ?: "",
                                            style = MotionToast.TOAST_ERROR
                                        )
                                    }
                                }else{
                                    showMessage(
                                        requireActivity(),
                                        TITLE_ERROR,
                                        message = result.message ?: "",
                                        style = MotionToast.TOAST_ERROR
                                    )
                                }
                            }else{
                                showMessage(
                                    requireActivity(),
                                    TITLE_ERROR,
                                    style = MotionToast.TOAST_ERROR
                                )
                            }
                        })
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN){
            val mGoogleSignInApi = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if(mGoogleSignInApi!!.isSuccess){
                Log.d("login fragment",mGoogleSignInApi.toString())
                val role = LoginFragmentArgs.fromBundle(arguments as Bundle).role
                when(role){
                    ROLE_GURU -> {startActivity(Intent(requireContext(),TeacherActivity::class.java))}
                    ROLE_SISWA -> {startActivity(Intent(requireContext(), MainActivity::class.java))}
                }
            }else{
                Toast.makeText(requireContext(),"sign in with google failed",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        googleApiClient.stopAutoManage(requireActivity())
        googleApiClient.disconnect()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
        Log.d(TAG,"connection failed sign in api google ${p0.toString()}")
    }
}