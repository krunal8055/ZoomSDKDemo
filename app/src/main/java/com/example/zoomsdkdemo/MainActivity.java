package com.example.zoomsdkdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static com.zipow.videobox.ConfActivity.startMeeting;

public class MainActivity extends AppCompatActivity {

    Button join_btn,login_join_btn;

    private ZoomSDKAuthenticationListener authenticationListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long l) {
            if (l == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(MainActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {

        }

        @Override
        public void onZoomIdentityExpired() {

        }

        @Override
        public void onZoomAuthIdentityExpired() {

        }
    };

    private void startMeeting(MainActivity mainActivity) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        join_btn = findViewById(R.id.join_btn);
        login_join_btn = findViewById(R.id.login_join_btn);

        initSDK(this);

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createJoinMeetingDialog();
            }
        });

        login_join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLoginDialog();
            }
        });
    }

    //For Login Dialog
    private void createLoginDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login)
                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        //TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
                        //TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
                        EditText emailInput = dialog.findViewById(R.id.email_input);
                        EditText passwordInput = dialog.findViewById(R.id.pw_input);
                        if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                            String email = emailInput.getText().toString();
                            String password = passwordInput.getText().toString();
                            if (email.trim().length() > 0 && password.trim().length() > 0) {
                                login(email, password);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //For Join Meeting Dialog
    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_join)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        //TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
                        //TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);

                        EditText numberInput = dialog.findViewById(R.id.meeting_no_input);
                        EditText passwordInput = dialog.findViewById(R.id.password_input);

                        if (numberInput != null && numberInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                            String meetingNumber = numberInput.getText().toString();
                            String password = passwordInput.getText().toString();
                            if (meetingNumber.trim().length() > 0 && password.trim().length() > 0) {
                                joinMeeting(MainActivity.this, meetingNumber, password);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initSDK(MainActivity mainActivity) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = Credentials.SDK_KEY;
        params.appSecret = Credentials.SDK_SECRET;
        //params.jwtToken = Credentials.SDK_JWT;
        params.domain = Credentials.SDK_DOMAIN;
        params.enableLog = true;
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) { }

            @Override
            public void onZoomAuthIdentityExpired() { }
        };
        zoomSDK.initialize(mainActivity, listener, params);

        if(zoomSDK.isInitialized())
        {
            Toast.makeText(mainActivity,"Initialized Successfully!",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(mainActivity,"Initialized not Successfully!",Toast.LENGTH_LONG).show();
        }
    }

    //JOIN Meeting Without Login Just Using Meeting No and Password
    public void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ""; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    //Login Using Zoom id and Password Using Zoom SDK
    public void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authenticationListener);
        }
    }

    //Start Meeting Using meeting No and password
    public void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }
}