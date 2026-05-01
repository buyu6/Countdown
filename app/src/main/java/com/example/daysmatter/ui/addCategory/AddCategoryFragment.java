package com.example.daysmatter.ui.addCategory;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentAddCategoryBinding;
import com.example.daysmatter.databinding.FragmentAddMsgBinding;
import com.example.daysmatter.logic.entity.IconItem;
import com.example.daysmatter.logic.room.CategoryDao;
import com.example.daysmatter.logic.room.CountdownDatabase;
import com.example.daysmatter.ui.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryFragment extends Fragment {

    private AddCategoryViewModel viewModel;
    private FragmentAddCategoryBinding binding;
    private final List<IconItem> iconList = new ArrayList<>();
    private IconAdapter adapter;

    private int selectedIconId = -1;

    public static AddCategoryFragment newInstance() {
        return new AddCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AddCategoryViewModel.class);
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setOnClicked();
        observeSaveResult();
    }

    private void setOnClicked() {
        binding.savebutton.setOnClickListener(v -> {
            String name = binding.bookName.getText().toString().trim();
            viewModel.save(name, selectedIconId);
        });
    }

    private void observeSaveResult() {
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

            if (result.isSuccess()) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    private void init() {
        iconList.clear();

        int[] icons = {
                R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,
                R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9, R.drawable.icon10,
                R.drawable.icon11, R.drawable.icon12, R.drawable.icon13, R.drawable.icon14, R.drawable.icon15,
                R.drawable.icon16, R.drawable.icon17, R.drawable.icon18, R.drawable.icon19, R.drawable.icon20,
                R.drawable.icon21, R.drawable.icon22, R.drawable.icon23, R.drawable.icon24, R.drawable.icon25,
                R.drawable.icon26, R.drawable.icon27, R.drawable.icon28, R.drawable.icon29, R.drawable.icon30,
                R.drawable.icon31, R.drawable.icon32, R.drawable.icon33, R.drawable.icon34, R.drawable.icon35,
                R.drawable.icon36, R.drawable.icon37, R.drawable.icon38, R.drawable.icon39
        };

        for (int resId : icons) {
            iconList.add(new IconItem(resId));
        }

        adapter = new IconAdapter(iconList, resId -> {
            selectedIconId = resId;
        });

        binding.iconRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.iconRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}