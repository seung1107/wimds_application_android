package com.wimds.wimds.activities;

/**
 * Created by dongdor on 2016. 5. 15..
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wimds.wimds.R;


public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;

    protected abstract int getLayoutResId();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
