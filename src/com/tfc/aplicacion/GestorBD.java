package com.tfc.aplicacion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class GestorBD extends SQLiteOpenHelper {

	// Clase que se encargara de gestionar la base de datos y de recibir la
	// nueva informacion

	private int prueba = 0;
	private Calendar calendario = new GregorianCalendar();

	public GestorBD(Context context) {
		super(context, "Cartelera", null, 5);
	}

	// Comprueba si los datos existen y si estan actualizados
	public void ComprobarActualizacion(int localidad, Activity lanzador) {
		SQLiteDatabase db = this.getReadableDatabase();
		String fecha = Integer.toString(calendario.get(Calendar.DAY_OF_YEAR))
				+ "/" + Integer.toString(calendario.get(Calendar.YEAR));
		Cursor C = db.rawQuery("SELECT Fecha FROM Localidades WHERE Codigo="
				+ localidad, null);
		boolean existe = C.moveToFirst();
		if (existe) {
			String fechatabla = C.getString(0);
			if (fecha.compareTo(fechatabla) != 0) {
				Actualizar(localidad, lanzador);
			}
		} else {
			Actualizar(localidad, lanzador);
		}

	}

	// Comprueba su hay conexion a internet
	public boolean ConexionInternet(Activity actividad) {
		Context context = actividad;
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectMgr != null) {
			NetworkInfo[] netInfo = connectMgr.getAllNetworkInfo();
			if (netInfo != null) {
				for (NetworkInfo net : netInfo) {
					if (net.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Conecta con el servidor para actualizar la tabla de una ciudad
	public void Actualizar(int Codigo, Activity lanzador) {
		if (ConexionInternet(lanzador)) {
			String Localidad = getNombre(Codigo);
			limpiarTabla(Localidad);
			crearTabla(Localidad);
			ProgressDialog Dialog = establecerMensajeDescarga(lanzador);
			ConexionServidor Conexion = new ConexionServidor(0, Dialog);
			Conexion.execute(Codigo, this.getWritableDatabase(), Localidad);
		}

	}

	// Conecta con el servidor para descargarse la lista de posibles localidades
	public void descargarLocalidades(Activity lanzador) {
		if (ConexionInternet(lanzador)) {
			ProgressDialog Dialog = establecerMensajeDescarga(lanzador);
			ConexionServidor Conexion = new ConexionServidor(0, Dialog);
			Conexion.execute(0, this.getWritableDatabase());
		}

	}

	// Hace que aparezca el mensaje de descarga de datos
	private ProgressDialog establecerMensajeDescarga(Activity activity) {
		ProgressDialog Dialog = ProgressDialog.show(activity, "Descargando",
				"Descargando la base...");
		return Dialog;
	}

	// Limpia de datos la tabla para poder insertar datos
	private void limpiarTabla(String localidad) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + localidad + "");
		// db.close();
	}

	// Crea una nueva tabla en la base de datos
	private void crearTabla(String localidad) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE "
				+ localidad
				+ " (ID INTEGER, Nombre TEXT, Tipo TEXT, Teatro TEXT, Precio TEXT, FechaInicio TEXT, FechaFin TEXT, Descripcion TEXT, Puntuacion FLOAT, Votos INTEGER ) ");
		// db.close();
	}

	// Devuelve una lista de las posibles localidades
	public String[] getLocalidades() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor C = db.rawQuery("SELECT Nombre FROM Localidades", null);
		String[] Localidades = new String[C.getCount()];
		C.moveToFirst();
		int i = 0;
		do {
			Localidades[i] = C.getString(0);
			i++;
		} while (C.moveToNext());
		// db.close();
		return Localidades;
	}

	// Devuelve los datos necesarios para crear una lista
	public String[][] getLista(int localidad) {
		SQLiteDatabase db = this.getReadableDatabase();
		String Nombre = getNombre(localidad);
		Cursor C = db.rawQuery("SELECT * FROM " + Nombre, null);
		String[][] lista = null;
		if (C.moveToFirst()) {
			lista = new String[C.getCount()][4];
			int i = 0;
			do {
				lista[i][0] = C.getString(1);
				lista[i][1] = C.getString(2);
				lista[i][2] = C.getString(0);
				lista[i][3] = C.getString(8);
				i++;
			} while (C.moveToNext());
		}
		// db.close();
		return lista;
	}

	// Devuelve toda la informacion en relacion a una obra que es buscada por su
	// nombre
	public String[] getDatos(String Localidad, String ID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor C = db.rawQuery("SELECT * FROM " + Localidad + " WHERE ID='"
				+ ID + "'", null);
		C.moveToFirst();

		String[] Datos = { C.getString(1), C.getString(2), C.getString(3),
				C.getString(4), FormateoFecha(C.getString(5)),
				FormateoFecha(C.getString(6)), C.getString(7), C.getString(8),
				C.getString(9), C.getString(0) };
		// db.close();
		return Datos;
	}

	// Da Formato a la fecha
	private String FormateoFecha(String Datos) {
		String res = Datos.substring(6, 8) + "/" + Datos.substring(4, 6) + "/"
				+ Datos.substring(0, 4);
		return res;
	}

	// Devuelve una lista con los resultados de la busqueda
	public String[][] getBusqueda(int Localidad, String Cadena) {
		SQLiteDatabase db = this.getReadableDatabase();
		String Nombre = getNombre(Localidad);
		Cursor C = db.rawQuery("SELECT * FROM " + Nombre
				+ " WHERE (Nombre LIKE '%" + Cadena + "%' ) OR (TEATRO LIKE '%"
				+ Cadena + "%')", null);
		String[][] lista = null;
		if (C.moveToFirst()) {
			lista = new String[C.getCount()][4];
			int i = 0;
			do {
				lista[i][0] = C.getString(1);
				lista[i][1] = C.getString(2);
				lista[i][2] = C.getString(0);
				lista[i][3] = C.getString(8);
				i++;
			} while (C.moveToNext());
		}
		return lista;
	}

	// Devuelve todos los resultados de una categoria
	public String[][] getCategoria(int localidad, String Categoria) {
		Cursor C;
		String[][] resultado = null;
		String Nombre = getNombre(localidad);
		SQLiteDatabase db = this.getReadableDatabase();
		int i = 0;
		if (Categoria.compareTo("Otros") != 0) {
			C = db.rawQuery("SELECT * FROM " + Nombre + " WHERE Tipo='"
					+ Categoria + "'", null);
			if (C.moveToFirst()) {
				resultado = new String[C.getCount()][4];
				do {
					resultado[i][0] = C.getString(1);
					resultado[i][1] = C.getString(2);
					resultado[i][2] = C.getString(0);
					resultado[i][3] = C.getString(8);
					i++;
				} while (C.moveToNext());
			}
		} else {
			C = db.rawQuery(
					"SELECT * FROM "
							+ Nombre
							+ " WHERE Tipo NOT IN ('Teatro','Danza','Infantil','Comedia','Drama','Opera','Musical')",
					null);
			if (C.moveToFirst()) {
				resultado = new String[C.getCount()][4];
				do {
					resultado[i][0] = C.getString(1);
					resultado[i][1] = C.getString(2);
					resultado[i][2] = C.getString(0);
					resultado[i][3] = C.getString(8);
					i++;
				} while (C.moveToNext());
			}
		}
		return resultado;
	}

	// Devuleve el nombre de la ciudad
	public String getNombre(int codigo) {
		String res;
		if (codigo == 0) {
			res = "0";
		} else {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor C = db.rawQuery(
					"SELECT Nombre FROM Localidades WHERE Codigo='" + codigo
							+ "'", null);
			C.moveToFirst();
			res = C.getString(0).replace(" ", "");
		}
		return res;
	}

	// Devuelve el código de la localidad asociado a su nombre
	public int getCodigo(String nombre) {
		SQLiteDatabase db = this.getReadableDatabase();
		int res;
		Cursor C = db.rawQuery("SELECT Codigo FROM Localidades WHERE Nombre='"
				+ nombre + "'", null);
		C.moveToFirst();
		res = C.getInt(0);
		return res;
	}

	// Devuelve si se puede votar la obra
	public boolean PuedoVotar(String localidad, int ID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS Votos (ID INTEGER, Localidad TEXT, Valor FLOAT)");
		Cursor C = db.rawQuery("SELECT * FROM Votos WHERE ID='" + ID
				+ "' AND Localidad='" + localidad + "'", null);
		return !C.moveToFirst();
	}

	// Metodo por el cual se guarda en la base de datos el voto y envia la
	// informacion al servidor
	public void Votar(String localidad, int ID, String valor) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("INSERT INTO Votos (ID, Localidad, Valor) VALUES ('" + ID
				+ "','" + localidad + "','" + valor + "')");
		ConexionServidor conexion = new ConexionServidor(1, null);
		conexion.execute(localidad, ID, valor);

	}

	// Metodo por el cual se elimina el voto
	public void QuitarVoto(String localidad, int ID) {
		int valor;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor C = db.rawQuery("SELECT Valor FROM Votos WHERE ID='" + ID
				+ "' AND Localidad='" + localidad + "'", null);
		C.moveToFirst();
		valor = C.getInt(0) * -1;
		ConexionServidor conexion = new ConexionServidor(1, null);
		conexion.execute(localidad, ID, valor);
		db.execSQL("DELETE FROM Votos WHERE ID='" + ID + "' AND Localidad='"
				+ localidad + "'");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
