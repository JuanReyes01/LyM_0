import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser {

	// Array de variables
	private ArrayList<String> varList;
	// Array de funciones
	private ArrayList<String> funcList;

	public Parser() {
		varList = new ArrayList<String>();
		funcList = new ArrayList<String>();
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
						linea[0] = linea[0].substring(1);
						checkLine(linea, "", "(");
					}
					else {
						checkLine(linea, "", "");
					}
				}
				lectura = f.readLine();
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void checkLine(String[] linea, String function, String blocks) throws Exception {
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

				break;
			case "REPEAT":

				break;
			case "IF":

				break;

			case "DEFINE":
				parseDefine(linea);
				break;
			case "TO":
				break;
			default:
				throw new Exception("Cadena inesperada");
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
			throw new Exception("El parámetro no se encuentra definido en la función");
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

	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			parser.leer("./LyM_proj0/data/data.txt");
			System.out.println("Yes");
		} catch (Exception e) {
			System.out.println("No");
			System.out.println(e.getMessage());
		}
	}
}
