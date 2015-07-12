package com.example.skywish.imtest001.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by skywish on 2015/7/5.
 */
public class ImageActivity extends BaseActivity {

    private ImageView imageView;
    private Button btn_setting;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String uri = getIntent().getStringExtra("image");

        imageView = (ImageView) findViewById(R.id.iv_imagebrower);
        btn_setting = (Button) findViewById(R.id.btn_setting);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        ImageLoader.getInstance().displayImage(uri, imageView, ImageLoadOptions.getOptions(), new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}
