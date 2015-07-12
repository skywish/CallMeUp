package com.example.skywish.imtest001.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.ui.CallUpActivity;
import com.example.skywish.imtest001.ui.NearPeopleActivity;
import com.example.skywish.imtest001.ui.RecordActivity;

/**
 * Created by skywish on 2015/7/7.
 */
public class FindFragment extends Fragment implements View.OnClickListener{

    private LinearLayout layout_call, layout_near;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        layout_call = (LinearLayout) getView().findViewById(R.id.layout_call);
        layout_near = (LinearLayout) getView().findViewById(R.id.layout_near);

        layout_near.setOnClickListener(this);
        layout_call.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.layout_near:
                intent = new Intent(getActivity(), NearPeopleActivity.class);
                break;
            case R.id.layout_call:
                intent = new Intent(getActivity(), RecordActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
