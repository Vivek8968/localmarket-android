package com.localmarket.app.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentLoginBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModel()
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    viewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                showToast("Google sign in failed: ${e.message}")
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            navigateToHome()
            return
        }
        
        setupGoogleSignIn()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }
    
    private fun setupClickListeners() {
        // Send OTP button
        binding.buttonSendOtp.setOnClickListener {
            val phoneNumber = binding.editTextPhone.text.toString().trim()
            
            if (phoneNumber.length != 10) {
                binding.textInputLayoutPhone.error = "Please enter a valid 10-digit phone number"
                return@setOnClickListener
            }
            
            binding.textInputLayoutPhone.error = null
            sendVerificationCode("+91$phoneNumber")
        }
        
        // Google Sign In button
        binding.buttonGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }
    
    private fun sendVerificationCode(phoneNumber: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(viewModel.phoneAuthCallbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    private fun observeViewModel() {
        // Observe phone authentication state
        viewModel.phoneAuthState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PhoneAuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PhoneAuthState.CodeSent -> {
                    binding.progressBar.visibility = View.GONE
                    
                    // Navigate to OTP verification screen
                    val phoneNumber = binding.editTextPhone.text.toString().trim()
                    val action = LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                        phoneNumber = "+91$phoneNumber",
                        verificationId = state.verificationId
                    )
                    findNavController().navigate(action)
                }
                is PhoneAuthState.VerificationCompleted -> {
                    binding.progressBar.visibility = View.GONE
                    // Auto-verification completed, sign in with the credential
                    viewModel.verifyPhoneNumberWithCode("", "")
                }
                is PhoneAuthState.VerificationFailed -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Verification failed: ${state.message}")
                }
                is PhoneAuthState.SignInSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    
                    // Register user with backend
                    val user = state.user
                    viewModel.registerUserWithBackend(
                        name = user.displayName,
                        email = user.email,
                        phone = user.phoneNumber
                    )
                }
                is PhoneAuthState.SignInFailed -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Sign in failed: ${state.message}")
                }
            }
        }
        
        // Observe Google authentication state
        viewModel.googleAuthState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    // Register user with backend
                    val user = resource.data
                    viewModel.registerUserWithBackend(
                        name = user.displayName,
                        email = user.email,
                        phone = user.phoneNumber
                    )
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Google sign in failed: ${resource.message}")
                }
            }
        }
        
        // Observe user registration state
        viewModel.userRegistrationState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    navigateToHome()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Registration failed: ${resource.message}")
                }
            }
        }
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}