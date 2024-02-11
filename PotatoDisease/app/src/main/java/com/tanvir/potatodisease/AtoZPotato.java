package com.tanvir.potatodisease;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AtoZPotato extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ato_zpotato);

        Toolbar toolbar=findViewById(R.id.toolbar);
        ImageView backBtn=findViewById(R.id.backBtn_2);
        setSupportActionBar(toolbar);

        ActionBar ab=getSupportActionBar();

        if(ab!=null){
            ab.setDisplayHomeAsUpEnabled(false);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AtoZPotato.this,MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });
    }
}