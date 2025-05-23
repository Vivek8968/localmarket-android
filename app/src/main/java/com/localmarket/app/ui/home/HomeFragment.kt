package com.localmarket.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentHomeBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.openWhatsApp
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var shopAdapter: ShopAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupVendorFab()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        shopAdapter = ShopAdapter(
            onShopClicked = { shop ->
                val action = HomeFragmentDirections.actionHomeFragmentToShopDetailFragment(shop.id)
                findNavController().navigate(action)
            },
            onWhatsAppClicked = { phoneNumber ->
                requireContext().openWhatsApp(phoneNumber)
            }
        )
        
        binding.recyclerViewShops.adapter = shopAdapter
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }
    
    private fun setupVendorFab() {
        binding.fabVendor.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVendorDashboardFragment()
            findNavController().navigate(action)
        }
    }
    
    private fun observeViewModel() {
        // Observe shops
        viewModel.shops.observe(viewLifecycleOwner) { resource ->
            binding.swipeRefreshLayout.isRefreshing = false
            
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textViewEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    if (resource.data.isEmpty()) {
                        binding.textViewEmpty.visibility = View.VISIBLE
                    } else {
                        binding.textViewEmpty.visibility = View.GONE
                        shopAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.textViewEmpty.text = resource.message
                    showToast(resource.message)
                }
            }
        }
        
        // Observe current user
        viewModel.currentUser.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val user = resource.data
                    binding.fabVendor.visibility = if (user.isVendor) View.VISIBLE else View.GONE
                }
                else -> {
                    binding.fabVendor.visibility = View.GONE
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    // Menu methods
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_home, menu)
        
        // Update login/logout menu item based on authentication state
        menu.findItem(R.id.action_login)?.isVisible = !viewModel.isUserLoggedIn()
        menu.findItem(R.id.action_logout)?.isVisible = viewModel.isUserLoggedIn()
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_login -> {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                true
            }
            R.id.action_logout -> {
                viewModel.signOut()
                requireActivity().invalidateOptionsMenu()
                showToast("Logged out successfully")
                true
            }
            R.id.action_refresh -> {
                binding.swipeRefreshLayout.isRefreshing = true
                viewModel.refreshData()
                true
            }
            else -> false
        }
    }
}