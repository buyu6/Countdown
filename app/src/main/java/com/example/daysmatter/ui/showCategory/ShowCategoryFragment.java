package com.example.daysmatter.ui.showCategory;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentShowCategoryBinding;
import com.example.daysmatter.ui.home.HomeViewModel;
import com.example.daysmatter.ui.home.MsgAdapter;

public class ShowCategoryFragment extends Fragment {

    private HomeViewModel viewModel;
    private FragmentShowCategoryBinding binding;
    private String category;
    private MsgAdapter adapter;


    public static ShowCategoryFragment newInstance() {
        return new ShowCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding=FragmentShowCategoryBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setUpObserves();
    }
    @SuppressLint("NewApi")
    private void setUpObserves(){
        viewModel.getMsgList().observe(getViewLifecycleOwner(),messages->{
            if(category.equals("全部")){
                adapter.submitList(messages);
            }else{
                adapter.submitList(messages.stream().filter(message -> message.getCategoryName().equals(category)).toList());
            }
        });
    }

    private void init(){
        binding.showCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter=new MsgAdapter(requireActivity());
        binding.showCategoryRecyclerView.setAdapter(adapter);
        Bundle bundle=getArguments();
        category=bundle.getString("category");

    }

}