public class ErrorSintactico {
    private int count = 0;

    public ErrorSintactico(){

    }
    /**
     * Ocurre cuando la expresión está mal ingresada, parentésis, etc.
     * @param line
     * @param msg
     */
    public void errorSintactico(int line,  String msg){
        System.out.println("");
        System.out.println("Error Sintáctico " + "en la línea " +line);
        System.out.println(">>>   " + msg);
        System.out.println("");
        count++;

    }
    /**
     * Ocurre cuando se no reconoce un token
     * @param line
     * @param msg
     */
    public void errorLexico(int line,  String msg){
        System.out.println("");
        System.out.println("Error Léxico " + "en la línea " +line);
        System.out.println(">>>   " + msg);
        System.out.println("");
        count++;

    }
    /**
     * Ocurre cuando se realiza un operación
     * @param line linea del archivo
     * @param msg mensaje indicativo
     */
    public void errorSemantico(int line, String msg){
        System.out.println("");
        System.out.println("Error semántico " + "en la línea " +line);
        System.out.println(">>>   " + msg);
        System.out.println("");
        count++;


    }
    /**
     * Muestra advertencia de sintaxis que no conlleva a error semántico.
     * @param line linea del archivo
     * @param msg mensaje indicativo
     */
    public void Warning(int line, String msg){
        System.out.println("");
        System.out.println("Advertencia " + "en la línea " +line);
        System.out.println(">>>   " + msg );
        System.out.println("");
        count++;

    }
    public void WarningIdententificador(String msg){
        System.out.println("");
        System.out.println("Advertencia ");
        System.out.println(">>>   " + msg );
        System.out.println("");
        count++;

    }
    /**
     * Obtener contador de errores
     * @return entero
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
