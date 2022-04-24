package com.scelon.vehicletracking.OtherActivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.scelon.vehicletracking.Activities.MainActivity;
import com.scelon.vehicletracking.Classes.MobileVerification;
import com.scelon.vehicletracking.R;
import com.scelon.vehicletracking.Utils.AuthEncrypter;
import com.scelon.vehicletracking.Utils.Constants;
import com.scelon.vehicletracking.Utils.ImpMethods;
import com.scelon.vehicletracking.Utils.TinyDB;

public class SignInActivity extends AppCompatActivity {
    private Context context;
    private static final String TAG = "SignInActivity";

    private TinyDB tinyDB;
    private TextView register_link;
    private EditText et_phone_no;
    private MaterialCardView btn_submit_login;

    private MobileVerification mobileVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = this;
        tinyDB = new TinyDB(context);

        mobileVerification = new MobileVerification(context, ImpMethods.getViewFromContext(context));

        et_phone_no = findViewById(R.id.et_phone_no);
        register_link = findViewById(R.id.register_link);
        btn_submit_login = findViewById(R.id.btn_submit_login);

        btn_submit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_no = et_phone_no.getText().toString().trim();
                Log.d(TAG, "onCreate: " + AuthEncrypter.Encrypt("9269501501"));
                Log.d(TAG, "onCreate: " + AuthEncrypter.Decrypt(AuthEncrypter.Encrypt("9269501501")));
                if (!ImpMethods.isMobileNoValid(phone_no)) {
                    Snackbar.make(view, "Invalid Mobile No. !!", Snackbar.LENGTH_SHORT).show();
                } else {
                    //MainLoader.Loader(true , findViewById(R.id.LL_loader));
                    //SignIn(phone_no , view);
                    verifyMobileNo(phone_no);
                }
            }
        });

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verifyMobileNo(String phone_no) {
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
                //Toast.makeText(context, ""+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    //Toast.makeText(context, "" + snapshot.exists(), Toast.LENGTH_SHORT).show();
                    if (snapshot.exists()) {
                        tinyDB.putString(Constants.USER_MOBILE_NO , decryptedData);
                        mobileVerification.verifyOtpDialog(finalPhone_no);
                        mobileVerification.addOnMobileVerificationFinished(new MobileVerification.OnMobileVerificationFinished() {
                            @Override
                            public void MobileVerificationFinished(int errorCode, boolean success, String mobileNo) {
                                //previousSuccessfulNo = mobileNo;
                                Log.d(TAG, "MobileVerificationFinished: " + mobileNo);
                                if (success) {
                                    tinyDB.putInt(Constants.LOGIN_FLAG, 1);

                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                    //registerUser(mobileNo, email, contactPerson, view);

                                    //btn_verify_mobile.setEnabled(false);
                                    //et_phone_no.setEnabled(false);
                                    //btn_verify_mobile.setImageResource(R.drawable.ic_edit);
                                    //btn_verify_mobile.setTag(R.drawable.ic_edit);
                                    //otpVerifiedFlag = 1;
                                } else {
                                    //otpVerifiedFlag = 0;
                                    switch (errorCode) {
                                        case MobileVerification.OTP_DIALOG_CLOSED:
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setErrorEnabled(true);
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setError("Verification Required");
                                            Toast.makeText(context, "Verification Required", Toast.LENGTH_SHORT).show();
                                            break;
                                        case MobileVerification.OTP_SENT_LIMIT_END:
                                            //tooManyRequestFlag = 1;
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setErrorEnabled(true);
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setError("Too Many Failed Attempts.\nTry again Later!");
                                            //btn_verify_mobile.setEnabled(false);
                                            Toast.makeText(context, "Too Many Failed Attempts.\\nTry again Later!", Toast.LENGTH_SHORT).show();
                                            break;
                                        case MobileVerification.OTP_VERIFICATION_FAILED:
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setEndIconActivated(false);
                                            break;
                                        case MobileVerification.INVALID_MOBILE_NO:
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setErrorEnabled(true);
                                            //((TextInputLayout) et_phone_no.getParent().getParent()).setError("Invalid Mobile No.");
                                            Toast.makeText(context, "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case MobileVerification.OTP_VERIFICATION_FAILURE:
                                            break;
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, "Mobile No. not registered!", Toast.LENGTH_SHORT).show();
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