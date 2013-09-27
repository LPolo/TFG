package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


//Clase asociada a la parte de buscar dentro de la base de datos

public class Buscador extends Activity {
	
	// Establecemos los objetos que tiene el activity
	private EditText texto;
	private Button Buscar;
	private Button Atras;
	
	//Guarda el codigo de la localidad en la que tenemos que buscar
	private int localidad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscador);
		//Obtenemos la informacion que nos ha pasado el anterior activity
		Bundle informacion = getIntent().getExtras();	
		localidad=informacion.getInt("Localidad");
		texto = (EditText) findViewById(R.id.textobuscar);
		AsignarBuscar();
		AsignarAtras();
	}

	//Metodo en el cual asignamos la funcion atras al boton Atras
	private void AsignarAtras() {
		Atras = (Button) findViewById(R.id.BotonAtrasBuscar);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				finish();
			}
		};
		Atras.setOnClickListener(listener);
		
	}

	//Metodo en el cual le asignamos la funcionalidad al boton buscar
	private void AsignarBuscar() {
		Buscar = (Button) findViewById(R.id.BotonBuscar);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				//Creamos un intent en el cual le pasamos los valores de la busqueda
				Intent intent = new Intent(Buscador.this,
						VerCatalogo.class);
				intent.putExtra("Localidad", localidad);
				intent.putExtra("EsCategoria", false);
				intent.putExtra("EsBusqueda", true);
				intent.putExtra("Busqueda", texto.getText().toString());
				//Comenzamos el intento
				startActivity(intent);
			}
		};
		Buscar.setOnClickListener(listener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buscador, menu);
		return true;
	}

}
