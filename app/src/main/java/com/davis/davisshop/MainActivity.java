package com.davis.davisshop;

/*All copyrights goes to @Gilberto_Davis
* Created the 28/06/2018
* Contact: peter01@hotmail.es
* Github: GilbDavis*/

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    private Button insert;
    private int stock;
    private float precio;
    private EditText et1, et2, et3, et4, et5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insert = findViewById(R.id.btnInsertar);

        et1 = findViewById(R.id.nJuego);
        et2 = findViewById(R.id.nCategoria);
        et3 = findViewById(R.id.nPlataforma);
        et4 = findViewById(R.id.nStock);
        et5 = findViewById(R.id.nPrecio);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Insertar().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.Buscar:
                new Buscar().execute();
                return true;

            case R.id.Eliminar:
                new Eliminar().execute();
                return true;

            case R.id.Actualizar:
                new Actualizar().execute();
                return true;

            case R.id.Limpiar:
                et1.setText("");
                et2.setText("");
                et3.setText("");
                et4.setText("");
                et5.setText("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class Insertar extends AsyncTask<String, String, String> {

        static final String NAMESPACE = "http://Servicio/";
        static final String METHODNAME = "InsertarJuegos";
        static final String URL = "http://10.0.2.2:8080/ProyectoAndroid/Gaming?wsdl";
        static final String SOAP_ACTION = NAMESPACE + METHODNAME;
        private String nombre, categoria, plataforma;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Proceso");
            dialog.setMessage("Insertando...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Insertado!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {

            nombre = et1.getText().toString();
            categoria = et2.getText().toString();
            plataforma = et3.getText().toString();
            stock = Integer.parseInt(et4.getText().toString());
            precio = Float.parseFloat(et5.getText().toString());

            SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

            request.addProperty("arg0", nombre);
            request.addProperty("arg1", categoria);
            request.addProperty("arg2", plataforma);
            request.addProperty("arg3", stock);
            request.addProperty("arg4", precio);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            new MarshalFloat().register(envelope);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            Log.d("transporte", request.toString());
            try{
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                Log.d("response", response.toString());
            }catch(Exception e){
                Log.d("eXXX", e.getMessage());
            }

            return null;
        }
    }

    private class Buscar extends AsyncTask<String, String, String> {

        static final String NAMESPACE = "http://Servicio/";
        static final String METHODNAME = "BuscarJuego";
        static final String URL = "http://10.0.2.2:8080/ProyectoAndroid/Gaming?wsdl";
        static final String SOAP_ACTION = NAMESPACE + METHODNAME;
        String rCategoria, rPlataforma;
        int rStock;
        float rPrecio;
        String nombre = et1.getText().toString();

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Proceso");
            dialog.setMessage("Buscando...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

            request.addProperty("arg0", nombre);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            Log.d("transporte", request.toString());
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                int count = response.getPropertyCount();
                for (int i = 0; i < count; i++) {
                    rCategoria = response.getProperty("categoria").toString().replace("anyType{}", "");
                    rPlataforma = response.getProperty("plataforma").toString().replace("anyType{}", "");
                    rStock = Integer.parseInt(response.getProperty("stock").toString().replace("anyType{}", ""));
                    rPrecio = Float.parseFloat(response.getProperty("precio").toString().replace("anyType", ""));
                }

                Log.d("response", response.toString());
            } catch (Exception e) {
                Log.d("eXXX", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            et2.setText("" + rCategoria);
            et3.setText("" + rPlataforma);
            et4.setText("" + rStock);
            et5.setText("" + rPrecio);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Encontrado!", Toast.LENGTH_SHORT).show();
        }
    }

    private class Eliminar extends AsyncTask<String, String, String>{

        static final String NAMESPACE = "http://Servicio/";
        static final String METHODNAME = "eliminarJuegos";
        static final String URL = "http://10.0.2.2:8080/ProyectoAndroid/Gaming?wsdl";
        static final String SOAP_ACTION = NAMESPACE + METHODNAME;
        String nombre = et1.getText().toString();
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

            request.addProperty("arg0", nombre);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            Log.d("transporte", request.toString());
            try{
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                Log.d("response", response.toString());
            }catch(Exception e){
                Log.d("eXXX", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Proceso");
            dialog.setMessage("Eliminando...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Eliminado!", Toast.LENGTH_SHORT).show();
        }
    }

    private class Actualizar extends AsyncTask<String, String, String>{

        static final String NAMESPACE = "http://Servicio/";
        static final String METHODNAME = "ActualizarJuegos";
        static final String URL = "http://10.0.2.2:8080/ProyectoAndroid/Gaming?wsdl";
        static final String SOAP_ACTION = NAMESPACE + METHODNAME;
        String nombre = et1.getText().toString();
        String categoria = et2.getText().toString();
        String plataforma = et3.getText().toString();
        int stock = Integer.parseInt(et4.getText().toString());
        float precio = Float.parseFloat(et5.getText().toString());
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

            request.addProperty("arg0", nombre);
            request.addProperty("arg1", categoria);
            request.addProperty("arg2", plataforma);
            request.addProperty("arg3", stock);
            request.addProperty("arg4", precio);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            new MarshalFloat().register(envelope);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            Log.d("transporte", request.toString());
            try{
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                Log.d("response", response.toString());
            }catch(Exception e){
                Log.d("eXXX", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Proceso");
            dialog.setMessage("Actualizando...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Actualizado!", Toast.LENGTH_SHORT).show();
        }
    }
}
