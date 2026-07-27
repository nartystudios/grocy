/*
 * This file is part of Grocy Android.
 *
 * Grocy Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grocy Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grocy Android. If not, see http://www.gnu.org/licenses/.
 *
 * Copyright (c) 2020-2024 by Patrick Zedler and Dominic Zedler
 * Copyright (c) 2024-2026 by Patrick Zedler
 */

package xyz.zedler.patrick.grocy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import xyz.zedler.patrick.grocy.Constants;
import xyz.zedler.patrick.grocy.R;
import xyz.zedler.patrick.grocy.activity.MainActivity;
import xyz.zedler.patrick.grocy.databinding.FragmentAddEditServerInstanceBinding;
import xyz.zedler.patrick.grocy.model.Server;
import xyz.zedler.patrick.grocy.util.ClickUtil;
import xyz.zedler.patrick.grocy.viewmodel.LoginRequestViewModel;

public class AddEditServerInstanceFragment extends BaseFragment {

  private static final String TAG = AddEditServerInstanceFragment.class.getSimpleName();

  private FragmentAddEditServerInstanceBinding binding;
  private MainActivity activity;
  private LoginRequestViewModel viewModel;
  private Server editingServer;
  private String currentNewServerId;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentAddEditServerInstanceBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    activity = (MainActivity) requireActivity();
    viewModel = new ViewModelProvider(this).get(LoginRequestViewModel.class);
    binding.setActivity(activity);
    binding.setFragment(this);
    binding.setClickUtil(new ClickUtil());
    binding.setLifecycleOwner(getViewLifecycleOwner());

    // Get server ID from arguments if editing
    String serverId = null;
    if (getArguments() != null) {
      serverId = getArguments().getString("serverId");
    }

    if (serverId != null) {
      // Edit mode
      viewModel.getServerByIdAsync(serverId, server -> {
        editingServer = server;
        if (editingServer != null) {
          binding.editTextDisplayName.setText(editingServer.getDisplayName());
          binding.editTextServerUrl.setText(editingServer.getGrocyServerUrl());
          binding.editTextApiKey.setText(editingServer.getGrocyApiKey());
          binding.editTextHaServerUrl.setText(editingServer.getHomeAssistantServerUrl());
          binding.editTextHaToken.setText(editingServer.getHomeAssistantLongLivedToken());
        }
      });
    } else {
      // Add mode - generate new server ID
      currentNewServerId = java.util.UUID.randomUUID().toString();
    }

    binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

    binding.btnSaveServer.setOnClickListener(v -> {
      String displayName = binding.editTextDisplayName.getText() != null 
          ? binding.editTextDisplayName.getText().toString().trim() 
          : "";
      String serverUrl = binding.editTextServerUrl.getText() != null 
          ? binding.editTextServerUrl.getText().toString().trim() 
          : "";
      String apiKey = binding.editTextApiKey.getText() != null 
          ? binding.editTextApiKey.getText().toString().trim() 
          : "";
      String haServerUrl = binding.editTextHaServerUrl.getText() != null 
          ? binding.editTextHaServerUrl.getText().toString().trim() 
          : null;
      String haToken = binding.editTextHaToken.getText() != null 
          ? binding.editTextHaToken.getText().toString().trim() 
          : null;

      if (serverUrl.isEmpty()) {
        Toast.makeText(activity, R.string.error_server_url_required, Toast.LENGTH_SHORT).show();
        return;
      }

      // Ensure URL has protocol
      if (!serverUrl.startsWith("http://") && !serverUrl.startsWith("https://")) {
        serverUrl = "https://" + serverUrl;
      }

      Server server;
      if (editingServer != null) {
        // Update existing server
        server = new Server();
        server.setId(editingServer.getId());
        server.setDisplayName(displayName.isEmpty() ? null : displayName);
        server.setGrocyServerUrl(serverUrl);
        server.setGrocyApiKey(apiKey.isEmpty() ? null : apiKey);
        server.setHomeAssistantServerUrl(haServerUrl);
        server.setHomeAssistantLongLivedToken(haToken);
        server.setStatus(editingServer.getStatus());
        server.setLastUsedTimestamp(editingServer.getLastUsedTimestamp());
        server.setDefault(editingServer.isDefault());
        
        viewModel.updateServer(server);
        Toast.makeText(activity, R.string.msg_server_updated, Toast.LENGTH_SHORT).show();
      } else {
        // Create new server
        server = new Server();
        server.setId(currentNewServerId);
        server.setDisplayName(displayName.isEmpty() ? null : displayName);
        server.setGrocyServerUrl(serverUrl);
        server.setGrocyApiKey(apiKey.isEmpty() ? null : apiKey);
        server.setHomeAssistantServerUrl(haServerUrl);
        server.setHomeAssistantLongLivedToken(haToken);
        server.setStatus(0); // Status: UNKNOWN
        server.setLastUsedTimestamp(0);
        server.setDefault(false);

        viewModel.insertServer(server);
        Toast.makeText(activity, R.string.msg_server_added, Toast.LENGTH_SHORT).show();
      }

      requireActivity().onBackPressed();
    });

    binding.btnDeleteServer.setOnClickListener(v -> {
      if (editingServer != null) {
        new MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.title_delete_server)
            .setMessage(R.string.msg_delete_server_confirm)
            .setPositiveButton(R.string.action_delete, (dialog, which) -> {
              performHapticHeavyClick();
              viewModel.deleteServerById(editingServer.getId());
              Toast.makeText(activity, R.string.msg_server_deleted, Toast.LENGTH_SHORT).show();
              requireActivity().onBackPressed();
            })
            .setNegativeButton(R.string.action_cancel, (dialog, which) -> performHapticClick())
            .setOnCancelListener(dialog -> performHapticClick())
            .create()
            .show();
      }
    });
  }
}
