package com.plaid.plaidypusbank.home.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.models.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMainFragment : Fragment() {

  val viewModel: HomeMainViewModel by viewModels()
  lateinit var user: User

  lateinit var recyclerView: RecyclerView
  lateinit var content: ConstraintLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val root = inflater.inflate(R.layout.home_main_fragment, container, false)
    recyclerView = root.findViewById(R.id.home_main_recycler)
    recyclerView.adapter = HomeAdapter()
    recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    content = root.findViewById(R.id.content)
    
    val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
    // Start animation
    content.startAnimation(slideUp)
    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    startPostponedEnterTransition()
  }
}
