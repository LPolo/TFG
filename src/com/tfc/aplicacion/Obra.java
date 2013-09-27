package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

//Clase en la que se muestra toda la informacion de la obra
public class Obra extends Activity {
	private GestorBD gestor = new GestorBD(this);
	
	private TextView Nombre ;
	private TextView Tipo ;
	private TextView Teatro;
	private TextView Precio ;
	private TextView FechaInicio;
	private TextView FechaFin;
	private TextView Descripcion;
	private TextView Puntuacion;
	private TextView Votos;
	private Button Atras;
	private Button Votar;
	private int ID;
	private String Localidad;
	private int voto;
	private float punt;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_obra);
		Nombre = (TextView) findViewById(R.id.Nombre);
		Tipo = (TextView) findViewById(R.id.Tipo);
		Teatro = (TextView) findViewById(R.id.Teatro);
		Precio = (TextView) findViewById(R.id.Precio);
		FechaInicio = (TextView) findViewById(R.id.FechaInicio);
		FechaFin = (TextView) findViewById(R.id.FechaFin);
		Descripcion = (TextView) findViewById(R.id.Descripcion);
		Puntuacion = (TextView) findViewById(R.id.Puntuacion);
		Votos = (TextView) findViewById(R.id.Votos);
		EscribirDatos();
		AsignarAtras();
		AsignarVotar();	
	}
	// Metodo por el cual se le asigna la función al boton Atras
	private void AsignarAtras() {
		Atras = (Button) findViewById(R.id.AtrasObra);
		OnClickListener listener = new OnClickListener() {
			public void onClick(View arg0) {
				finish();			
			}
		};
		Atras.setOnClickListener(listener);		
	}
	// Metodo por el cual se le asigna la función al boton Votar
	private void AsignarVotar() {
		Votar = (Button) findViewById(R.id.BotonVotar);
		OnClickListener listener;
		if(gestor.PuedoVotar(Localidad,ID)){
			Votar.setText("Votar");
		listener = new OnClickListener() {
			public void onClick(View arg0) {
				MensajeVotar();		
			}
		};
		Votar.setOnClickListener(listener);	
		}else{
			Votar.setText("Quitar Voto");
			listener = new OnClickListener() {
				public void onClick(View arg0) {
					MensajeQuitarVoto();		
				}
			};
			Votar.setOnClickListener(listener);
		}
			
	}
	// Metodo por el cual se muestra un mensaje para poder votar la obra
	private void MensajeVotar(){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Seleccione la puntuación");
			final String[] valores ={"1","2","3","4","5","6","7","8","9","10"}; 
			builder.setItems(valores, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	gestor.Votar(Localidad,ID,valores[item]);
			    	gestor.Actualizar(gestor.getCodigo(Localidad), Obra.this);
			    	AsignarVotar();
			    }
			});
			final AlertDialog mensaje = builder.create();
			mensaje.setButton(mensaje.BUTTON_POSITIVE, "Cancelar",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mensaje.cancel();
						}
					});
			mensaje.show();
	}
	
	// Metodo por el cual se muestra un mensaje para poder quitar la puntuacion a la obra
	private void MensajeQuitarVoto(){
		
		final AlertDialog mensaje = new AlertDialog.Builder(this).create();
		mensaje.setTitle("¿Quitar Voto?");
		mensaje.setButton(mensaje.BUTTON_POSITIVE, "Si",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						gestor.QuitarVoto(Localidad, ID);
				    	gestor.Actualizar(gestor.getCodigo(Localidad), Obra.this);
				    	AsignarVotar();
					}
				});
		mensaje.setButton(mensaje.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mensaje.cancel();
					}
				});
		mensaje.show();
}
	// Metodo por el cual se muestran por pantalla los datos de la obra
	private void EscribirDatos() {
		Bundle informacion = getIntent().getExtras();
		Localidad = informacion.getString("Localidad");
		String[] Datos =gestor.getDatos(Localidad, informacion.getString("ID"));
		Nombre.setText("Titulo: "+Datos[0]);
		Tipo.setText("Tipo: "+Datos[1]);
		Teatro.setText("Teatro: "+Datos[2]);
		Precio.setText("Precio: "+Datos[3]+"€");
		FechaInicio.setText("Fecha de Inicio: "+Datos[4]);
		FechaFin.setText("Fecha Fin: "+Datos[5]);
		Descripcion.setText("Descripción: "+Datos[6]);
		Puntuacion.setText("Puntuacion: "+Datos[7]);
		punt=Float.parseFloat(Datos[7]);
		Votos.setText("Votos: "+Datos[8]);
		voto=Integer.parseInt(Datos[8]);
		ID=Integer.parseInt(Datos[9]);		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.obra, menu);
		return true;
	}

}
