/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.miappgcm.appfactory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MiInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Se llama cuando Gcm servers actualizan el registration token, principalemnte por motivos  de seguridad
     */
    @Override
    public void onTokenRefresh() {
        //obtener nuevamente el token y enviarlo a la aplicacion servidor
        RegitroGcmcAsyncTask regitroGcmcAsyncTask = new RegitroGcmcAsyncTask();
        regitroGcmcAsyncTask.execute();
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object>
    {

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
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(),progress[0],Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object result)
        {
            if(result instanceof  String)
            {
                String resulatado = (String)result;
                Toast.makeText(getApplicationContext(), "Registro exitoso. " + resulatado, Toast.LENGTH_SHORT).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
