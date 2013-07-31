package com.gmail.hasszhao.mininews.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gmail.hasszhao.mininews.R;


public abstract class BasicActivity extends SherlockFragmentActivity {

    protected void replaceOpenFragment(Fragment _f, String _tag) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.hyperspace_fast_in, R.anim.hyperspace_fast_out, R.anim.fade_in,
                R.anim.hyperspace_fast_out);
        trans.replace(R.id.container_error, _f, _tag).commit();
    }

    protected void removeFragment(String _tag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(_tag);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
    }
}
