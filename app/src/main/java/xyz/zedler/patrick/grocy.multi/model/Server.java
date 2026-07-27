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

package xyz.zedler.patrick.grocy.multi.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

@Entity(tableName = "server_table")
public class Server implements Parcelable {

  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id")
  @NonNull
  private String id;

  @ColumnInfo(name = "alias")
  @SerializedName("alias")
  private String displayName;

  @ColumnInfo(name = "grocy_server_url")
  @SerializedName("grocy_server_url")
  private String grocyServerUrl;

  @ColumnInfo(name = "grocy_api_key")
  @SerializedName("grocy_api_key")
  private String grocyApiKey;

  @ColumnInfo(name = "home_assistant_server_url")
  @SerializedName("home_assistant_server_url")
  private String homeAssistantServerUrl;

  @ColumnInfo(name = "home_assistant_token")
  @SerializedName("home_assistant_token")
  private String homeAssistantLongLivedToken;

  @ColumnInfo(name = "home_assistant_ingress_session_key")
  @SerializedName("home_assistant_ingress_session_key")
  private String homeAssistantIngressSessionKey;

  @ColumnInfo(name = "status")
  @SerializedName("status")
  private int status;

  @ColumnInfo(name = "last_used_timestamp")
  @SerializedName("last_used_timestamp")
  private long lastUsedTimestamp;

  @ColumnInfo(name = "is_default")
  @SerializedName("is_default")
  private boolean isDefault;

  public Server() {
  }  // for Room

  @Ignore
  public Server(Parcel parcel) {
    id = parcel.readString();
    displayName = parcel.readString();
    grocyServerUrl = parcel.readString();
    grocyApiKey = parcel.readString();
    homeAssistantServerUrl = parcel.readString();
    homeAssistantLongLivedToken = parcel.readString();
    homeAssistantIngressSessionKey = parcel.readString();
    status = parcel.readInt();
    lastUsedTimestamp = parcel.readLong();
    isDefault = parcel.readByte() != 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(displayName);
    dest.writeString(grocyServerUrl);
    dest.writeString(grocyApiKey);
    dest.writeString(homeAssistantServerUrl);
    dest.writeString(homeAssistantLongLivedToken);
    dest.writeString(homeAssistantIngressSessionKey);
    dest.writeInt(status);
    dest.writeLong(lastUsedTimestamp);
    dest.writeByte((byte) (isDefault ? 1 : 0));
  }

  public static final Creator<Server> CREATOR = new Creator<>() {

    @Override
    public Server createFromParcel(Parcel in) {
      return new Server(in);
    }

    @Override
    public Server[] newArray(int size) {
      return new Server[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getGrocyServerUrl() {
    return grocyServerUrl;
  }

  public void setGrocyServerUrl(String grocyServerUrl) {
    this.grocyServerUrl = grocyServerUrl;
  }

  public String getGrocyApiKey() {
    return grocyApiKey;
  }

  public void setGrocyApiKey(String grocyApiKey) {
    this.grocyApiKey = grocyApiKey;
  }

  public String getHomeAssistantServerUrl() {
    return homeAssistantServerUrl;
  }

  public void setHomeAssistantServerUrl(String homeAssistantServerUrl) {
    this.homeAssistantServerUrl = homeAssistantServerUrl;
  }

  public String getHomeAssistantLongLivedToken() {
    return homeAssistantLongLivedToken;
  }

  public void setHomeAssistantLongLivedToken(String homeAssistantLongLivedToken) {
    this.homeAssistantLongLivedToken = homeAssistantLongLivedToken;
  }

  public String getHomeAssistantIngressSessionKey() {
    return homeAssistantIngressSessionKey;
  }

  public void setHomeAssistantIngressSessionKey(String homeAssistantIngressSessionKey) {
    this.homeAssistantIngressSessionKey = homeAssistantIngressSessionKey;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getLastUsedTimestamp() {
    return lastUsedTimestamp;
  }

  public void setLastUsedTimestamp(long lastUsedTimestamp) {
    this.lastUsedTimestamp = lastUsedTimestamp;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Server server = (Server) o;
    return status == server.status && lastUsedTimestamp == server.lastUsedTimestamp && isDefault == server.isDefault && Objects.equals(id, server.id) && Objects.equals(displayName, server.displayName) && Objects
        .equals(grocyServerUrl, server.grocyServerUrl) && Objects
        .equals(grocyApiKey, server.grocyApiKey) && Objects
        .equals(homeAssistantServerUrl, server.homeAssistantServerUrl) && Objects
        .equals(homeAssistantLongLivedToken, server.homeAssistantLongLivedToken) && Objects
        .equals(homeAssistantIngressSessionKey, server.homeAssistantIngressSessionKey);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(id, displayName, grocyServerUrl, grocyApiKey, homeAssistantServerUrl, homeAssistantLongLivedToken, homeAssistantIngressSessionKey, status, lastUsedTimestamp, isDefault);
  }

  @NonNull
  @Override
  public String toString() {
    return "Server(" + grocyServerUrl + ")";
  }
}
