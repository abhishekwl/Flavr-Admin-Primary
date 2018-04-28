package io.github.abhishekwl.flavradminprimary.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.R;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.signInLogoImageView) ImageView appLogoImageView;
    @BindView(R.id.signInEmailAddressEditText) TextInputEditText emailEditText;
    @BindView(R.id.signInPasswordEditText) TextInputEditText passwordEditText;

    private Unbinder unbinder;
    private MaterialDialog materialDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_sign_in);

        unbinder = ButterKnife.bind(SignInActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        Glide.with(getApplicationContext()).load(R.drawable.logo_white).into(appLogoImageView);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.signInNextButton)
    public void onSignInButtonPress() {
        String emailAddress = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) Snackbar.make(appLogoImageView, "Fields cannot be empty", Snackbar.LENGTH_SHORT).show();
        else processCredentials(emailAddress, password);
    }

    private void processCredentials(String emailAddress, String password) {
        materialDialog = new MaterialDialog.Builder(SignInActivity.this)
                .title("Flavr (Admin)")
                .content("Signing In...")
                .iconRes(R.drawable.logo_small)
                .progress(true, 0)
                .widgetColorRes(R.color.colorPrimaryDark)
                .show();

        firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (materialDialog.isShowing()) materialDialog.dismiss();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    if (materialDialog.isShowing()) materialDialog.dismiss();
                    Snackbar.make(appLogoImageView, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
