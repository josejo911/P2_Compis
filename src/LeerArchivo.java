/**
 * Nueva
 */
import java.io.*;
import java.util.HashMap;

public class LeerArchivo {

    public HashMap leerArchivo(File archivo){
        int contador=0;
        int tamaño=0;
        String input="";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(archivo.getAbsoluteFile()));
            String linea;

            int cantidadLineas=1;

            HashMap<Integer,String> detailString = new HashMap();
            while ((linea = br.readLine()) != null) {

                linea = linea.replaceAll("\\.(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", "π");


                if (!linea.equals("")){
                    if ((!linea.contains("π"))&&!(linea.contains("COMPILER")
                            ||linea.contains("CHARACTERS")
                            ||linea.contains("KEYWORDS")
                            ||linea.contains("TOKENS")
                            ||linea.contains("IGNORE"))
                            ){
                        input +=linea;

                    }
                    else{
                        linea = linea.replaceAll("π", ".");
                        input+=linea;
                        while (input.startsWith(" "))
                            input = input.substring(1);
                        detailString.put(cantidadLineas, input);
                        input = "";
                    }
                }
                cantidadLineas++;

            }
            return detailString;
        } catch (IOException e) {

        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    public void crearArchivo(String output,String nombreArchivo){
        try {
            File archivo;
            File nuevoArchivo = new File("");
            String path = nuevoArchivo.getAbsolutePath();
            archivo = new File(path+"/LEXERS/"+nombreArchivo+".java");
            FileWriter fw = new FileWriter(archivo);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(output+"\r\n");
            bw.close();
            System.out.println("Se creo un nuevo archivo con nombre " + nombreArchivo +" exitosamente");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
