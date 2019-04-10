package com.example.first_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import android.support.design.widget.Snackbar;

public class Lunge extends AppCompatActivity {
    private ViewFlipper vfContainer;
    private Context context;
    private float initialX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunge);

        Intent lunge = getIntent();

        context = this;
//
//        vfContainer = (ViewFlipper) this.findViewById(R.id.vfContainer);
//
//        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                vfContainer.setAutoStart(true);
//                vfContainer.setFlipInterval(1000);
//                vfContainer.startFlipping();
//
//                Snackbar.make(view, "Automatic view flipping has started", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                vfContainer.stopFlipping();
//
//                Snackbar.make(view, "Automatic view flipping has stopped", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;

            case MotionEvent.ACTION_UP:
                float finalX = touchevent.getX();
                if (initialX > finalX) {
                    if (vfContainer.getDisplayedChild() == 1)
                        break;

                    vfContainer.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_left));
                    vfContainer.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_from_left));

                    vfContainer.showPrevious();
                } else {
                    if (vfContainer.getDisplayedChild() == 0)
                        break;

                    vfContainer.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_right));
                    vfContainer.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_from_right));

                    vfContainer.showNext();
                }
                break;
        }
        return false;
    }
}
