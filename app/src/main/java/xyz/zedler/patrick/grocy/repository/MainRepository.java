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

package xyz.zedler.patrick.grocy.repository;

import android.app.Application;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import xyz.zedler.patrick.grocy.database.AppDatabase;
import xyz.zedler.patrick.grocy.model.Server;

public class MainRepository {

  private final AppDatabase appDatabase;

  public MainRepository(Application application) {
    this.appDatabase = AppDatabase.getAppDatabase(application);
  }

  public void clearAllTables() {
    Single
        .just(0)
        .doFinally(appDatabase::clearAllTables)
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

  public Single<List<Server>> getServers() {
    return appDatabase.serverDao().getServers();
  }

  public Single<Server> getServerById(String id) {
    return appDatabase.serverDao().getServerById(id);
  }

  public Single<Long> insertServer(Server server) {
    return appDatabase.serverDao().insertServer(server);
  }

  public Single<Integer> updateServer(Server server) {
    return appDatabase.serverDao().updateServer(server);
  }

  public Single<Integer> deleteServerById(String id) {
    return appDatabase.serverDao().deleteServerById(id);
  }

  public Single<Integer> clearDefaults() {
    return appDatabase.serverDao().clearDefaults();
  }

  public Single<Integer> setDefaultServer(String id) {
    return appDatabase.serverDao().setDefaultServer(id);
  }

  public Single<Integer> updateLastUsedTimestamp(String id, long timestamp) {
    return appDatabase.serverDao().updateLastUsedTimestamp(id, timestamp);
  }

  public interface OnVersionListener {
    void onVersion(int version);
  }
}
