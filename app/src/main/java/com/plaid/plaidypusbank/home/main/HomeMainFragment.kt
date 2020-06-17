package com.plaid.plaidypusbank.home.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMainFragment : Fragment() {

  val viewModel: HomeMainViewModel by viewModels()

  lateinit var recyclerView: RecyclerView
  lateinit var content: ConstraintLayout
  lateinit var banner: TextView
  private lateinit var imageView: ImageView
  private lateinit var title: TextView

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

    title = root.findViewById(R.id.title)
    imageView = root.findViewById(R.id.logo)

    content = root.findViewById(R.id.content)
    banner = root.findViewById(R.id.home_banner)
    banner.setOnClickListener {
      goToApproval()
    }

    root.findViewById<ImageView>(R.id.home_settings).setOnClickListener {
      startActivity(Intent(requireContext(), SettingsActivity::class.java))
    }

    val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
    slideUp.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {
      }

      override fun onAnimationEnd(animation: Animation?) {
        viewModel.checkForPending()
      }

      override fun onAnimationStart(animation: Animation?) {
      }
    })
    // Start animation
    content.startAnimation(slideUp)

    viewModel.homeState.observe(viewLifecycleOwner, Observer(::updateUi))

    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    startPostponedEnterTransition()
  }

  private fun updateUi(homeState: HomeState) {
    when (homeState) {
      HomeState.Default -> {
      }
      HomeState.PendingApproval -> {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        banner.visibility = View.VISIBLE
        banner.startAnimation(slideDown)
      }
    }
  }

  private fun goToApproval() {
    val extras = FragmentNavigatorExtras(
      imageView to imageView.transitionName,
      title to title.transitionName
    )
    (requireActivity() as HomeActivity).navController.navigate(R.id.home_approve_action, null, null, extras)
  }
}
