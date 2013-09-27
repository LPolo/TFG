package com.tfc.aplicacion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

public class ListaCategoria extends Activity {

	// Lista donde se veran las categorias que existen
	private ListView Categoria;
	// Lista de tipos de obras teatrales
	private String[] tipos = { "Teatro", "Danza", "Infantil", "Comedia",
			"Drama", "Opera", "Musical", "Otros" };
	// Codigo de la localidad en la que se buscara las obras
	private int localidad;
	private Button Atras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_categorias);
		Categoria = (ListView) findViewById(R.id.ListaCategorias);
		EstablecerCategorias();
		AsignarAtras();
	}

	// Metodo por el cual se escriben las categorias en la lista
	private void EstablecerCategorias() {
		// Se obteniene la informacion que el anterior activity nos ha pasado
		Bundle informacion = getIntent().getExtras();
		localidad = informacion.getInt("Localidad");
		// Nos creamos un Adaptador de listas con una celda que esta
		// integrada en android
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, tipos);
		Categoria.setAdapter(adaptador);
		// Añadimos un listener para cuando se pulse la categoria te muestre las
		// obras que hay dentro de esa categoria
		Categoria.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adap, View view, int pos,
					long arg3) {
				Intent intento = new Intent(ListaCategoria.this,
						VerCatalogo.class);
				intento.putExtra("Localidad", localidad);
				intento.putExtra("EsCategoria", true);
				intento.putExtra("Categoria", tipos[pos]);
				startActivity(intento);
			}
		});
	}

	// Metodo por el cual se le asigna la función de ir atras al boton Atras
	private void AsignarAtras() {
		Atras = (Button) findViewById(R.id.BotonAtrasCategorias);
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
