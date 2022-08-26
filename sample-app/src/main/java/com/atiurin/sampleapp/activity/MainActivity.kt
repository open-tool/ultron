package com.atiurin.sampleapp.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.atiurin.sampleapp.adapters.ContactAdapter
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.R
import kotlin.collections.ArrayList
import com.atiurin.sampleapp.MyApplication
import android.view.View
import android.widget.Toast
import com.atiurin.sampleapp.async.ContactsPresenter
import com.atiurin.sampleapp.async.ContactsProvider
import com.atiurin.sampleapp.data.Tags
import com.atiurin.sampleapp.idlingresources.IdlingHelper
import com.atiurin.sampleapp.idlingresources.resources.ContactsIdlingResource
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.sampleapp.view.CircleImageView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ContactsProvider {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ContactAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var accountManager: AccountManager
    private val onItemClickListener: View.OnClickListener? = null
    private val contactsPresenter = ContactsPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountManager = AccountManager(applicationContext)
        if (!accountManager.isLogedIn()) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        setContentView(R.layout.activity_main)
        MyApplication.context = applicationContext
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.title_friends_list)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navigationAvatar = navView.getHeaderView(0).findViewById<CircleImageView>(R.id.navigation_user_avatar)
        navigationAvatar.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        viewManager = LinearLayoutManager(this)
        viewAdapter = ContactAdapter(this, ArrayList<Contact>(),
            object : ContactAdapter.OnItemClickListener {
                override fun onItemClick(contact: Contact) {
                    val intent = Intent(applicationContext, ChatActivity::class.java)
                    intent.putExtra(INTENT_CONTACT_ID_EXTRA_NAME, contact.id)
                    startActivity(intent)
                }
            })
        recyclerView = findViewById<RecyclerView>(R.id.recycler_friends).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.tag = Tags.CONTACTS_LIST
        contactsPresenter.getAllContacts()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(com.atiurin.sampleapp.R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onContactsLoaded(contacts: ArrayList<Contact>) {
        viewAdapter.updateData(contacts)
        viewAdapter.notifyDataSetChanged()
        IdlingHelper.ifAllowed { ContactsIdlingResource.getInstanceFromApp()?.setIdleState(true) }
    }

    override fun onFailedToLoadContacts(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_settings -> {
                Snackbar.make(recyclerView, "Settings not implemented", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            R.id.nav_saved_messages -> {
                Snackbar.make(recyclerView, "Saved messages not implemented", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile not implemented", Toast.LENGTH_LONG).show()
            }
            R.id.nav_logout -> {
                accountManager.logout()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.ui_elements -> {
                val intent = Intent(applicationContext, UiElementsActivity::class.java)
                startActivity(intent)
            }
            R.id.web_view_nav_item -> {
                val intent = Intent(applicationContext, WebViewActivity::class.java)
                startActivity(intent)
            }
            R.id.compose_elements -> {
                startActivity(Intent(applicationContext, ComposeElementsActivity::class.java))
            }
            R.id.compose_list -> {
                startActivity(Intent(applicationContext, ComposeListActivity::class.java))
            }
            R.id.custom_clicks_nav_item -> {
                val intent = Intent(applicationContext, CustomClicksActivity::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
