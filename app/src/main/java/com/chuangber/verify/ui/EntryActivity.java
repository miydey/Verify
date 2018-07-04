package com.chuangber.verify.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.chuangber.verify.R;
import com.chuangber.verify.adapter.FragAdapter;
import com.chuangber.verify.fragment.IDFragment;
import com.chuangber.verify.fragment.NameFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinyh on 2017/10/30.
 */

public class EntryActivity extends FragmentActivity {
    List<Fragment> fragments;
    ViewPager viewPagerInput;
    IDFragment idFragment;
    NameFragment nameFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_entry);
        fragments = new ArrayList<Fragment>();
        idFragment = new IDFragment();
        nameFragment = new NameFragment();
        fragments.add(idFragment);
        fragments.add(nameFragment);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        viewPagerInput = (ViewPager)findViewById(R.id.viewpager_entry);
        viewPagerInput.setAdapter(adapter);

    }

    public void swipeNext(){
        viewPagerInput.setCurrentItem(viewPagerInput.getCurrentItem() + 1, true);
        forceOpenSoftKeyboard(this);
    }
    public void swipeLast(){
        viewPagerInput.setCurrentItem(viewPagerInput.getCurrentItem() -1, true);
        //forceHideSoftKeyboard(this);
    }


    public  void forceOpenSoftKeyboard(Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
        }

}
