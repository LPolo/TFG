package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//Clase ralacionada con el activity que muestra un listado de obras
public class VerCatalogo extends Activity {
	
	private GestorBD gestor = new GestorBD(this);
	private int TamañoLista;
	private String[][] lista;
	private boolean EsCategoria=false, EsBusqueda=false;
	private int localidad;
	private Button Atras;
	private celda[] celdas;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_catalogo);
		ListView Catalogo = (ListView) findViewById(R.id.ListaCatalogo);
		//Obtenemos la informacion que el anterior activity nos ha pasado
		Bundle informacion = getIntent().getExtras();
		localidad= informacion.getInt("Localidad");
		try{
		if(informacion.getBoolean("EsCategoria")){
			//Si es categoria solo necesitara los datos de dicha categoria
			EsCategoria=true;
			lista= gestor.getCategoria(localidad, informacion.getString("Categoria"));
		}
		else if(informacion.getBoolean("EsBusqueda")){
			//Si es busqueda solo nesita los datos que guarden relacion con el termino a buscar
			EsBusqueda=true;
			lista = gestor.getBusqueda(localidad, informacion.getString("Busqueda"));
		}else{
			lista = gestor.getLista(localidad);		
		}
	
		if(lista!=null){
			//Si la lista no esta vacia se crea la lista con un adaptador especial
		TamañoLista= lista.length;
		celdas= new celda[TamañoLista];
		for(int i=0;i<TamañoLista;i++){
			celdas[i]=new celda(lista[i][0],lista[i][1],lista[i][3]);			
		}
		AdaptadorLista adaptador = new AdaptadorLista(this, celdas);
		Catalogo.setAdapter(adaptador);
		Catalogo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adap, View view, int pos,
					long arg3) {
				Intent intent = new Intent(VerCatalogo.this, Obra.class);
				intent.putExtra("Localidad", gestor.getNombre(localidad));
				intent.putExtra("localidadint",localidad);
				intent.putExtra("ID", lista[pos][2]);	
				startActivity(intent);
			}
			
		});
		}else{
			//Si la lista es vacia se muestra el mensaje no hay resultados
			String[] alistar = new String[]{"No hay resultados"};
			Catalogo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alistar));
		}
		}catch(RuntimeException exc){
			//Si hay algun problema con la bse de datos se muestra un error de base de datos
			String[] listaerror = new String[]{"Error, no se tiene la base de datos"};
			Catalogo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaerror));
	}
		AsignarAtras();
	}
	

	// Metodo por el cual se le asigna la función al boton Atras
	private void AsignarAtras() {
		Atras = (Button) findViewById(R.id.BotonAtrasCatalogo);
		OnClickListener listener = new OnClickListener() {

			public void onClick(View arg0) {
		
				finish();
				
			}
		};
		Atras.setOnClickListener(listener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ver_catalogo, menu);
		return true;
	}

}
