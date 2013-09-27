package com.tfc.aplicacion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Clase que describe el formato que tendra cada celda dentro de la lista

public class AdaptadorLista extends ArrayAdapter {
	private Activity context;
	private celda[] celdas;

	AdaptadorLista(Activity context, celda[] celdas) {
		super(context, R.layout.itemlist, celdas);
		this.celdas=celdas;
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.itemlist, null);
		TextView Titulo = (TextView) item.findViewById(R.id.Titulo);
		Titulo.setText(celdas[position].getTitulo());
		TextView Subtitulo = (TextView) item.findViewById(R.id.SubTitulo);
		Subtitulo.setText(celdas[position].getSubtitulo());
		ImageView image = (ImageView) item.findViewById(R.id.imagen);
		image.setImageResource(celdas[position].getImagen());
		return (item);
	}
}