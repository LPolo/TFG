package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

//Clase asociada a la parte de configuracion de la aplicación

public class Configuracion extends Activity {

	// Este activity solo consta de un botón y una lista
	private Button Atras;
	private ListView lista;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		CrearVisual();
		AsignarAtras();
	}

	// Metodo que carga toda la información en la lista
	private void CrearVisual() {
		// Asociamos el objeto con su homonimo en el activity
		lista = (ListView) findViewById(R.id.lista);
		try {
			// Creamos el objeto que guardara las preferencias y el editor que
			// las introducira
			final SharedPreferences config = getSharedPreferences(
					"Configuracion", Context.MODE_PRIVATE);
			final Editor editar = config.edit();
			// Objeto que enlaza esta activity con la base de datos
			final GestorBD gestor = new GestorBD(this);
			// Obtenemos la lista de localidades
			String[] listalocalidades = gestor.getLocalidades();
			// Nos creamos un Adaptador de listas con una celda que esta
			// integrada en android
			ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, listalocalidades);
			lista.setAdapter(adaptador);
			// Asignamos un listener para cuando el usuario haga click en una
			// localidad
			lista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				// Metodo que se ejecuta al pulsar una localidad
				public void onItemClick(AdapterView<?> adap, View view,
						int pos, long arg3) {
					// Obtenemos el nombre y código de la localidad que se ha
					// seleccionado
					String localidad = (String) adap.getItemAtPosition(pos);
					int cod = gestor.getCodigo(localidad);
					// Se comprueba si en las preferencias ya existia una
					// localidad
					if (config.contains("Localidad")) {
						// Si existe se elimina
						editar.remove("Localidad");
					}
					// Se introduce el código en la variable localidad de las
					// preferencias
					editar.putInt("Localidad", cod);
					// Se obliga a escribirlo
					editar.commit();
					// Se lanza un lanza un intent que ejecuta el menu principal
					// y se finaliza este activity
					Intent intent = new Intent(Configuracion.this,
							MainActivity.class);
					finish();
					startActivity(intent);

				}
			});
		} catch (RuntimeException exc) {
			String[] listaerror = new String[] { "Error, no se tiene la base de datos" };
			lista.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, listaerror));
		}

	}

	// Metodo por el cual se le asigna la función de ir atras al boton Atras
	private void AsignarAtras() {
		Atras = (Button) findViewById(R.id.BotonAtrasConfiguracion);
		SharedPreferences config = getSharedPreferences("Configuracion",
				Context.MODE_PRIVATE);
		if (config.getInt("Localidad", 0) == 0) {
			Atras.setEnabled(false);
		}
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(Configuracion.this,
						MainActivity.class);
				finish();
				startActivity(intent);
			}
		};
		Atras.setOnClickListener(listener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuracion, menu);
		return true;
	}

}
