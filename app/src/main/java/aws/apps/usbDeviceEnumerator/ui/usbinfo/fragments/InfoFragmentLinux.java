/*******************************************************************************
 * Copyright 2011 Alexandros Schillings
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package aws.apps.usbDeviceEnumerator.ui.usbinfo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import aws.apps.usbDeviceEnumerator.R;
import uk.co.alt236.usbdeviceenumerator.UsbConstantResolver;
import uk.co.alt236.usbdeviceenumerator.sysbususb.SysBusUsbDevice;

public class InfoFragmentLinux extends BaseInfoFragment {
    public final static String DEFAULT_STRING = "???";
    private final static String EXTRA_DATA = InfoFragmentLinux.class.getName() + ".BUNDLE_DATA";
    private static final int LAYOUT_ID = R.layout.fragment_usb_info;
    private final String TAG = this.getClass().getName();
    private SysBusUsbDevice device;
    private boolean validData;

    private ViewHolder viewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        device = (SysBusUsbDevice) getArguments().getSerializable(EXTRA_DATA);
        final View view;

        if (device == null) {
            view = inflater.inflate(R.layout.fragment_error, container, false);
            validData = false;
        } else {
            view = inflater.inflate(LAYOUT_ID, container, false);
            validData = true;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        if (validData) {
            viewHolder = new ViewHolder(view);
            populateDataTable(LayoutInflater.from(getContext()));
        } else {
            final TextView textView = (TextView) view.findViewById(R.id.errorText);
            textView.setText(R.string.error_loading_device_info_unknown);
        }
    }

    private void populateDataTable(LayoutInflater inflater) {
        final String vid = padLeft(device.getVid(), "0", 4);
        final String pid = padLeft(device.getPid(), "0", 4);
        final String deviceClass = UsbConstantResolver.resolveUsbClass(device);

        viewHolder.getLogo().setImageResource(R.drawable.no_image);

        viewHolder.getVid().setText(vid);
        viewHolder.getPid().setText(pid);
        viewHolder.getDevicePath().setText(device.getDevicePath());
        viewHolder.getDeviceClass().setText(deviceClass);

        viewHolder.getReportedVendor().setText(device.getReportedVendorName());
        viewHolder.getReportedProduct().setText(device.getReportedProductName());

        final TableLayout bottomTable = viewHolder.getBottomTable();
        addDataRow(inflater, bottomTable, getString(R.string.usb_version_), device.getUsbVersion());
        addDataRow(inflater, bottomTable, getString(R.string.speed_), device.getSpeed());
        addDataRow(inflater, bottomTable, getString(R.string.protocol_), device.getDeviceProtocol());
        addDataRow(inflater, bottomTable, getString(R.string.maximum_power_), device.getMaxPower());
        addDataRow(inflater, bottomTable, getString(R.string.serial_number_), device.getSerialNumber());

        loadAsyncData(viewHolder, vid, pid, device.getReportedVendorName());
    }

    @Override
    public String getSharePayload() {
        return ShareUtils.getSharePayload(viewHolder);
    }

    public static Fragment create(final SysBusUsbDevice usbDevice) {
        final Fragment fragment = new InfoFragmentLinux();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_DATA, usbDevice);
        fragment.setArguments(bundle);
        return fragment;
    }
}
