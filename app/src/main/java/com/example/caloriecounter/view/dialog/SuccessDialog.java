package com.example.caloriecounter.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.example.caloriecounter.R;

public class SuccessDialog extends Dialog {

    public SuccessDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);

        View view = LayoutInflater.from(context).inflate(R.layout.activity_success, new FrameLayout(context), false);
        setContentView(view);

        LottieAnimationView animSuccess = view.findViewById(R.id.animSuccess);
        animSuccess.setVisibility(View.VISIBLE);
        animSuccess.playAnimation();

    }

}
