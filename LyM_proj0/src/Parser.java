import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import sun.applet.Main;

public class Parser {
	
	//Array de variables
	private ArrayList<String> varList;
	public Parser() {
		varList = new ArrayList<String>();
	}
	/**
	 * 
	 * @param dir
	 */
	public void leer(String dir) throws Exception{
		try (BufferedReader f = new BufferedReader(new FileReader(dir))){			
		String lectura = f.readLine();
		while(lectura!=null){						
			String[] linea = lectura.split(" ");
			//Recorrido de cada linea de codigo
			for(int i=0; i<linea.length; i++){
				linea[i] = linea[i].trim();
			}
				switch(linea[0]){
				case "MOVE":					
					parseN(linea);						
					break;
					
				case "RIGHT":
					parseN(linea);
					break;
					
				case "LEFT":
					parseN(linea);					
					break;
					
				case "ROTATE":
					parseN(linea);
					break;
				case "LOOK":
					//No hay mas datos
					if(linea.length<=1){
						throw new Exception("No hay parametro LOOK");
					}
					//Si es un numero
					else if(isNumeric(linea[1])){
						throw new Exception("El valor dado no es numerico");
					}
					//No estan los datos pedidos
					else if(!(linea[1].equals('N')||linea[1].equals('S')||linea[1].equals('W')||linea[1].equals('E'))){
						throw new Exception("No contiene el valor esperado");
					}
					//Verificar si hay mas valores
					else if(linea.length>1){
						throw new Exception("No deberia haber mas valores");
					}
					
					break;
				case "DROP":
					parseN(linea);
					break;
					
				case "FREE":
					parseN(linea);
					break;
					
				case "PICK":
					parseN(linea);
					break;
					
				case "POP":
					parseN(linea);
					break;
					
				case "CHECK":
					int i = 0;		
					//No hay mas datos para analizar
					if(3>linea.length) throw new Exception("No hay mas datos en la linea del codigo");
					//PARAMETRO O
					//Si no es numerico
					else if(isNumeric(linea[1])){
						throw new Exception("El valor dado no es numerico");
					}
					//No estan los datos pedidos
					else if(!(linea[1].equals('C')||linea[1].equals('B'))){
						throw new Exception("No contiene el valor esperado");
					}
					//PARAMETRO N
					//Si es un numero
					if(!isNumeric(linea[i++])){
						//Si la variable existe
						if(!varList.contains(linea[i])){
							throw new Exception("La variable no es valida o no existe");
						}
					}					
					//Verificar si hay otro dato
					if(linea.length>3) throw new Exception("No deberia haber otro dato despues del parametro");		
					break;
					
				case "BLOCKEDP":
					if(linea.length>2) throw new Exception("No deberia haber otro dato despues del parametro");		
					break;
				
				case "NOP":
					
					break;
					
				case "BLOCK":
					
					break;
				case "REPEAT":
					
					break;
				case "IF":
					
					break;
				
				case "DEFINE":
					if(3>linea.length) throw new Exception("No hay mas datos en la linea del codigo");
					
					//Verificar var n 
					//No es numerico
					else if(isNumeric(linea[1])){
						throw new Exception("El valor dado no es numerico");
					}
					//Es lowerCase
					else if(!linea[1].equals(linea[1].toLowerCase())){
						throw new Exception("La variable debe estar en minusculas");
					}
					
					//Verificar val 
					
					else if(!isNumeric(linea[2])){
						throw new Exception("El valor deberia ser entero");
					}					
					if(linea.length>3) throw new Exception("No deberia haber otro dato despues del parametro");
					
					//Agregar a la lista de parametros
					varList.add(linea[1]);
					
					break;
				
				case "TO":
					
					break;
					
				}
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * Metodo que retorna si un string es un entero
	 * @param num: string para comprobar
	 * @return bool, true si es entero, false si no lo es
	 */
	public boolean isNumeric(String num){
		try{
			Integer.parseInt(num);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public void parseN(String[] linea) throws Exception{
		int i = 0;		
		//No hay mas datos para analizar
		if(2>linea.length) throw new Exception("No hay mas datos en la linea del codigo");
		//Si es un numero
		if(!isNumeric(linea[i++])){
			//Si la variable existe
			if(!varList.contains(linea[i])){
				throw new Exception("La variable no es valida o no existe");
			}
		}				
		//Verificar si hay otro dato
		if(linea.length>2) throw new Exception("No deberia haber otro dato despues del parametro");		
	}
	
	public static void main(String[] args) {
	
	}
}
