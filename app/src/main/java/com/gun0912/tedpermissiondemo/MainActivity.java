package com.gun0912.tedpermissiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onButtonClick(view);
            }
        };
        //ButterKnife.bind(this);
        View view = findViewById(R.id.btn_nomessage);
        view.setOnClickListener(clickListener);

        view = findViewById(R.id.btn_only_deny_message);
        view.setOnClickListener(clickListener);

        view = findViewById(R.id.btn_only_rationale_message);
        view.setOnClickListener(clickListener);

        view = findViewById(R.id.btn_rationale_deny);
        view.setOnClickListener(clickListener);

    }

    public void onButtonClick(View view){

        int id = view.getId();

        Intent intent=null;

        switch (id){
            case R.id.btn_nomessage:
                intent = new Intent(this,NoDialogActivity.class);
                break;

            case R.id.btn_only_deny_message:
                intent = new Intent(this,DenyActivity.class);
                break;

            case R.id.btn_only_rationale_message:
                intent = new Intent(this,RationaleActivity.class);
                break;

            case R.id.btn_rationale_deny:
                intent = new Intent(this,RationaleDenyActivity.class);
                break;


        }

        if(intent!=null){
            startActivity(intent);
        }
    }
}
