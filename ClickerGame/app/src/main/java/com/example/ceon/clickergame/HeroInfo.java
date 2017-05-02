package com.example.ceon.clickergame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HeroInfo extends AppCompatActivity {

    // Retrieve the extra data passed on from the intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_info);
        int originalPosition = getIntent().getExtras().getInt("originalPosition");

        // Serialize the passed in extra
        ArrayList<Hero> heroes = (ArrayList<Hero>) getIntent().getSerializableExtra("heroes");

        // Get the hero
        Hero currHero = heroes.get(originalPosition);

        TextView nameText = (TextView) findViewById(R.id.hero_name);
        nameText.setText(currHero.getName());

        ImageView heroImage = (ImageView) findViewById(R.id.hero_image);
        heroImage.setImageResource(currHero.getIconID());

        TextView costText = (TextView) findViewById(R.id.hero_cost);
        costText.setText("" + currHero.getCost());

        TextView dpsText = (TextView) findViewById(R.id.hero_dps);
        dpsText.setText("" + currHero.getTotalDps());

        TextView descText = (TextView) findViewById(R.id.hero_desc);
        descText.setText(currHero.getDescription());
    }
}
