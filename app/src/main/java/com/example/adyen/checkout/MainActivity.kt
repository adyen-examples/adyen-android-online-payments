package com.example.adyen.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.adyen.checkout.service.ComponentType
import com.example.adyen.checkout.ui.cart.CartViewFragment
import com.example.adyen.checkout.ui.components.ComponentSelectContent
import com.example.adyen.checkout.ui.components.ComponentSelectFragment
import com.example.adyen.checkout.ui.components.ComponentsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), CartViewFragment.OnListFragmentInteractionListener,
    ComponentSelectFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dropin, R.id.navigation_components
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // load cart view on click of component type list item in components view
    override fun onListFragmentInteraction(item: ComponentSelectContent.ComponentSelectItem?) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val current = fragmentManager.findFragmentById(R.id.select_list)
        if (current != null) {
            fragmentTransaction.remove(current)
        }
        // pass the component type from the list item
        val fragment = ComponentsFragment(item?.id ?: ComponentType.IDEAL)
        fragmentTransaction.add(R.id.component_list_container, fragment)
        fragmentTransaction.commit()
    }
}
