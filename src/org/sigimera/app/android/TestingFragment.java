package org.sigimera.app.android;

import org.sigimera.app.android.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TestingFragment extends FragmentActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.login);
    }
}
