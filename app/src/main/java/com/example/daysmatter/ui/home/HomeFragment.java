package com.example.daysmatter.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.daysmatter.MainActivity;
import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentHomeBinding;
import com.example.daysmatter.logic.room.Message;
import com.example.daysmatter.ui.View.ViewLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private MsgAdapter adapter;
    private ViewLayout topItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(
                this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(requireContext());
        binding.firstRecyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MsgAdapter(requireActivity());
        binding.firstRecyclerView.setAdapter(adapter);
        topItem=view.findViewById(R.id.topItem);
        setOnClicked();
        setUpObserves();
    }
    private  void setUpObserves(){
        homeViewModel.getMsgList().observe(getViewLifecycleOwner(),messages-> {
            //筛选置顶信息
            List<Message> pinnedList = new ArrayList<>(messages);
            pinnedList.removeIf(message -> !message.isTop());
            if (!pinnedList.isEmpty() && topItem != null) {
                topItem.setVisibility(View.VISIBLE);
                topItem.setPinnedMessages(pinnedList, msg -> {
                    int flag=0;
                    Bundle result = new Bundle();
                    result.putInt("flag",flag);
                    result.putSerializable("msg_data", msg);
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_showMsgFragment, result);
                });

            }else {
                topItem.removeAllViews();
                topItem.setVisibility(View.INVISIBLE);
            }
            //普通信息刷新
            adapter.submitList(messages);
            binding.titleCategory.setText("倒数纪念日-" + MainActivity.currentCategory);
        });
    }


    private void setOnClicked() {
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("result",0);
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_addMsgFragment,bundle);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String category = MainActivity.currentCategory;
        if ("全部".equals(category)) {
            homeViewModel.loadMessages();
        } else {
            homeViewModel.loadMessagesByCategory(category);
        }
    }
}