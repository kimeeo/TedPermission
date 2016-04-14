package com.gun0912.tedpermission;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.gun0912.tedpermission.busevent.TedBusProvider;
import com.gun0912.tedpermission.busevent.TedPermissionEvent;
import com.gun0912.tedpermission.util.Dlog;

import java.util.ArrayList;

/**
 * Created by TedPark on 16. 2. 17..
 */
public class TedPermissionActivity extends AppCompatActivity {

    public static final int REQ_CODE_PERMISSION_REQUEST = 10;
    public static final int REQ_CODE_REQUEST_SETTING = 20;


    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_RATIONALE_MESSAGE = "rationale_message";
    public static final String EXTRA_RATIONALE_VIEW = "rationale_view";

    public static final String EXTRA_DENY_MESSAGE = "deny_message";
    public static final String EXTRA_DENY_VIEW = "deny_view";

    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_RATIONALE_CONFIRM_TEXT = "rationale_confirm_text";
    public static final String EXTRA_IGNORE_SETTINGS= "ignore_settings";

    public static final String EXTRA_RATIONALE_DENY_TEXT = "rationale_deny_text";

    public static final String EXTRA_DENIED_DIALOG_CLOSE_TEXT = "denied_dialog_close_text";

    public static final String EXTRA_SHOW_DENIED_VIEW_IN_BOTTOM_SHEET = "show_denied_view_in_bottom_sheet";


    String rationale_message;
    int rationale_view;
    int deny_view;
    String denyMessage;
    String[] permissions;
    String packageName;
    String deniedCloseButtonText;
    String rationaleConfirmText;
    String rationaleDenyText;
    String ignoreSettings;
    Boolean showDeniedViewInBottomSheet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        setupFromSavedInstanceState(savedInstanceState);
        checkPermissions(false);
    }


    private void setupFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            permissions = savedInstanceState.getStringArray(EXTRA_PERMISSIONS);
            rationale_message = savedInstanceState.getString(EXTRA_RATIONALE_MESSAGE);
            rationale_view= savedInstanceState.getInt(EXTRA_RATIONALE_VIEW,-1);
            denyMessage = savedInstanceState.getString(EXTRA_DENY_MESSAGE);
            deny_view= savedInstanceState.getInt(EXTRA_DENY_VIEW,-1);

            packageName = savedInstanceState.getString(EXTRA_PACKAGE_NAME);

            rationaleConfirmText = savedInstanceState.getString(EXTRA_RATIONALE_CONFIRM_TEXT);

            ignoreSettings= savedInstanceState.getString(EXTRA_IGNORE_SETTINGS);

            rationaleDenyText= savedInstanceState.getString(EXTRA_RATIONALE_DENY_TEXT);
            deniedCloseButtonText = savedInstanceState.getString(EXTRA_DENIED_DIALOG_CLOSE_TEXT);

            showDeniedViewInBottomSheet=savedInstanceState.getBoolean(EXTRA_SHOW_DENIED_VIEW_IN_BOTTOM_SHEET);
        } else {

            Intent intent = getIntent();
            permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS);
            rationale_message = intent.getStringExtra(EXTRA_RATIONALE_MESSAGE);
            denyMessage = intent.getStringExtra(EXTRA_DENY_MESSAGE);
            packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);

            rationaleConfirmText = intent.getStringExtra(EXTRA_RATIONALE_CONFIRM_TEXT);
            ignoreSettings= intent.getStringExtra(EXTRA_IGNORE_SETTINGS);

            rationaleDenyText= intent.getStringExtra(EXTRA_RATIONALE_DENY_TEXT);
            deniedCloseButtonText = intent.getStringExtra(EXTRA_DENIED_DIALOG_CLOSE_TEXT);
            rationale_view= intent.getIntExtra(EXTRA_RATIONALE_VIEW,0);
            deny_view= intent.getIntExtra(EXTRA_DENY_VIEW,0);
            showDeniedViewInBottomSheet=intent.getBooleanExtra(EXTRA_SHOW_DENIED_VIEW_IN_BOTTOM_SHEET,true);

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(EXTRA_PERMISSIONS, permissions);
        outState.putString(EXTRA_RATIONALE_MESSAGE, rationale_message);
        outState.putString(EXTRA_DENY_MESSAGE, denyMessage);
        outState.putString(EXTRA_PACKAGE_NAME, packageName);

        outState.putString(EXTRA_DENIED_DIALOG_CLOSE_TEXT, deniedCloseButtonText);
        outState.putString(EXTRA_RATIONALE_CONFIRM_TEXT, rationaleConfirmText);
        outState.putString(EXTRA_RATIONALE_DENY_TEXT, rationaleDenyText);

        outState.putString(EXTRA_IGNORE_SETTINGS, ignoreSettings);

        outState.putInt(EXTRA_RATIONALE_VIEW,rationale_view);
        outState.putInt(EXTRA_DENY_VIEW,deny_view);
        outState.putBoolean(EXTRA_SHOW_DENIED_VIEW_IN_BOTTOM_SHEET,showDeniedViewInBottomSheet);

        super.onSaveInstanceState(outState);
    }


    private void permissionGranted() {
        TedBusProvider.getInstance().post(new TedPermissionEvent(true, null));
        finish();
        overridePendingTransition(0, 0);
    }

    private void permissionDenied(ArrayList<String> deniedpermissions) {
        TedBusProvider.getInstance().post(new TedPermissionEvent(false, deniedpermissions));
        finish();
        overridePendingTransition(0, 0);
    }


    private void checkPermissions(boolean fromOnActivityResult) {
        Dlog.d("");

        ArrayList<String> needPermissions = new ArrayList<>();


        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needPermissions.add(permission);
            }
        }


        boolean showRationale = false;

        for(String permission:needPermissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
            {
                showRationale=true;
                break;
            }
        }



        if (needPermissions.isEmpty()) {
            permissionGranted();
        }
        //From Setting Activity
        else if (fromOnActivityResult)
        {
            permissionDenied(needPermissions);
        }
        //Need Show Rationale
        else if (showRationale && rationale_view!=0) {
            showRationaleDialog(needPermissions,rationale_view);
        }
        else if (showRationale && !TextUtils.isEmpty(rationale_message)) {
            showRationaleDialog(needPermissions);
        }
        //Need Request Permissions
        else {
            requestPermissions(needPermissions);
        }
    }

    public void requestPermissions(ArrayList<String> needPermissions) {
        ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), REQ_CODE_PERMISSION_REQUEST);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        Dlog.d("");
        ArrayList<String> deniedPermissions = new ArrayList<>();


        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty())
            permissionGranted();
        else if(deny_view!=0)
            showPermissionDenyDialog(deniedPermissions,deny_view,showDeniedViewInBottomSheet);
        else if(!TextUtils.isEmpty(denyMessage))
            showPermissionDenyDialog(deniedPermissions);
        else
            permissionDenied(deniedPermissions);
    }

    private void showRationaleDialog(final ArrayList<String> needPermissions,int viewID) {
        try {

            boolean viewHasPositiveButton = false;
            boolean viewHasNegativeButton = false;
            View view = getLayoutInflater().inflate(viewID, null);

            if (view.findViewById(R.id.positiveButton) != null)
                viewHasPositiveButton = true;
            if (view.findViewById(R.id.negativeButton) != null)
                viewHasNegativeButton = true;

            AlertDialog.Builder b = new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(true);

            if (viewHasPositiveButton == false) {
                b.setPositiveButton(rationaleConfirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(needPermissions);
                    }
                });
            }

            if (viewHasNegativeButton == false && !TextUtils.isEmpty(rationaleDenyText)) {
                b.setNegativeButton(rationaleDenyText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionDenied(needPermissions);
                    }
                });
            }


            final AlertDialog alertDialog = b.show();
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    permissionDenied(needPermissions);
                }
            });
            if (view.findViewById(R.id.positiveButton) != null) {
                view.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermissions(needPermissions);
                        alertDialog.dismiss();
                    }
                });
            }

            if (view.findViewById(R.id.negativeButton) != null) {
                view.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionDenied(needPermissions);
                        alertDialog.dismiss();
                    }
                });
            }
        }catch (Exception e)
        {
            if(!TextUtils.isEmpty(rationale_message))
                showRationaleDialog(needPermissions);
        }
    }

    private void showRationaleDialog(final ArrayList<String> needPermissions) {
        new AlertDialog.Builder(this)
                .setMessage(rationale_message)
                .setCancelable(false)
                .setNegativeButton(rationaleConfirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(needPermissions);

                    }
                })
                .show();
    }
    public void showPermissionDenyDialog(final ArrayList<String> deniedPermissions,int viewID,boolean isBottomSheet)
    {
        View view = getLayoutInflater().inflate(viewID, null);



        if(isBottomSheet) {
            try
            {
                final PermissionsBottomSheetDialogFragment bottomSheetDialogFragment = new PermissionsBottomSheetDialogFragment();
                bottomSheetDialogFragment.setRootView(view);
                 Handler handler = new Handler();
                  final Runnable runnablelocal = new Runnable() {
                     @Override
                     public void run() {
                         bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                     }
                     };
                handler.postDelayed(runnablelocal, 400);

                if (view.findViewById(R.id.positiveButton) != null) {
                    view.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoSettings();
                            bottomSheetDialogFragment.dismiss();
                        }
                    });
                }

                if (view.findViewById(R.id.negativeButton) != null) {
                    view.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionDenied(deniedPermissions);
                            bottomSheetDialogFragment.dismiss();
                        }
                    });
                }

                if (view.findViewById(R.id.neutralButton) != null) {
                    view.findViewById(R.id.neutralButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionDenied(deniedPermissions);
                            bottomSheetDialogFragment.dismiss();
                        }
                    });
                }

            }
            catch (Exception e)
            {
                if (!TextUtils.isEmpty(denyMessage))
                    showPermissionDenyDialog(deniedPermissions);
            }

        }
        else {
            try {
                AlertDialog.Builder b = new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true);

                boolean viewHasPositiveButton = false;
                boolean viewHasNegativeButton = false;

                if (view.findViewById(R.id.positiveButton) != null)
                    viewHasPositiveButton = true;
                if (view.findViewById(R.id.negativeButton) != null)
                    viewHasNegativeButton = true;

                if (!TextUtils.isEmpty(denyMessage))
                    b.setTitle(denyMessage);

                if (viewHasPositiveButton == false) {
                    b.setPositiveButton(getString(R.string.tedpermission_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gotoSettings();
                        }
                    });
                }
                if (viewHasNegativeButton == false && !TextUtils.isEmpty(ignoreSettings)) {
                    b.setNegativeButton(ignoreSettings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            permissionDenied(deniedPermissions);
                        }
                    });


                }


                final AlertDialog alertDialog = b.show();

                if (!TextUtils.isEmpty(denyMessage))
                    alertDialog.setTitle(denyMessage);

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        permissionDenied(deniedPermissions);
                    }
                });


                if (view.findViewById(R.id.positiveButton) != null) {
                    view.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoSettings();
                            alertDialog.dismiss();
                        }
                    });
                }

                if (view.findViewById(R.id.negativeButton) != null) {
                    view.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionDenied(deniedPermissions);
                            alertDialog.dismiss();
                        }
                    });
                }
            } catch (Exception e) {
                if (!TextUtils.isEmpty(denyMessage))
                    showPermissionDenyDialog(deniedPermissions);
            }
        }
    }




    public void showPermissionDenyDialog(final ArrayList<String> deniedPermissions) {
        if (TextUtils.isEmpty(denyMessage)) {
            permissionDenied(deniedPermissions);
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setMessage(denyMessage)
                .setCancelable(false)

                .setNegativeButton(deniedCloseButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionDenied(deniedPermissions);
                    }
                });

        builder.setPositiveButton(getString(R.string.tedpermission_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoSettings();
            }
        });

        builder.show();

    }

    private void gotoSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + packageName));
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Dlog.e(e.toString());
            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_REQUEST_SETTING:
                checkPermissions(true);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
