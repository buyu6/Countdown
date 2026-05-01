package com.example.daysmatter.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentDashboardBinding;
import com.example.daysmatter.logic.room.Category;
import com.example.daysmatter.ui.settingCategory.CategoryAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private CategoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         viewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setMenu();
        setUpObserves();
        setOnclicked();
    }
    private void setOnclicked(){
        binding.addCategoryBtn.setOnClickListener(v->{
            Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_addCategoryFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
         viewModel.loadCategories();
    }

    private void setMenu(){
        MenuHost menuHost=requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.category_menu,menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.add_category){
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_dashboard_to_addCategoryFragment);

                    return true;
                }else if(menuItem.getItemId()==R.id.settingCategory){
                    Bundle bundle=new Bundle();
                    bundle.putInt("flag",2);
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_dashboard_to_settingCategoryFragment,bundle);
                 return true;
                }
                return false;
            }
        },getViewLifecycleOwner(),Lifecycle.State.RESUMED);
    }
    private void init(){
         binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
         adapter=new CategoryAdapter(
                 requireActivity(),
                 new ArrayList<>(),
                 false,
                 true,
                 false,
                 -1,
                 category -> {

                 },
                 category -> {

                 }
         );
         binding.categoryRecyclerView.setAdapter(adapter);
    }
    private void setUpObserves(){
        viewModel.getMsgList().observe(getViewLifecycleOwner(), categories -> {
            List<Category> allCategories = new ArrayList<>();
            allCategories.add(new Category("全部", R.drawable.allevent));
            allCategories.add(new Category("生活", R.drawable.life));
            allCategories.add(new Category("纪念日", R.drawable.miss));
            allCategories.add(new Category("工作", R.drawable.work));
            allCategories.addAll(categories);
            Map<String, Category> map = new LinkedHashMap<>();

            for (Category item : allCategories) {
                map.putIfAbsent(item.getName(), item);
            }

            List<Category> distinctList = new ArrayList<>(map.values());

            if (adapter != null) {
                adapter.submitList(distinctList);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}