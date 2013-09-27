package com.tfc.aplicacion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

// Clase que servira de puente entre la aplicación y el servidor
public class ConexionServidor extends AsyncTask<Object, Object, Object> {

	private Socket cliente;
	private Object Datos;
	//IP del servidor
	private String IP = "192.168.1.47";
	//Puerto en el que comienza a emitir
	private int PuertoComienzo = 5000;
	//Puerto por el que actualmente emite
	private int Puerto = 5000;
	//Numero de servidores activos
	private int Servidores = 1;
	//Objeto que sirve para obtener la fecha actual
	private Calendar calendario = new GregorianCalendar();
	//Codigo de la accion que se va a realizar
	private int Codigo;
	//Dialogo que sirve para bloquear la aplicacion mientras se descargan los datos
	private ProgressDialog Dialog;

	//Contructor de la clase 
	public ConexionServidor(int Codigo, ProgressDialog Dialog) {
		this.Codigo = Codigo;
		this.Dialog=Dialog;		
	}
	
	

	@Override
	protected Object doInBackground(Object... params) {
		try {
			//Establecemos la conexion con el servidor
			cliente = new Socket(IP, Puerto);
			//Establecemos los canales de comunicacion 
			ObjectOutputStream TunelSalida = new ObjectOutputStream(
					cliente.getOutputStream());
			ObjectInputStream TunelEntrada = new ObjectInputStream(
					cliente.getInputStream());
			//Depende del valor del codigo haremos diferentes acciones
			switch (Codigo) {
			// Si el codigo es 0 solicitamos informacion al servidor
			case 0:
				int localidad = Integer.parseInt(params[0].toString());				
				TunelSalida.writeObject(localidad);
				TunelSalida.flush();
				Datos = TunelEntrada.readObject();
				TunelEntrada.close();
				TunelSalida.close();
				//Si el valor de localidad es 0 quiere decir que es una actualizacion de datos
				if (localidad != 0) {
					Actualizar((String) params[2], (SQLiteDatabase) params[1]);
				} //Si el valor es 0 quiere decir que es una descarga de lista de localidades
				else {
					descargarLocalidades((SQLiteDatabase) params[1]);
				}
				break;
			// Si el codigo es 1 enviamos la informacion de un nuevo voto
			case 1:
				TunelSalida.writeObject(-1);
				TunelSalida.flush();
				TunelSalida.writeObject(params[0]);
				TunelSalida.flush();
				TunelSalida.writeObject(params[1]);
				TunelSalida.flush();
				TunelSalida.writeObject(params[2]);
				TunelSalida.flush();
				break;
			}
		} catch (IOException e) {
			//Si el puerto esta ocupado aumentamos el puerto de comunicacion
			if (Puerto < PuertoComienzo + Servidores) {
				Puerto++;
			} else {
				Puerto = PuertoComienzo;
			}
			//Esperamos 1 segundo 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//Vilvemos a intentarlo
			doInBackground(params);
		} catch (ClassNotFoundException e) {

		} catch (RuntimeException ex) {

		}
		return null;
	}

	//Metodo por el cual actualizamos los datos de una tabla
	private void Actualizar(String localidad, SQLiteDatabase db) {
		//Construimos la cadena de la fecha
		String fecha = Integer.toString(calendario.get(Calendar.DAY_OF_YEAR))
				+ "/" + Integer.toString(calendario.get(Calendar.YEAR));
		String informacion;
		//Los datos los parseamos a un JSONArray para que lo manejemos más fácil
		informacion = (String) Datos;
		Object obj = JSONValue.parse(informacion);
		JSONArray array = (JSONArray) obj;
		JSONObject jaux;
		if (array != null) {
			ContentValues nuevoRegistro = new ContentValues();
			// Por cada elemento de la lista introducmos un nuevo registro en la base de datos
			for (int i = 0; i < array.size(); i++) {
				jaux = (JSONObject) array.get(i);
				nuevoRegistro.put("ID", (String) jaux.get("ID"));
				nuevoRegistro.put("Nombre", (String) jaux.get("Nombre"));
				nuevoRegistro.put("Tipo", (String) jaux.get("Tipo"));
				nuevoRegistro.put("Teatro", (String) jaux.get("Teatro"));
				nuevoRegistro.put("Precio", (String) jaux.get("Precio"));
				nuevoRegistro.put("FechaInicio",
						(String) jaux.get("FechaInicio"));
				nuevoRegistro.put("FechaFin", (String) jaux.get("FechaFin"));
				nuevoRegistro.put("Descripcion", ((String) jaux
						.get("Descripcion")).toLowerCase(new Locale("es")));
				nuevoRegistro
						.put("Puntuacion", (String) jaux.get("Puntuacion"));
				nuevoRegistro.put("Votos", (String) jaux.get("Votos"));
				db.insert(localidad, null, nuevoRegistro);
				
			}
			// Actualizamos la fecha en la que se descargo la base de datos
			db.execSQL("UPDATE Localidades SET Fecha='" + fecha
					+ "' WHERE Nombre='" + localidad + "'");

		}
		//Cerramos la base y terminamos el dialogo de descarga
		db.close();
		Dialog.cancel();

	}

	//Metodo por el cual se introduce la lista de localidades en la base de datos del móvil
	private void descargarLocalidades(SQLiteDatabase db) {
		//Construimos la cadena de la fecha
		String fecha = Integer
				.toString(calendario.get(Calendar.DAY_OF_YEAR) - 1)
				+ "/"
				+ Integer.toString(calendario.get(Calendar.YEAR));
		//Parseamos la informacion obtenida a un JSONArray
		String informacion = Datos.toString();
		Object obj = JSONValue.parse(informacion);
		JSONArray array = (JSONArray) obj;
		JSONObject aux;
		ContentValues nuevoRegistro = new ContentValues();
		//Creamos la tabla
		db.execSQL("DROP TABLE IF EXISTS Localidades");
		db.execSQL("CREATE TABLE Localidades (Nombre TEXT, Codigo INTEGER, Fecha TEXT) ");
		//Por cada elemento de la lista agregamos un nuevo registro a la base de datos
		for (int i = 0; i < array.size(); i++) {
			aux = (JSONObject) array.get(i);
			nuevoRegistro.put("Nombre", (String) aux.get("Ciudad"));
			nuevoRegistro.put("Codigo", (String) aux.get("Codigo"));
			nuevoRegistro.put("Fecha", fecha);
			db.insert("Localidades", null, nuevoRegistro);

		}
		//Cerramos el dialogo de descarga
		Dialog.cancel();
	}
}
