package com.example.first_app;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

public class ClassicSquat extends AppCompatActivity {
    private int squatImages[] = new int[]{
            R.drawable.squatstart1, R.drawable.squatmed1, R.drawable.squatend1
    };

    private String[] squatImagesTitle = new String[]{
            "Squat1", "Squat2", "Squat3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_squat);

        Intent classicSquat = getIntent();

        CarouselView carouselView = findViewById(R.id.carousel);
        carouselView.setPageCount(squatImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(squatImages[position]);
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(ClassicSquat.this, squatImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * \brief enter Instructions&Setup view on click
     * @param view
     */

    public void enterInstructions(View view) {
        Intent squatInstr = new Intent(this, SquatInstr.class);
        startActivity(squatInstr);
    }
}
