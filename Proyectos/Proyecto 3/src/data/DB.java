package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
/**
Esta clase implementa una forma facil de crear una conexion a una base de datos sqlite.

Los metodos incluidos sirven para crear la conexion, cerrarla y ejecutar consultas a la base de datos.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
public class DB{
	private String dbName;
	private Connection con;
	private String error_msg;
	private Hashtable<String,ResultSet> RSHash;
	public DB(String dbName){
		this.dbName=dbName;
		con=null;
		RSHash = new Hashtable<String,ResultSet>();
		error_msg="";
	}
/**
	Inicializa la conexion a la base de datos,
	@exception UGDBConnectException if unable to open connection to database
	@return	un valor boolean que indica si fue posible realizar la conexion a la base de datos, en caso la conexion fue exitosa el valor retornado es true, en caso contrario regresa false
**/
	public boolean connect() throws UGDBConnectException{
		try{
			Class.forName("org.sqlite.JDBC");
		}catch(Exception e){
			try{
				ClassPathModificator.appendPath("sqlitejdbc.jar");
				Class.forName("org.sqlite.JDBC");
			}catch(Exception ex){
				error_msg="Unable to open connection to the database, aditional information below:\n"+ex.getMessage();
				//return false;
				throw new UGDBConnectException(error_msg);
			}
		}
		try{
			con=DriverManager.getConnection("jdbc:sqlite:"+dbName);
		}catch(Exception e){
			error_msg="Unable to connect to the database,make sure database name is well written,additional information below:\n"+e.getMessage();
			//return false;
			throw new UGDBConnectException(error_msg);
		}
		return true;
	}
/**
	Cierra la conexion a la base de datos, es importante utilizar este metodo al terminar de hacer alguna accion con la base de datos, ya que sqlite bloquea la base de datos hasta que la conexion actual se cierre.
	Esto significa que no se podran crear conexiones posteriores hasta que la actual se cierre.
	
	@return	boolean	true si fue posible cerrar la conexion, false en caso contrario
**/
	public boolean close() throws UGDBDisconnectException{
		try{
		con.close();
		}catch(Exception e){error_msg="Unable to close db connection";throw new UGDBDisconnectException(error_msg);}
		return true;
	}
/**
	Es utilizado para realizar operaciones sql que regresan un set de resultados asiciados a una consulta, el set de resultados se guarda en la estructura default de sets de resultados. Por ejemplo un select a la base de datos
	@param	q	representa la consulta sql que se desea realizar.
	@return boolean	si el query se ejecuto con exito el valor de retorno es true, en caso contrario es false
**/
	public boolean executeQuery(String q) throws UGDBCreateStatementException,UGDBQueryExecutionException{
		Statement stm=null;
		try{
			stm=con.createStatement();
		}catch(Exception e){
			error_msg="There was an error when creating the statement, make sure the connection to the database has been established";
			throw new UGDBCreateStatementException(error_msg);
		}
		try{
			ResultSet rs=stm.executeQuery(q);
			RSHash.put("default",rs);
		}catch(Exception e){
			error_msg="There was an error when processing the query, aditional information below:\n"+e.getMessage();
			throw new UGDBQueryExecutionException(error_msg);
		}
		return true;
	}
/**
	Es utilizado para realizar operaciones sql que regresan un set de resultados asociados a una consulta. Por ejemplo un select a la base de datos
	@param	q	representa la consulta sql que se desea realizar.
    @param  resultSetName indica el nombre del set de resultados con el cual se desea trabajar.
	@return	boolean	si el query fue ejecutado exitosamente se regresa true, en caso contario false sera el valor de retorno.
**/
	public boolean executeQuery(String q,String resultSetName) throws UGDBCreateStatementException,UGDBQueryExecutionException{
		Statement stm=null;
		try{
			stm=con.createStatement();
		}catch(Exception e){
			error_msg="There was an error when creating the statement, make sure the connection to the database has been established";
			throw new UGDBCreateStatementException(error_msg);
		}
		try{
			//rs=stm.executeQuery(q);
			RSHash.put(resultSetName,stm.executeQuery(q));
		}catch(Exception e){
			error_msg="There was an error when processing the query, aditional information below:\n"+e.getMessage();
			throw new UGDBQueryExecutionException(error_msg);
		}
		return true;
	}
/**
	Ejecuta una sentencia sql sobre la base de datos, pero esta no regresa algun set de resultados.
	@param	q	sentencia sql a realizar.
	@return	boolean	el valor de retorno sera false si la base de datos no a sido inicializada o la sentencia sql tenga algun error sintactico
**/	
	public boolean executeNonQuery(String q) throws UGDBCreateStatementException,UGDBNonQueryExecutionException{
		Statement stm=null;
		try{
			stm=con.createStatement();
		}catch(Exception e){
			error_msg="There was an error when creating the statement, make sure the connection to the database has been established";
			throw new UGDBCreateStatementException(error_msg);
		}
		try{
			stm.executeUpdate(q);
		}catch(Exception e){
			error_msg="There was an error when processing the statement, aditional information below:\n"+e.getMessage();
			throw new UGDBNonQueryExecutionException(error_msg);
		}
		return true;
	}
/**
	Mueve el puntero del set de resultados a la siguiente fila del set de resultados default.
	@return	boolean	se regresa false si se produjo un error o si el set de resultados a llegado a su fin
	
**/
	public boolean next() throws UGDBGetNextTupleException{
		boolean f=false;
		try{
			f=RSHash.get("default").next();
		}catch(Exception e){
			error_msg="There was an error trying to move to next Tuple,make sure the connection to the database has been established, aditional information below:\n"+e.getMessage();
			//f = false;
			throw new UGDBGetNextTupleException(error_msg);
		}
		return f;
	}
/**
	Mueve el puntero del set de resultados a la siguiente fila del set de resultados.
    @param  resultSetName indica el nombre del set de resultados con el cual se desea trabajar
	@return	boolean	se regresa false si se produjo un error o si el set de resultados a llegado a su fin
**/
	public boolean next(String resultSetName) throws UGDBGetNextTupleException{
		boolean f=false;
		try{
			f=RSHash.get(resultSetName).next();
		}catch(Exception e){
			error_msg="There was an error trying to move to next Tuple,make sure the connection to the database has been established, aditional information below:\n"+e.getMessage();
			//f = false;
			throw new UGDBGetNextTupleException(error_msg);
		}
		return f;
	}
/**
	Regresa el mensaje del ultimo error.
	@return	String	String que contiene el ultimo error capturado por la libreria.
**/
	public String getError(){
		return error_msg;
	}
/**
	Obtiene el valor contenido en la columna indicada de la tupla actual.
	@param	column	debe de ser un numero entero mayor a 0, que representa una columna en la tupla.
	@return	String	contiene el valor de la columna seleccionada
**/
	public String getString(int column) throws UGDBInvalidFieldException{
		String data="";
		try{
			data= RSHash.get("default").getString(column);
		}catch(Exception e){
			error_msg="There was an error trying to retrive column value, aditional information below:\n"+e.getMessage();
			throw new UGDBInvalidFieldException(error_msg);
		}
		return data;
	}
/**
*	Obtiene el valor contenido en la columna indicada de la tupla actual.
*	@param	column	debe de ser un numero entero mayor a 0, que representa una columna en la tupla.
*   @param  resultSetName indica el nombre del set de resultados con el cual se desea trabajar
*	@return	String	contiene el valor de la columna seleccionada
**/
	public String getString(int column,String resultSetName) throws UGDBInvalidFieldException{
		String data="";
		try{
			data= RSHash.get(resultSetName).getString(column);
		}catch(Exception e){
			error_msg="There was an error trying to retrive column value, aditional information below:\n"+e.getMessage();
			throw new UGDBInvalidFieldException(error_msg);
		}
		return data;
	}
/**
*	Obtiene el valor contenido en la columna indicada de la tupla actual.
*	@param	column	indica el nombre de la columna de la cual se quiere obtener el valor
*	@return	String	contiene el valor de la columna seleccionada
**/
	public Object getString(String column) throws UGDBInvalidFieldException{
		String data="";
		try{
			data= RSHash.get("default").getString(column);
		}catch(Exception e){
			error_msg="There was an error trying to retrive column value, aditional information below:\n"+e.getMessage();
			throw new UGDBInvalidFieldException(error_msg);
		}
		return data;
	}
/**
*	Obtiene el valor contenido en la columna indicada de la tupla actual.
*	@param	column	indica el nombre de la columna de la cual se quiere obtener el valor
*   @param  resultSetName indica el nombre del set de resultados con el cual se desea trabajar
*	@return	String	contiene el valor de la columna seleccionada
**/
	public Object getString(String column,String resultSetName) throws UGDBInvalidFieldException{
		String data="";
		try{
			data= RSHash.get(resultSetName).getString(column);
		}catch(Exception e){
			error_msg="There was an error trying to retrive column value, aditional information below:\n"+e.getMessage();
			throw new UGDBInvalidFieldException(error_msg);
		}
		return data;
	}
}

/**Exceptions Definition**/

/**
Esta clase implementa Excepcion de error al conectarse a la base de datos. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBConnectException extends Exception {
	UGDBConnectException() { super(); }
	UGDBConnectException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al desconectarse a la base de datos. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBDisconnectException extends Exception {
	UGDBDisconnectException() { super(); }
	UGDBDisconnectException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al crear un SQL Statement. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBCreateStatementException extends Exception {
	UGDBCreateStatementException() { super(); }
	UGDBCreateStatementException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al ejecutar query. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBQueryExecutionException extends Exception {
	UGDBQueryExecutionException() { super(); }
	UGDBQueryExecutionException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al ejecutar un NON Query. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBNonQueryExecutionException extends Exception {
	UGDBNonQueryExecutionException() { super(); }
	UGDBNonQueryExecutionException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al intentar obtener una tupla de la consulta sin exito a causa de un error. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBGetNextTupleException extends Exception {
	UGDBGetNextTupleException() { super(); }
	UGDBGetNextTupleException(String s) { super(s); }
}
/**
Esta clase implementa Excepcion de error al querer obtener el valor de un campo invalido. Invocar metodo getMessage() para mas informacion de la excepcion al ser lanzada.

@author	Wifi Cordon	<wifi@galileo.edu>

*/
class UGDBInvalidFieldException extends Exception {
	UGDBInvalidFieldException() { super(); }
	UGDBInvalidFieldException(String s) { super(s); }
}
