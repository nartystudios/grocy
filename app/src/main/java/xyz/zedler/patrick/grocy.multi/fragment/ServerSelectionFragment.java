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

package xyz.zedler.patrick.grocy.multi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import xyz.zedler.patrick.grocy.multi.R;
import xyz.zedler.patrick.grocy.multi.activity.MainActivity;
import xyz.zedler.patrick.grocy.multi.databinding.FragmentServerSelectionBinding;
import xyz.zedler.patrick.grocy.multi.model.Server;
import xyz.zedler.patrick.grocy.multi.util.ClickUtil;
import xyz.zedler.patrick.grocy.multi.viewmodel.LoginRequestViewModel;

public class ServerSelectionFragment extends BaseFragment {

  private static final String TAG = ServerSelectionFragment.class.getSimpleName();

  private FragmentServerSelectionBinding binding;
  private MainActivity activity;
  private LoginRequestViewModel viewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentServerSelectionBinding.inflate(inflater, container, false);
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

    activity.updateBottomAppBar(false, R.menu.menu_empty);

    binding.toolbar.setNavigationOnClickListener(v -> activity.finish());

    binding.btnAddServer.setOnClickListener(v -> {
      performHapticClick();
      activity.navUtil.navigate(
          ServerSelectionFragmentDirections.actionServerSelectionFragmentToAddEditServerInstanceFragment()
      );
    });

    viewModel.getServersLiveData().observe(getViewLifecycleOwner(), servers -> {
      if (servers != null && !servers.isEmpty()) {
        binding.recyclerViewServers.setVisibility(View.VISIBLE);
        binding.textViewNoServers.setVisibility(View.GONE);

        // For now, show the first default server or the latest used server
        Server activeServer = null;
        for (Server server : servers) {
          if (server.isDefault()) {
            activeServer = server;
            break;
          }
        }
        
        if (activeServer == null && !servers.isEmpty()) {
          activeServer = servers.get(0);
        }

        final Server finalActiveServer = activeServer;

        if (finalActiveServer != null) {
          binding.btnSelectServer.setText(finalActiveServer.getDisplayName() != null 
              ? finalActiveServer.getDisplayName() 
              : finalActiveServer.getGrocyServerUrl());
          binding.btnSelectServer.setVisibility(View.VISIBLE);
          binding.btnSelectServer.setOnClickListener(v -> {
            performHapticHeavyClick();
            viewModel.selectServerInstance(finalActiveServer);
            requireActivity().onBackPressed();
          });
        }
      } else {
        binding.recyclerViewServers.setVisibility(View.GONE);
        binding.textViewNoServers.setVisibility(View.VISIBLE);
        binding.btnSelectServer.setVisibility(View.GONE);
      }
    });
  }

  public void selectServerInstance(Server server) {
    viewModel.selectServerInstance(server);
    requireActivity().onBackPressed();
  }
}
