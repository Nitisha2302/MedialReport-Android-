package com.medicalreport.bottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medicalreport.R;

public class DevicesBottomSheetDialog extends BottomSheetDialogFragment {

    AppCompatTextView tvDeviceName;
    private OnDeviceBottomItemClickListener onDeviceBottomItemClickListener;

    private String deviceName = "";




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_devices_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvDeviceName.setText(deviceName);
        //*Device Click
        view.findViewById(R.id.ivDeviceName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceBottomItemClickListener.onDeviceClick();
                DevicesBottomSheetDialog.this.dismiss();
            }
        });

        //*Camera Click
        view.findViewById(R.id.ivCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceBottomItemClickListener.onDeviceCameraClick();
                DevicesBottomSheetDialog.this.dismiss();
            }
        });

        //*Cancel Click
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceBottomItemClickListener.onDeviceCancelBottomSheet();
                DevicesBottomSheetDialog.this.dismiss();
            }
        });*/
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceActionListener(OnDeviceBottomItemClickListener onDeviceBottomItemClickListener) {
        this.onDeviceBottomItemClickListener = onDeviceBottomItemClickListener;
    }

    public interface OnDeviceBottomItemClickListener {
        void onDeviceClick();

        void onDeviceCameraClick();

        void onDeviceCancelBottomSheet();
    }
}
