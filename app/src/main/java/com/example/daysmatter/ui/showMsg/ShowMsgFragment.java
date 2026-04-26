package com.example.daysmatter.ui.showMsg;

import static java.lang.Math.abs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.daysmatter.R;
import com.example.daysmatter.databinding.FragmentShowMsgBinding;
import com.example.daysmatter.logic.room.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowMsgFragment extends Fragment {
    private FragmentShowMsgBinding binding;
    private ShowMsgViewModel viewModel;
    private int flag;

    public static final String PREFS_NAME = "AppBackground";
    public static final String PREFS_KEY = "background_uri";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ShowMsgViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowMsgBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 绑定观察者：UI 会随 ViewModel 数据变化自动更新
        viewModel.getTitleText().observe(getViewLifecycleOwner(), binding.showTitle::setText);
        viewModel.getTimeText().observe(getViewLifecycleOwner(), binding.showTime::setText);
        viewModel.getAimTimeText().observe(getViewLifecycleOwner(), binding.showAimtime::setText);

        initData();
        setOnClicked();
        loadSavedBackground();
    }

    private void initData() {
        if (getArguments() != null) {
            flag = getArguments().getInt("flag");
            viewModel.setMsg((Message) getArguments().getSerializable("msg_data"));
        }

        getParentFragmentManager().setFragmentResultListener("edit_msg_result", this, (key, b) -> {
            viewModel.setMsg((Message) b.getSerializable("msg_data"));
        });

        if (flag == 1) setupMenu();
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.show_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.editMsg) {
                    Bundle b = new Bundle();
                    b.putInt("result",1);
                    b.putSerializable("msg_data", viewModel.getMsg());
                    Navigation.findNavController(requireView()).navigate(R.id.action_showMsgFragment_to_addMsgFragment, b);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setOnClicked() {
        binding.changeBackground.setOnClickListener(v -> updateBackground());
        binding.share.setOnClickListener(v -> {
            Bitmap bitmap = captureView(binding.main);
            if (bitmap != null) {
                Uri imgUri = saveImgAndGetUri(requireContext(), bitmap);
                if (imgUri != null) shareImage(requireContext(), imgUri);
            }
        });
    }

    private void loadSavedBackground() {
        String uriString = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(PREFS_KEY, null);
        if (uriString != null) {
            Glide.with(this).load(Uri.parse(uriString)).centerCrop().error(R.color.purple_200).into(binding.backgroundImageView);
        }
    }

    private void updateBackground() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(PREFS_KEY, uri.toString()).apply();
                        Glide.with(this).load(uri).centerCrop().into(binding.backgroundImageView);
                    }
                }
            }
    );

    private Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private Uri saveImgAndGetUri(Context context, Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        if (!imageFolder.exists()) imageFolder.mkdirs();
        File file = new File(imageFolder, "shared_image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
        } catch (IOException e) { e.printStackTrace(); return null; }
        return FileProvider.getUriForFile(context, "com.example.countdown.fileprovider", file);
    }

    private void shareImage(Context context, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "分享图片到..."));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}