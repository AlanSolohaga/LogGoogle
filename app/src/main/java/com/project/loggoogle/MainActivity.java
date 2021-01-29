package com.project.loggoogle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView idTextView;
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idTextView = findViewById(R.id.id_user);
        /** Todo igual que en el login **/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /** Llamamos al metodo onStart para que al iniciar verifique de modo silencioso si ya existe una sesión iniciada**/
    @Override
    protected void onStart() {
        super.onStart();
        /**Aqui vemos si quedo pendiente una sesion GoogleSingnInResult y obtenemos ese resultado para analizar*/
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            /**Creamos este metodo para analizar el resultado**/
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        /**Si todo salió bien a traves de GoogleSignAccount obtenemos los atributos de la cuenta*/
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            //nameTextView.setText(account.getDisplayName());
            //emailTextView.setText(account.getEmail());
            idTextView.setText(account.getId());

            //Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);

        } else {
            /**Caso que no haya una sesión iniciada vamos a la otra activity para iniciar sesión*/
            goLogInScreen();
        }
    }

    private void goLogInScreen() {
        /**A Traves del intent vamos a la activity para iniciar sesión**/
        Intent intent = new Intent(this, LogGoogle.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**Metodo para el boton logOut**/
    public void logOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "NO SE PUDO CERRAR SESIÓN", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**Metodo para el boton revoke**/
    public void revoke(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(),"No se pudo salir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
