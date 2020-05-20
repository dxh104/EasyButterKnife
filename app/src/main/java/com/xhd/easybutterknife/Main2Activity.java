package com.xhd.easybutterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.xhd.easy_butterknife.ButterKnife;
import com.xhd.easy_butterknife_annotations.BindView;

public class Main2Activity extends AppCompatActivity {
    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        textView.setText("界面2");
    }
}
