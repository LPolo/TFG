package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//Clase relacionada con el activity que se habre nada mas arrancar la plicacion
public class MainActivity extends Activity {

	private Button Catalogo;
	private Button Categorias;
	private Button Buscar;
	private Button Configuracion;
	private Button Actualizacion;
	private GestorBD gestor;
	SharedPreferences config;
	private int prueba = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int localidad;
		gestor = new GestorBD(this);
		config = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
		AsignarCatalogo();
		AsignarCategoria();
		AsignarBuscar();
		AsignarConfiguracion();
		localidad = config.getInt("Localidad", 0);
		AsignarActualizacion(localidad);
		if (localidad == 0) {
			gestor.descargarLocalidades(this);
			MensajeConfiguracion();
			Catalogo.setEnabled(false);
			Categorias.setEnabled(false);
			Buscar.setEnabled(false);
			Actualizacion.setEnabled(false);
		} else {
			gestor.ComprobarActualizacion(localidad, this);
		}

	}

	// Metodo por el cual se le asigna la función al boton Catalogo
	private void AsignarCatalogo() {
		Catalogo = (Button) findViewById(R.id.catalogo);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, VerCatalogo.class);
				intent.putExtra("Localidad", config.getInt("Localidad", 0));
				intent.putExtra("EsCategoria", false);
				intent.putExtra("EsBusqueda", false);
				startActivity(intent);
			}
		};
		Catalogo.setOnClickListener(listener);
	}

	// Metodo por el cual se le asigna la función al boton Categoria
	private void AsignarCategoria() {
		Categorias = (Button) findViewById(R.id.Categorias);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						ListaCategoria.class);
				intent.putExtra("Localidad", config.getInt("Localidad", 0));
				startActivity(intent);
			}
		};
		Categorias.setOnClickListener(listener);

	}

	// Metodo por el cual se le asigna la función al boton Buscar
	private void AsignarBuscar() {
		Buscar = (Button) findViewById(R.id.Buscar);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, Buscador.class);
				intent.putExtra("Localidad", config.getInt("Localidad", 0));
				startActivity(intent);
			}
		};
		Buscar.setOnClickListener(listener);

	}

	// Metodo por el cual se le asigna la función al boton Configuracion
	private void AsignarConfiguracion() {
		Configuracion = (Button) findViewById(R.id.Configuracion);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						Configuracion.class);
				startActivity(intent);
			}
		};
		Configuracion.setOnClickListener(listener);

	}

	// Metodo por el cual se le asigna la función al boton Actualizacion
	private void AsignarActualizacion(final int localizacion) {

		Actualizacion = (Button) findViewById(R.id.Actualizar);
		OnClickListener listener = new OnClickListener() {
			public void onClick(View arg0) {
				gestor.Actualizar(localizacion, MainActivity.this);
			}
		};
		Actualizacion.setOnClickListener(listener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Metodo por el cual se muestra un mensaje de que necesita configuracion la
	// primera vez que abrimos la aplicación
	private void MensajeConfiguracion() {
		AlertDialog mensaje = new AlertDialog.Builder(this).create();
		mensaje.setCancelable(false);
		mensaje.setTitle("Configuracion");
		mensaje.setMessage("Es la primera vez que se abre la aplicacion y necesita configurarla");
		mensaje.setButton(mensaje.BUTTON_POSITIVE, "Ir a configuración",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(MainActivity.this,
								Configuracion.class);
						finish();
						startActivity(intent);
					}
				});
		mensaje.show();

	}

}
