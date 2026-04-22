package com.example.daysmatter.ui.launch;






import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daysmatter.R;

public class LaunchFragemnt extends Fragment {

    private LaunchFragemntViewModel mViewModel;

    public static LaunchFragemnt newInstance() {
        return new LaunchFragemnt();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch_fragemnt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LaunchFragemntViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    Navigation.findNavController(getView())
                            .navigate(R.id.action_launchFragemnt_to_navigation_home);
                }
            }
        }, 2500);
    }

}