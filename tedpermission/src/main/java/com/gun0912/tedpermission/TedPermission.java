package com.gun0912.tedpermission;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.gun0912.tedpermission.util.Dlog;
import com.gun0912.tedpermission.util.ObjectUtils;

/**
 * Created by TedPark on 16. 2. 17..
 */
public class TedPermission {


    private static TedInstance instance;


    public TedPermission(Context context) {
         instance = new TedInstance(context);
    }





    public TedPermission setPermissionListener(PermissionListener listener) {

        instance.listener = listener;

        return this;
    }


    public TedPermission setPermissions(String... permissions) {

        instance.permissions = permissions;
        return this;
    }

    public TedPermission setRationaleMessage(String rationaleMessage) {

        instance.rationaleMessage = rationaleMessage;
        return this;
    }


    public TedPermission setRationaleMessage(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for RationaleMessage");

        instance.rationaleMessage = instance.context.getString(stringRes);
        return this;
    }



    public TedPermission setDeniedMessage(String denyMessage) {

        instance.denyMessage = denyMessage;
        return this;
    }



    public TedPermission setDeniedMessage(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for DeniedMessage");

        instance.denyMessage = instance.context.getString(stringRes);
        return this;
    }


    public TedPermission setRationaleView(@LayoutRes int view) {

        if (view <= 0)
            throw new IllegalArgumentException("Invalid value for DeniedView");

        instance.rationaleView = view;
        return this;
    }
    public TedPermission setDenyView(@LayoutRes int view) {

        if (view <= 0)
            throw new IllegalArgumentException("Invalid value for DeniedView");

        instance.denyView = view;
        return this;
    }




    public TedPermission setIgnoreSettings(String rationaleConfirmText) {
        instance.ignoreSettings = rationaleConfirmText;
        return this;
    }


    public TedPermission setIgnoreSettings(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for RationaleConfirmText");
        instance.ignoreSettings = instance.context.getString(stringRes);
        return this;
    }




    public TedPermission setRationaleConfirmText(String rationaleConfirmText) {

        instance.rationaleConfirmText = rationaleConfirmText;
        return this;
    }


    public TedPermission setRationaleConfirmText(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for RationaleConfirmText");


        instance.rationaleConfirmText = instance.context.getString(stringRes);

        return this;
    }



    public TedPermission setRationaleDenyText(String rationaleConfirmText) {

        instance.rationaleConfirmText = rationaleConfirmText;
        return this;
    }


    public TedPermission setRationaleDenyText(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for RationaleConfirmText");


        instance.rationaleDenyText = instance.context.getString(stringRes);

        return this;
    }
    public TedPermission setTedActivity(Class<TedPermissionActivity> tedActivity) {
        instance.tedActivity = tedActivity;
        return this;
    }




    public TedPermission setDeniedCloseButtonText(String deniedCloseButtonText) {

        instance.deniedCloseButtonText = deniedCloseButtonText;
        return this;
    }

    public TedPermission setShowDeniedViewInBottomSheet(boolean value) {

        instance.showDeniedViewInBottomSheet = value;
        return this;
    }



    public TedPermission setDeniedCloseButtonText(@StringRes int stringRes) {

        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for DeniedCloseButtonText");


        instance.deniedCloseButtonText = instance.context.getString(stringRes);

        return this;
    }


    public void check() {


        if (instance.listener == null) {
            throw new NullPointerException("You must setPermissionListener() on TedPermission");
        } else if (ObjectUtils.isEmpty(instance.permissions)) {
            throw new NullPointerException("You must setPermissions() on TedPermission");
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Dlog.d("preMarshmallow");
            instance.listener.onPermissionGranted();

        } else {
            Dlog.d("Marshmallow");
            instance.checkPermissions();
        }


    }


}
