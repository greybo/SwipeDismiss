package com.swipe.swipedisvissview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_test);
        TextView test = (TextView) findViewById(R.id.view_test);
        Swipe swipe = new Swipe(this);
        swipe.addView(test);
        swipe.setLayout(layout);
    }
}
