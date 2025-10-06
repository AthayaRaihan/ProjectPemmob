package com.example.projectpemmob.ui.navigation

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.projectpemmob.R
import com.example.projectpemmob.ui.home.HomepageActivity
import com.example.projectpemmob.ui.wisata.WisataActivity
import com.example.projectpemmob.ui.kuliner.KulinerActivity
import com.example.projectpemmob.ui.favorit.FavoritActivity
import com.example.projectpemmob.ui.profil.ProfileActivity

class BottomNavigationHandler(private val activity: Activity) {
    private lateinit var ivHome: ImageView
    private lateinit var ivLocation: ImageView
    private lateinit var ivRestaurant: ImageView
    private lateinit var ivFavorites: ImageView
    private lateinit var ivProfile: ImageView

    fun setupBottomNavigation() {
        initViews()
        setupClickListeners()
        updateNavigationIcons()
    }

    private fun initViews() {
        ivHome = activity.findViewById(R.id.iv_home)
        ivLocation = activity.findViewById(R.id.iv_location)
        ivRestaurant = activity.findViewById(R.id.iv_restaurant)
        ivFavorites = activity.findViewById(R.id.iv_favorites)
        ivProfile = activity.findViewById(R.id.iv_profile)
    }

    private fun setupClickListeners() {
        activity.findViewById<LinearLayout>(R.id.ll_home)?.setOnClickListener {
            if (activity !is HomepageActivity) {
                navigateToActivity(HomepageActivity::class.java)
            }
        }

        activity.findViewById<LinearLayout>(R.id.ll_location)?.setOnClickListener {
            if (activity !is WisataActivity) {
                navigateToActivity(WisataActivity::class.java)
            }
        }

        activity.findViewById<LinearLayout>(R.id.ll_restaurant)?.setOnClickListener {
            if (activity !is KulinerActivity) {
                navigateToActivity(KulinerActivity::class.java)
            }
        }

        activity.findViewById<LinearLayout>(R.id.ll_favorites)?.setOnClickListener {
            if (activity !is FavoritActivity) {
                navigateToActivity(FavoritActivity::class.java)
            }
        }

        activity.findViewById<LinearLayout>(R.id.ll_profile)?.setOnClickListener {
            if (activity !is ProfileActivity) {
                navigateToActivity(ProfileActivity::class.java)
            }
        }
    }

    private fun updateNavigationIcons() {
        // Reset all icons to unselected state
        ivHome.setImageResource(R.drawable.ic_home)
        ivLocation.setImageResource(R.drawable.ic_location)
        ivRestaurant.setImageResource(R.drawable.ic_restaurant)
        ivFavorites.setImageResource(R.drawable.ic_favorites)
        ivProfile.setImageResource(R.drawable.ic_profile)

        // Set selected icon based on current activity
        when (activity) {
            is HomepageActivity -> ivHome.setImageResource(R.drawable.ic_home_selected)
            is WisataActivity -> ivLocation.setImageResource(R.drawable.ic_location_selected)
            is KulinerActivity -> ivRestaurant.setImageResource(R.drawable.ic_restaurant_selected)
            is FavoritActivity -> ivFavorites.setImageResource(R.drawable.ic_favorites_selected)
            is ProfileActivity -> ivProfile.setImageResource(R.drawable.ic_profile_selected)
        }
    }

    private fun navigateToActivity(activityClass: Class<out Activity>) {
        val intent = Intent(activity, activityClass)
    // If the target activity already exists in the task's back stack, bring it to front
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    // Avoid activity launch animations
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity.startActivity(intent)
        // Remove default animation so the bottom navigation appears static
        activity.overridePendingTransition(0, 0)
    }
}