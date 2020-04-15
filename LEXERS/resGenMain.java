
import java.util.HashMap;
import javax.swing.JFileChooser;
import java.io.File;
/**
*
*/
public class resultadoGeneradorMain {
public static String EPSILON = "ε";
public static char EPSILON_CHAR = EPSILON.charAt(0);
/**
 */
public static void main(String[] args) {
	LeerArchivo leer = new LeerArchivo();
	File file = new File("input"+".txt");
	HashMap input = leer.leerArchivo(file);
	Ejemplo resGenerator = new Ejemplo(input);
	resGenerator.automatas();
	resGenerator.revisar();
	}
}

