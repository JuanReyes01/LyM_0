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
	//Funciones a revisar
	private String function;
	//no se puede usar define
	private boolean noDefine;

	public Parser() {
		varList = new ArrayList<String>();
		funcList = new ArrayList<String>();
		blocks = "";
		function = "";
		noDefine = false;
	}

	public void leer(String dir) throws Exception {
		try (BufferedReader f = new BufferedReader(new FileReader(dir))) {
			String lectura = f.readLine();
			
			while (lectura != null) {
				lectura = lectura.replaceAll(" +", " ");
				lectura = lectura.replaceAll(" *\\] *", " ] ");
				lectura = lectura.replaceAll(" *\\[ *", " [ ");
				lectura = lectura.replaceAll(" *\\( *", " ( ");
				lectura = lectura.replaceAll(" *\\) *", " ) ");
				lectura = lectura.replaceAll(" \\) \\]", " )]");
				lectura = lectura.replaceAll(" \\] \\)", " ])");
				lectura = lectura.trim();
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
						checkLine(linea);
					}

					else {
						checkLine(linea);
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

	public void checkLine(String[] linea) throws Exception {
		if(linea.length!=0){
			linea = checkEnd(linea);
			if(linea.length!=0) {
				switch (linea[0]) {
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
					parseLook(linea);
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
					parseCheck(linea);
					break;
				case "BLOCKEDP":
					if (linea.length > 2)
						throw new Exception("No deberia haber otro dato despues del parametro");
					break;
				case "NOP":
					break;
				case "BLOCK":
					parseBlock(linea, blocks);
					break;
				case "REPEAT":
					parseRepeat(linea);
					break;
				case "IF":
					parseIf(linea);
					break;
				case "DEFINE":
					if(noDefine) throw new Exception("No se debe utilizar Define");
					parseDefine(linea);
					break;
				case "TO":
					if ( !function.equals("") )
						throw new Exception("Ya hay una función en declaración");
					String fnc = "";
					//El nombre del metodo no existe 
					int j = 0;				
					if(!funcList.contains(linea[1])&&!linea[1].matches("\\d.*")){
						fnc+= linea[1];
						//Si hay parametros
						if(linea.length>2){
							//Revisa los parametros
							boolean hayOutput = false;
							for(int i=2; i<linea.length&&!hayOutput;i++){
								if(linea[i].startsWith(":")){
									if(linea[i].length()<2){
										i++;
										if(varList.contains(linea[i].substring(1))){
											throw new Exception("La variable ya existe");
										}
										else fnc=":"+linea[i];
									}
									else if(varList.contains(linea[i].substring(1))){
										throw new Exception("La variable ya existe");
									}
									else fnc+=linea[i];
									
								}
								if(linea[i].equals("OUTPUT")){
									hayOutput = true;
								}
							j = i;
							}
							if(hayOutput){
							j++;
							String[] linea2 = Arrays.copyOfRange(linea,j,linea.length-1);
							funcList.add(function.substring(0, function.length()-1));
							noDefine = true;
							checkLine(linea2);
							fnc+="1";
							}
							else fnc+="0";
						}
						else fnc+="0";
						function = fnc;
					}
					else throw new Exception("El nombre de la funcion no es valido");
					break;
				case "OUTPUT":
					if(function.endsWith("0")){
						funcList.add(function.substring(0, function.length()-1));
						if(linea.length>1){
						String[] linea2 = Arrays.copyOfRange(linea,1,linea.length);
						noDefine = true;
						checkLine(linea2);
						noDefine = false;
						}
					}
					else throw new Exception("Ya se inicializo el OUTPUT");
					break;
				case "END":
					if(!(function.length()>0)){
						throw new Exception("No hay una funcion que terminar");
					} else {
						function = "";
					}
					if ( linea.length > 1 ) 
						throw new Exception("No puede haber más instrucciones en esta linea luego del END");
					break;
				case "":
					if ( linea.length > 1 ) {
						checkLine(Arrays.copyOfRange(linea,1,linea.length));
					}
					break;
				default:
					boolean esta = false;				
					for(int i=0;i<funcList.size()&&!false;i++){
						String[] var = funcList.get(i).split(":");
						if(var[0].equals(linea[0])){
							esta = true;
							if(!(var.length==linea.length)){
								throw new Exception("No se estan ingresando todos los parametros");
							}
							for ( int k = 1; k < var.length; k++ ) {
								boolean declarado = false;
								if ( !isNumeric(linea[k]) ) {
									for ( int h = 1; h < var.length; h++ ) {
										if ( (":"+var[h]).equals(linea[k]) ) {
											declarado = true;
										}
									}
									if ( varList.contains(linea[k]) ) {
										declarado = true;
									}
									if ( !declarado ) {
										throw new Exception("El parametro no está definido");
									}
								}
							}
						}
						
					}
					if(!esta)throw new Exception("Cadena inesperada:"+linea[0]);
			}
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

	public void parseN(String[] linea) throws Exception {
		// No hay mas datos para analizar
		if (linea.length < 2)
			throw new Exception("No hay mas datos en la linea del codigo");
		boolean esta = false;
		if ( function.length() > 0 ) {
			String[] f = function.substring(0, function.length()-1).split(":");
			for(int i=0;i<f.length&&!esta;i++){
				if(f[i].equals(linea[1].substring(1)))
					esta = true;
			}
		}
		
		if (!function.equals("") && linea[1].startsWith(":")&&!esta)
			throw new Exception("El parámetro no se encuentra definido en la funcion");
		// Si es un numero
		if (!isNumeric(linea[1])) {
			// Si la variable existe
			if (!varList.contains(linea[1])&&!esta)
				throw new Exception("La variable no es valida o no existe");
		}
		// Verificar si hay otro dato
		
		
		else if (linea.length > 2)
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

	public void parseCheck(String[] linea) throws Exception {
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
		if ( !blocks.equals("") )
			throw new Exception("El Define no puede estar dentro de otra instrucción");
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

	public void parseBlock(String[] linea, String blocks) throws Exception{
		String[] linea2 = new String[linea.length-1];
		for(int i=0; i<linea.length; i++){
			if(linea.length!=1&&i+1<=linea.length-1){
				linea2[i] = linea[i+1];
			}					
		}
		noDefine= true;
		checkLine(linea2);
		noDefine= false;
	}

	/**
	 * Determina si se cierran las llaves en orden
	 * @param linea
	 * @throws Exception
	 */

	public String[] checkEnd(String[] linea)throws Exception{
		
		//Si en la ultima palabra la ultima letra es ]
		if(!linea[0].equals("")&&linea[linea.length-1].charAt(0)==']'){
			//check si la lista no esta vacia
			if(!blocks.isEmpty()){
				//check si hay un [ en el ultimo char de blocks 
				if(blocks.charAt(blocks.length()-1)=='['){
					//Si existe se elimina de la lista
					blocks = blocks.substring(0, blocks.length()-1);
					if ( linea[linea.length-1].indexOf("]") != linea[linea.length-1].lastIndexOf("]") || linea[linea.length-1].contains(")") ) {
						linea[linea.length-1] = linea[linea.length-1].substring(linea[linea.length-1].indexOf("]")+1, linea[linea.length-1].length());
						return checkEnd(linea);
					}
					else {
						String[] linea2 = Arrays.copyOfRange(linea,0,linea.length-1);
						return linea2;
					}
				}
				else {
					throw new Exception("Se intento cerrar las llaves en desorden: ]");
				}
			}
			else {
				throw new Exception("Llave que se intenta cerrar no esta inicalizada: ]");
			}
		}
		else if(!linea[0].equals("")&&linea[linea.length-1].charAt(0)==')'){
			//check si la lista no esta vacia
			if(!blocks.isEmpty()){
				//check si hay un [ en el ultimo char de blocks 
				if(blocks.charAt(blocks.length()-1)=='('){
					//Si existe se elimina de la lista
					blocks = blocks.substring(0, blocks.length()-1);
					if ( linea[linea.length-1].indexOf(")") != linea[linea.length-1].lastIndexOf(")") || linea[linea.length-1].contains("]") ) {
						linea[linea.length-1] = linea[linea.length-1].substring(linea[linea.length-1].indexOf(")")+1, linea[linea.length-1].length());
						return checkEnd(linea);
					}
					else {
						String[] linea2 = Arrays.copyOfRange(linea,0,linea.length-1);
						return linea2;
					}
				}
				else {
					throw new Exception("Se intento cerrar las llaves en desorden: )");
				}
			}
			else {
				throw new Exception("Llave que se intenta cerrar no esta inicalizada: )");
			}
		}
		return linea;

	}
	

	public void parseRepeat(String[] linea)throws Exception{
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
				noDefine= true;
				checkLine(linea2);
				noDefine= false;
			}
		}
		else throw new Exception("No se inicializa la repeticion");
	}

	public void parseIf(String[] linea) throws Exception{
		//Si hay un not
		int i = 1;
		if(linea[i].equals("!")){
			i++;
		}
		else if(linea[i].contains("!")){
			linea[i] = linea[i].substring(linea[i].lastIndexOf("!")+1,linea[i].length() ).trim();
		}
		
		if( i < linea.length && linea[i].trim().equals("BLOCKEDP") ){
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
					if(linea3[0]==null) linea3[0]="";
					noDefine = true;
					checkLine(linea3);
					noDefine= false;
				} 
			}
			else throw new Exception("No se inicializa la llave");
		}
	}

	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			parser.leer("./data/data.txt");
			System.out.println("Yes");
		} catch (Exception e) {
			System.out.println("No: " + e.getMessage());
		}
	}
}
