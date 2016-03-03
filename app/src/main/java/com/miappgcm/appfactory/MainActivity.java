package com.miappgcm.appfactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.miappgcm.appfactory.R;


public class MainActivity extends ActionBarActivity {

    ProgressDialog mProgressDialog;
    TextView textViewImei;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        textViewImei = (TextView)findViewById(R.id.imeiTextView);
        textViewImei.setText(Utilidades.DameIMEI(this));
    }

    public void OnClick_RegitroButton(View v)
    {
        try
        {
            if(!Utilidades.CheckPlayServices(this))
                throw new Exception("No se encuetra Google Play Services");

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);

            RegitroGcmcAsyncTask regitroGcmcAsyncTask = new RegitroGcmcAsyncTask();
            regitroGcmcAsyncTask.execute();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Utilidades.MostrarAlertDialog(context, ex.getMessage(), "ERROR",R.mipmap.ic_error).show();
        }
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Registrando en aplicacion servidor...");
            mProgressDialog.show();
        }

        @Override
        protected Object doInBackground(String ... params) {

            try {

                publishProgress("Obteniendo Registration Token en GCM Servers...");
                String registrationToken = Utilidades.ObtenerRegistrationTokenEnGcm(getApplicationContext());

                publishProgress("Enviando Registration a mi aplicacion servidor...");
                String respuesta = Utilidades.RegistrarseEnAplicacionServidor(getApplicationContext(),registrationToken);
                return respuesta;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
        }

        @Override
        protected void onPostExecute(Object result)
        {
            mProgressDialog.dismiss();

            if(result instanceof  String)
            {
                String resulatado = (String)result;
                Utilidades.MostrarAlertDialog(context, "Registro exitoso. " + resulatado, "GCM", R.mipmap.ic_ok).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                Utilidades.MostrarAlertDialog(context, ex.getMessage(), "ERROR",R.mipmap.ic_error).show();
            }
        }

    }


}
