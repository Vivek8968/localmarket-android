package com.localmarket.app.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentOtpVerificationBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class OtpVerificationFragment : Fragment() {
    
    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!
    
    private val args: OtpVerificationFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by viewModel()
    
    private var resendTimer: CountDownTimer? = null
    private var canResendOtp = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        observeViewModel()
        startResendTimer()
    }
    
    private fun setupUI() {
        binding.textViewPhoneNumber.text = args.phoneNumber
    }
    
    private fun setupClickListeners() {
        // Verify OTP button
        binding.buttonVerifyOtp.setOnClickListener {
            val otp = binding.editTextOtp.text.toString().trim()
            
            if (otp.length != 6) {
                binding.textInputLayoutOtp.error = "Please enter a valid 6-digit OTP"
                return@setOnClickListener
            }
            
            binding.textInputLayoutOtp.error = null
            viewModel.verifyPhoneNumberWithCode(args.verificationId, otp)
        }
        
        // Resend OTP text
        binding.textViewResendOtp.setOnClickListener {
            if (canResendOtp) {
                resendVerificationCode(args.phoneNumber)
                startResendTimer()
            }
        }
    }
    
    private fun resendVerificationCode(phoneNumber: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(viewModel.phoneAuthCallbacks)
            .setForceResendingToken(PhoneAuthProvider.ForceResendingToken.zza())
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    private fun startResendTimer() {
        canResendOtp = false
        binding.textViewResendOtp.isEnabled = false
        binding.textViewResendOtp.text = getString(R.string.resend_otp_in, 60)
        
        resendTimer?.cancel()
        
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textViewResendOtp.text = getString(
                    R.string.resend_otp_in,
                    millisUntilFinished / 1000
                )
            }
            
            override fun onFinish() {
                canResendOtp = true
                binding.textViewResendOtp.isEnabled = true
                binding.textViewResendOtp.text = getString(R.string.resend_otp)
            }
        }.start()
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
                    showToast("OTP sent successfully")
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
        findNavController().navigate(R.id.action_otpVerificationFragment_to_homeFragment)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel()
        _binding = null
    }
}