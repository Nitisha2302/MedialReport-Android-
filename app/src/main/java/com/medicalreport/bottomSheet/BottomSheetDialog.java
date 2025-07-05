package com.medicalreport.bottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medicalreport.R;


public class BottomSheetDialog extends BottomSheetDialogFragment {
    private static final String TAG = BottomSheetDialog.class.getSimpleName();
    private OnBottomItemClickListener onBottomItemClickListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //*Gallery Click
        view.findViewById(R.id.ivGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomItemClickListener.onGalleryClick();
                BottomSheetDialog.this.dismiss();
            }
        });

        //*Camera Click
        view.findViewById(R.id.ivCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomItemClickListener.onCameraClick();
                BottomSheetDialog.this.dismiss();
            }
        });

        //*Cancel Click
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomItemClickListener.onCancelBottomSheet();
                BottomSheetDialog.this.dismiss();
            }
        });
    }

    public void setActionListener(OnBottomItemClickListener onBottomItemClickListener) {
        this.onBottomItemClickListener = onBottomItemClickListener;
    }

    public interface OnBottomItemClickListener {

        void onCameraClick();

        void onGalleryClick();

        void onCancelBottomSheet();

    }
}
