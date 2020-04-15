/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 */
public class Abreviaciones extends Operadores {
    private final String escapeChars = "\"" + "\'" +"\\";


    public String abreviacionOr(String regex){
        String resultado = new String();
        String pos ="";
        try{
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)=='-'){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +=CInicioParentesis;
                        for (int j = 0;j<=fin-inicio;j++)
                        {
                            if (this.escapeChars.contains(Character.toString((char)(inicio+j))))
                                pos="\\";
                            if (j==(fin-inicio))
                                resultado+= pos+Character.toString((char)(inicio+j));
                            else
                                resultado+= pos+ Character.toString((char)(inicio+j))+COr;

                            pos="";
                        }
                        resultado +=CFinalParentesis;
                        i=i+4;
                    }
                    else{
                        resultado +=ch;
                    }
                }
                else{
                    resultado+=ch;
                }
            }
        } catch (Exception e){
            System.out.println("Error en la conversión " + regex);
            resultado = " ";
        }return resultado;
    }

    public String abreviacionAnd(String regex){
        String resultado = new String();
        try{
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)==this.CConcatenacion){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +=CInicioParentesis;
                        for (int j = 0;j<=fin-inicio;j++)
                        {

                            resultado+= Character.toString((char)(inicio+j));
                        }
                        resultado +=CFinalParentesis;
                        i=i+4;
                    }
                }
                else{
                    resultado+=ch;
                }
            }
        }catch (Exception e){
            System.out.println("Error en la conversion "+regex);
            resultado = "(a|b)*abb";
        }return resultado;
    }
}
