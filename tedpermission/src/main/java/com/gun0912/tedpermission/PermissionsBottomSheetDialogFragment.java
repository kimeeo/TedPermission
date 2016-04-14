package com.gun0912.tedpermission;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;

/**
 * Created by BhavinPadhiyar on 14/04/16.
 */
public class PermissionsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
    private View rootView;
    public void setRootView(View rootView)
    {
        this.rootView = rootView;
    }
    public View getRootView()
    {
        return  rootView;
    }
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        if(getRootView()!=null) {
            View contentView = getRootView();
            dialog.setContentView(contentView);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            if (contentView.getParent() != null && ((View) contentView.getParent()).getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                if (behavior != null && behavior instanceof BottomSheetBehavior)
                    ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
        }
    }
}
