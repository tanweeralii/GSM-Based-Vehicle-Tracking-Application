package com.scelon.vehicletracking.OtherActivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.scelon.vehicletracking.Classes.MobileVerification;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.AuthEncrypter;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.ImpMethods;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private Context context;
    private static final String TAG = "RegisterActivity";

    private TextView login_link;
    private TextInputEditText et_email, et_mobileNo, et_contact_person;//, et_CaCode; //et_gst_no
    private MaterialCardView btn_submit;

    //private Toolbar toolbar;

    private ProgressDialog verifyingOtpDialog;

    private MobileVerification mobileVerification;

    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;
        mobileVerification = new MobileVerification(context, findViewById(R.id.rootView));

        firebaseDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE);

        /*toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("Register");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

        login_link = findViewById(R.id.login_link);
        //et_gst_no = findViewById(R.id.et_gst_no);
        et_email = findViewById(R.id.et_email);
        et_mobileNo = findViewById(R.id.et_mobileNo);
        et_contact_person = findViewById(R.id.et_name);
        //btn_verify_mobile = findViewById(R.id.btn_verify_mobile);
        btn_submit = findViewById(R.id.btn_submit);

        verifyingOtpDialog = new ProgressDialog(context);
        verifyingOtpDialog.setMessage("Verifying...");
        verifyingOtpDialog.setCancelable(false);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String mGSTNo = et_gst_no.getText().toString().trim();
                String mEmail = et_email.getText().toString().trim();
                String mMobileNo = et_mobileNo.getText().toString().trim();
                String mContactPerson = et_contact_person.getText().toString().trim();
                //String mCACode = et_CaCode.getText().toString().trim();

                int check = 0;

                ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(false);

                ((TextInputLayout) et_email.getParent().getParent()).setErrorEnabled(false);

                ((TextInputLayout) et_contact_person.getParent().getParent()).setErrorEnabled(false);

                //((TextInputLayout) et_CaCode.getParent().getParent()).setErrorEnabled(false);

                if (mContactPerson.equals("")) {
                    ((TextInputLayout) et_contact_person.getParent().getParent()).setError("Field Required");
                    ((TextInputLayout) et_contact_person.getParent().getParent()).setErrorEnabled(true);
                    check++;
                }

                /*if (mCACode.equals("")) {
                    ((TextInputLayout) et_CaCode.getParent().getParent()).setError("Field Required");
                    ((TextInputLayout) et_CaCode.getParent().getParent()).setErrorEnabled(true);
                    check++;
                }*/

                if (!ImpMethods.isMobileNoValid(mMobileNo)) {
                    Log.d(TAG, "onClick: if");
                    ((TextInputLayout) et_mobileNo.getParent().getParent()).setError("Invalid Mobile No.");
                    ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(true);
                    check++;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    Log.d(TAG, "where: 3");
                    ((TextInputLayout) et_email.getParent().getParent()).setError("Invalid Email Address");
                    ((TextInputLayout) et_email.getParent().getParent()).setErrorEnabled(true);
                    check++;
                }

                if (check == 0) {
                    Log.d(TAG, "where: 6");

                    verifyMobileNo(mMobileNo , mEmail, mContactPerson , findViewById(R.id.rootView));
                }
            }
        });

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void registerUser(String mobileNo, String email, String contactPerson, View view) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Registering...");
        dialog.setCancelable(false);
        dialog.show();

        String encryptedData = AuthEncrypter.Encrypt(mobileNo).trim();
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.USER_NAME, contactPerson);
        data.put(Constants.USER_EMAIL, email);
        data.put(Constants.USER_MOBILE_NO, encryptedData);

        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(encryptedData).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
                dialog.dismiss();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                dialog.dismiss();
                Log.d(TAG, "onCanceled: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d(TAG, "onFailure: " + e.toString());
                Snackbar.make(ImpMethods.getViewFromContext(context), "Network Problem! Please try again.", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private int otpVerifiedFlag = 0;
    private String previousSuccessfulNo = "";

    private void verifyMobileNo(String phone_no, String email, String contactPerson, View view) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Verifying...");
        dialog.setCancelable(false);
        dialog.show();
        Log.d(TAG, "verifyMobileNo: " + phone_no);
        //Log.d(TAG, "verifyMobileNo: " + ccp.getSelectedCountryCodeWithPlus());
        phone_no = "+91" + phone_no;
        Log.d(TAG, "verifyMobileNo: " + phone_no);

        String decryptedData = AuthEncrypter.Encrypt(phone_no).trim();
        String finalPhone_no = phone_no;
        FirebaseDatabase.getInstance(Constants.FIREBASE_REFERENCE).getReference().child("Users").child(decryptedData).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dialog.dismiss();

                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        Toast.makeText(context, "Mobile No. already registered!", Toast.LENGTH_SHORT).show();
                    }else{
                        mobileVerification.verifyOtpDialog(finalPhone_no);
                        mobileVerification.addOnMobileVerificationFinished(new MobileVerification.OnMobileVerificationFinished() {
                            @Override
                            public void MobileVerificationFinished(int errorCode, boolean success, String mobileNo) {
                                previousSuccessfulNo = mobileNo;
                                Log.d(TAG, "MobileVerificationFinished: " + mobileNo);
                                if (success) {
                                    ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(false);

                                    ((TextInputLayout) et_mobileNo.getParent().getParent()).setEndIconDrawable(R.drawable.correct);
                                    ((TextInputLayout) et_mobileNo.getParent().getParent()).setEndIconVisible(true);

                                    registerUser(mobileNo, email, contactPerson, view);

                                    otpVerifiedFlag = 1;
                                } else {
                                    otpVerifiedFlag = 0;
                                    switch (errorCode) {
                                        case MobileVerification.OTP_DIALOG_CLOSED:
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(true);
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setError("Verification Required");
                                            break;
                                        case MobileVerification.OTP_SENT_LIMIT_END:
                                            //tooManyRequestFlag = 1;
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(true);
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setError("Too Many Failed Attempts.\nTry again Later!");
                                            //btn_verify_mobile.setEnabled(false);
                                            break;
                                        case MobileVerification.OTP_VERIFICATION_FAILED:
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setEndIconActivated(false);
                                            break;
                                        case MobileVerification.INVALID_MOBILE_NO:
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setErrorEnabled(true);
                                            ((TextInputLayout) et_mobileNo.getParent().getParent()).setError("Invalid Mobile No.");
                                            break;
                                        case MobileVerification.OTP_VERIFICATION_FAILURE:
                                            break;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                dialog.dismiss();
                Log.d(TAG, "onCanceled: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

    }

}