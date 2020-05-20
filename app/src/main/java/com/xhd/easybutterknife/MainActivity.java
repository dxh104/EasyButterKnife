package com.xhd.easybutterknife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xhd.easy_butterknife.ButterKnife;
import com.xhd.easy_butterknife.UnBinder;
import com.xhd.easy_butterknife_annotations.BindView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.btnOpenMain2)
    TextView btnOpenMain2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        textView.setText("界面1");
        btnOpenMain2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnOpenMain2.getId()) {
            startActivity(new Intent(MainActivity.this, Main2Activity.class));
        }
    }
}
