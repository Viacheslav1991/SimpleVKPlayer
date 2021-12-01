package com.android.simplevkplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.simplevkplayer.R
import com.android.simplevkplayer.ui.list.SongsListFragment
import com.android.simplevkplayer.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LoginFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SongsListFragment.newInstance(""))
                .commitNow()
        }
    }

    override fun loginSuccess(token: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SongsListFragment.newInstance(token))
            .commitNow()
    }


}