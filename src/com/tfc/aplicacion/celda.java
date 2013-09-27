package com.tfc.aplicacion;

//Clase que sirve para guardar la informacion de la celda.

public class celda{
	private String Titulo;
	private String Subtitulo;
	private int puntuacion;
	
	public celda(String titulo, String subtitulo, String puntuacion){
		Titulo = titulo;
		Subtitulo=subtitulo;
		float aux=Float.parseFloat(puntuacion);
		this.puntuacion=(int)Math.round(aux);
		if(this.puntuacion>aux){
			this.puntuacion--;
		}
		
	}
	

	
	public int getImagen(){
		int resource;
		switch(puntuacion){
		case 1:
			resource = R.drawable.numero1;
			break;
		case 2:
			resource = R.drawable.numero2;
			break;
		case 3:
			resource = R.drawable.numero3;
			break;
		case 4:
			resource = R.drawable.numero4;
			break;
		case 5:
			resource = R.drawable.numero5;
			break;
		case 6:
			resource = R.drawable.numero6;
			break;
		case 7:
			resource = R.drawable.numero7;
			break;
		case 8:
			resource = R.drawable.numero8;
			break;
		case 9:
			resource = R.drawable.numero9;
			break;
		case 10:
			resource = R.drawable.numero10;
			break;
		default:
			resource = R.drawable.numero0;
			break;
			
		}
		return resource;
	}

	public String getTitulo(){
		return Titulo;
	}
	
	public String getSubtitulo(){
		return Subtitulo;
	}
}
