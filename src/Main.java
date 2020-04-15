/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * Principal
 */

import java.util.HashMap;
import java.io.File;


public class Main {
    public static String EPSILON = "ε";
    public static char EPSILON_CHAR = EPSILON.charAt(0);
    public static ErrorSintactico errores = new ErrorSintactico();


    public static void main(String[] args) {
                    LeerArchivo leer = new LeerArchivo();
                    File file = new File("cocol"+".txt");
                    HashMap cocol = leer.leerArchivo(file);
                    System.out.println("Procesando...");
                    System.out.println("esto tardara unos segundos...");
                    Lexer lexer = new Lexer(cocol);
                    lexer.vocabulario();
                    lexer.construct(cocol);
                    System.out.println("Archivo de Cocol Aceptado.");

                    if(lexer.getOutput()){
                        GeneLXR generador = new GeneLXR(cocol);
                        generador.AnalizadorLexico();
                    }
                    System.out.println("El Numero de errores encontrados es: "+errores.getCount());

        }
}






