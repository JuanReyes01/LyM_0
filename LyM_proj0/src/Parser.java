import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

	// Array de variables
	private ArrayList<String> varList;
	// Array de funciones
	private ArrayList<String> funcList;
	//Array de llaves
	private String blocks;


	public Parser() {
		varList = new ArrayList<String>();
		funcList = new ArrayList<String>();
		blocks = "";
	}

	/**
	 * 
	 * @param dir
	 */
	public void leer(String dir) throws Exception {
		try (BufferedReader f = new BufferedReader(new FileReader(dir))) {
			String lectura = f.readLine();
			while (lectura != null) {
				lectura = lectura.replaceAll(" +", " ");
				String[] linea = lectura.split(" ");
				// Recorrido de cada linea de codigo
				for (int i = 0; i < linea.length; i++) {
					linea[i] = linea[i].trim();
				}
				if ( linea.length > 0 && !linea[0].equals("")) {
					if ( linea[0].startsWith("(") ) {
						//Se busca el BLOCK al lado del [
						linea[0] = linea[0].substring(1);
						blocks+="(";
						checkLine(linea, "");
					}

					else {
						checkLine(linea, "");
					}
				}
				lectura = f.readLine();
			}
			//Si no se cierran todas las llaves
			if(blocks.length()!=0) throw new Exception("No se cerraron todas las llaves");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void checkLine(String[] linea, String function) throws Exception {
		if(linea.length!=0){
			checkEnd(linea);
			switch (linea[0]) {
			case "MOVE":
				parseN(linea, function);
				break;
			case "RIGHT":
				parseN(linea, function);
				break;
			case "LEFT":
				parseN(linea, function);
				break;
			case "ROTATE":
				parseN(linea, function);
				break;
			case "LOOK":
				parseLook(linea);
				break;
			case "DROP":
				parseN(linea, function);
				break;
			case "FREE":
				parseN(linea, function);
				break;
			case "PICK":
				parseN(linea, function);
				break;
			case "POP":
				parseN(linea, function);
				break;
			case "CHECK":
				parseCheck(linea, function);
				break;
			case "BLOCKEDP":
				if (linea.length > 2)
					throw new Exception("No deberia haber otro dato despues del parametro");
				break;
			case "NOP":

				break;

			case "BLOCK":
				parseBlock(linea, function, blocks);

				break;
			case "REPEAT":
				parseRepeat(linea, function);

				break;
			case "IF":
				parseIf(linea, function);
				break;

			case "DEFINE":
				parseDefine(linea);
				break;
			case "TO":
				
				break;
			case "":

				break;
			default:
				throw new Exception("Cadena inesperada:"+linea[0]);
			}
		}
	}

	/**
	 * Metodo que retorna si un string es un entero
	 * 
	 * @param num: string para comprobar
	 * @return bool, true si es entero, false si no lo es
	 */
	public boolean isNumeric(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void parseN(String[] linea, String function) throws Exception {
		// No hay mas datos para analizar
		if (linea.length < 2)
			throw new Exception("No hay mas datos en la linea del codigo");
		if (!function.equals("") && linea[1].startsWith(":") && !function.contains(linea[1]))
			throw new Exception("El parámetro no se encuentra definido en la funcion");
		// Si es un numero
		if (!isNumeric(linea[1])) {
			// Si la variable existe
			if (!varList.contains(linea[1]))
				throw new Exception("La variable no es valida o no existe");
		}
		// Verificar si hay otro dato
		if (linea.length > 2)
			throw new Exception("No deberia haber otro dato despues del parametro");
	}

	public void parseLook(String[] linea) throws Exception {
		// No hay mas datos
		if (linea.length < 2)
			throw new Exception("No hay parametro LOOK");
		// Si es un numero
		else if (isNumeric(linea[1]))
			throw new Exception("El valor dado no es numerico");
		// No estan los datos pedidos
		else if (!(linea[1].equals("N") || linea[1].equals("S") || linea[1].equals("W") || linea[1].equals("E")))
			throw new Exception("No contiene el valor esperado");
		// Verificar si hay mas valores
		else if (linea.length > 1)
			throw new Exception("No deberia haber mas valores");
	}

	public void parseCheck(String[] linea, String function) throws Exception {
		int i = 0;
		// No hay mas datos para analizar
		if (3 > linea.length)
			throw new Exception("No hay mas datos en la linea del codigo");
		// PARAMETRO O
		// Si no es numerico
		if (isNumeric(linea[1]))
			throw new Exception("El valor dado no es numerico");
		// No estan los datos pedidos
		if (!(linea[1].equals("C") || linea[1].equals("B")))
			throw new Exception("No contiene el valor esperado");
		// PARAMETRO N
		if (!function.equals("") && linea[2].startsWith(":") && !function.contains(linea[2]))
			throw new Exception("El parámetro no se encuentra definido en la función");
		// Si es un numero
		if (!isNumeric(linea[i++])) {
			// Si la variable existe
			if (!varList.contains(linea[i]))
				throw new Exception("La variable no es valida o no existe");
		}
		// Verificar si hay otro dato
		if (linea.length > 3)
			throw new Exception("No deberia haber otro dato despues del parametro");
	}

	public void parseDefine(String[] linea) throws Exception {
		if (linea.length < 3)
			throw new Exception("No hay mas datos en la linea del codigo");
		// Verificar var n
		// No es numerico
		if (isNumeric(linea[1]))
			throw new Exception("El valor dado no es numerico");
		// Es lowerCase
		if (!linea[1].equals(linea[1].toLowerCase()))
			throw new Exception("La variable debe estar en minusculas");
		// Verificar val
		if (!isNumeric(linea[2]))
			throw new Exception("El valor deberia ser entero");
		if (linea.length > 3)
			throw new Exception("No deberia haber otro dato despues del parametro");
		// Agregar a la lista de parametros
		varList.add(linea[1]);
	}

	public void parseBlock(String[] linea, String function, String blocks) throws Exception{
		String[] linea2 = new String[linea.length-1];
		for(int i=0; i<linea.length; i++){
			if(linea.length!=1&&i+1<=linea.length-1){
				linea2[i] = linea[i+1];
			}					
		}
		checkLine(linea2, function);
	}

	/**
	 * Determina si se cierran las llaves en orden
	 * @param linea
	 * @throws Exception
	 */

	public void checkEnd(String[] linea)throws Exception{

		//Si en la ultima palabra la ultima letra es ]
		if(linea[linea.length-1].charAt(linea[linea.length-1].length()-1)==']'){
			//check si la lista no esta vacia
			if(!blocks.isEmpty()){
				//check si hay un [ en el ultimo char de blocks 
				if(blocks.charAt(blocks.length()-1)=='['){
					//Si existe se elimina de la lista
					blocks = blocks.substring(0, blocks.length()-1);
					linea[linea.length-1] = linea[linea.length-1].replace("]", ""); 
				}
				else {
					throw new Exception("Se intento cerrar las llaves en desorden: ]");
				}
			}
			else {
				throw new Exception("Llave que se intenta cerrar no esta inicalizada: ]");
			}
		}
		else if(linea[linea.length-1].charAt(linea[linea.length-1].length()-1)==')'){
			//check si la lista no esta vacia
			if(!blocks.isEmpty()){
				//check si hay un [ en el ultimo char de blocks 
				if(blocks.charAt(blocks.length()-1)=='('){
					//Si existe se elimina de la lista
					blocks = blocks.substring(0, blocks.length()-1);
					linea[linea.length-1] = linea[linea.length-1].replace(")", ""); 
				}
				else {
					throw new Exception("Se intento cerrar las llaves en desorden: )");
				}
			}
			else {
				throw new Exception("Llave que se intenta cerrar no esta inicalizada: )");
			}
		}

	}

	public void parseRepeat(String[] linea, String function)throws Exception{
		// No hay mas datos para analizar
		if (!function.equals("") && linea[1].startsWith(":") && !function.contains(linea[1]))
			throw new Exception("El parametro no se encuentra definido en la funcion");
		// Si es un numero
		if (!isNumeric(linea[1])) {
			// Si la variable existe
			if (!varList.contains(linea[1]))
				throw new Exception("La variable no es valida o no existe");
		}

		//Se abre la llave
		if(linea[2].equals("[")){
			blocks+="[";
			String[] linea2 = new String[linea.length-1];
			//Si la linea tiene mas instrucciones crea una sublista
			if(3<linea.length){
				for(int i=2; i<linea.length; i++){
					if(linea.length!=1&&i+1<=linea.length-1){
						linea2[i] = linea[i+1];
					}					
				}
				checkLine(linea2, function);
			}
		}
		else throw new Exception("No se inicializa la repeticion");
	}
	
	public void parseIf(String[] linea, String function) throws Exception{
		//Si hay un not
		int i = 1;
		if(linea[i].equals("!")){
			i++;
		}
		else if(linea[i].contains("!")){
			linea[i] = linea[i].substring(1,linea.length);
		}

		//TODO Este condicional tiene que confirmar si el elemento es booleano
		if(true){
			i++;
			//Se abre la llave
			if(linea[i].equals("[")){
				blocks+="[";
				String[] linea3 = new String[linea.length-3];
				//Si la linea tiene mas instrucciones crea una sublista
				if(3<linea.length){
					for(int j=0; j<linea.length-3; j++){
						if(linea.length!=1&&i+1<=linea.length-1){
							linea3[j] = linea[j+3];
						}					
					}
					checkLine(linea3, function);
				} 
			}
		}
	}

	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			parser.leer("./data/data.txt");
			System.out.println("Yes");
		} catch (Exception e) {
			System.out.println("No");
			System.out.println(e.getMessage());
		}
	}
}
