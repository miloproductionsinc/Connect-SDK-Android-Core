/*
 * DevicePicker
 * Connect SDK
 *
 * Copyright (c) 2014 LG Electronics.
 * Created by Hyun Kook Khang on 19 Jan 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.connectsdk.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.connectsdk.R;

/**
 * ###Overview
 * The DevicePicker is provided by the DiscoveryManager as a simple way for you to present a list of available devices to your users.
 *
 * ###In Depth
 * By calling the getPickerDialog you will get a reference to the AlertDialog that will be updated automatically updated as compatible devices are discovered.
 */
public class DevicePicker {
    Activity activity;
    ConnectableDevice device;

    /**
     * Creates a new DevicePicker
     *
     * @param activity Activity that DevicePicker will appear in
     */
    public DevicePicker(Activity activity) {
        this.activity = activity;
    }

    public ListView getListView() {
        return new DevicePickerListView(activity);
    }

    /**
     * Sets a selected device.
     *
     * @param device Device that has been selected.
     */
    public void pickDevice(ConnectableDevice device) {
        this.device = device;
    }

    /**
     * Cancels pairing with the currently selected device.
     */
    public void cancelPicker() {
        if (device != null) {
            device.cancelPairing();
        }
        device = null;
    }

    /**
     * This method will return an AlertDialog that contains a ListView with an item for each discovered ConnectableDevice.
     *
     * @param message The title for the AlertDialog
     * @param listener The listener for the ListView to get the item that was clicked on
     */
    public AlertDialog getPickerDialog(String message, final OnItemClickListener listener, DialogInterface.OnCancelListener cancelListener) {

        View view = LayoutInflater.from(activity).inflate(R.layout.v_devices_list, null, false);
        View progressView = view.findViewById(R.id.progressView);
        DevicePickerListView listView = (DevicePickerListView) view.findViewById(R.id.deviceListView);
        listView.setProgressView(progressView);

        TextView title = (TextView) activity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        title.setText(message);

        final AlertDialog pickerDialog = new AlertDialog.Builder(activity)
                .setCustomTitle(title)
                .setOnCancelListener(cancelListener)
                .setView(view)
                .create();

        listView.setOnItemClickListener(new OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                listener.onItemClick(arg0, arg1, arg2, arg3);
                pickerDialog.dismiss();
            }
        });

        return pickerDialog;
    }
}
