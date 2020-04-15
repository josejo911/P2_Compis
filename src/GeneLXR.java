import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;


public class GeneLXR extends Operadores{

    private String nombreArchivo;
    private final HashMap<Integer,String> cadena;
    private final String ANY = "[!-#]"+COr+"[%-.]"+COr+"[@-Z]"+COr+"[^-z]";
    private Abreviaciones ab = new Abreviaciones();
    private String ignoreSets = " ";
    private final HashMap<String, String> cadenaCompleta;
    private final HashMap<String, String> tokensExpr;
    private final TreeMap<String,String> keyMap;
    private final Stack pilaConcatenacion;
    private final Stack pilaAvanzada;
    private final HashMap<String, Boolean> verKeywords;




    public GeneLXR(HashMap cadena){
        this.verKeywords = new HashMap();
        this.pilaConcatenacion = new Stack();
        this.keyMap = new TreeMap();
        this.tokensExpr = new HashMap();
        this.cadenaCompleta = new HashMap();
        this.pilaAvanzada = new Stack();
        this.cadena=cadena;

    }



    public void encontrarNombre(){

        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("COMPILER")){
                value = value.trim();
                int index = value.indexOf("R");
                this.nombreArchivo = value.substring(++index,value.length());
                this.nombreArchivo = this.nombreArchivo.trim();

            }

        }

    }

    public void generarCharactersYKeywords(){
        ignoreSets();
        Regex convert = new Regex();
        cadenaCompleta.put("ANY", convert.ab.abreviacionOr(ANY));

        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("CHARACTERS")){
                int linea = entry.getKey();
                while(true){
                    linea = proximaLinea(linea);
                    if (this.cadena.get(linea).contains("KEYWORDS"))
                        break;
                    String valor = this.cadena.get(linea);

                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    revisar = revisar.trim();
                    revisar = crearCadenasOr(revisar);
                    cadenaCompleta.put(ident.trim(), revisar);
                }

            }
            if (value.contains("KEYWORDS")&&!value.contains("EXCEPT")){
                int linea = entry.getKey();
                linea = proximaLinea(linea);
                while(true){

                    if (this.cadena.get(linea).contains("END")||this.cadena.get(linea).contains("TOKENS"))
                        break;
                    String valor = this.cadena.get(linea);
                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    revisar = revisar.trim();
                    //revisar = crearCadenasOr(revisar);
                    revisar = revisar.replaceAll("\"", "");
                    //tokensExpr.put(ident.trim(), revisar);


                    keyMap.put(ident.trim(),revisar);
                    tokensExpr.put(ident.trim(),revisar);


                    linea = proximaLinea(linea);

                }
            }

        }

    }
    public void generarTokens(){
        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("TOKENS")){
                int linea = entry.getKey();
                while(true){
                    linea = proximaLinea(linea);
                    if (this.cadena.get(linea).contains("END")||this.cadena.get(linea).contains("IGNORE"))
                        break;
                    String valor = this.cadena.get(linea);



                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    if (revisar.contains("EXCEPT")){
                        revisar = revisar.substring(0,revisar.indexOf("EXCEPT")).trim();
                        verKeywords.put(ident, Boolean.TRUE);
                    }
                    revisar = revisar.trim();

                    System.out.println("");
                    System.out.println(ident);
                    System.out.println(revisar);
                    revisar = revisar.replaceAll("\\{", CInicioParentesis+"");
                    revisar = revisar.replaceAll("\\}", CFinalParentesis+""+CKleene);
                    revisar = revisar.replaceAll("(\\[)(?=(?:[^\"']|[\"|'][^\"']*\")*)", CInicioParentesis+"");
                    revisar = revisar.replaceAll("(\\])(?=(?:[^\"']|[\"|'][^\"']*\")*)",CFinalParentesis+"" +CInt);
                    revisar = revisar.replaceAll("\\|",COr+"");
                    revisar = formatodeRegex(revisar);
                    System.out.println(revisar);
                    for (Map.Entry<String, String> entryRegex : cadenaCompleta.entrySet()) {

                        if (revisar.contains(entryRegex.getKey())){
                            revisar = revisar.replaceAll(entryRegex.getKey().trim(), entryRegex.getValue());
                        }

                    }
                    System.out.println("enmedio");
                    System.out.println(revisar);
                    revisar = fixString(revisar);

                    revisar = revisar.replaceAll("\\s","");
                    tokensExpr.put(ident.trim(), revisar);

                    System.out.println(revisar);


                }

            }
        }
    }
    public void generarClaseAnalizadora() {

        String scanner_total = (
                "/**"+"\n"+
                        " * Nombre del archivo: "+this.nombreArchivo+".java"+"\n"+
                        "* Universidad Del Valle de Guatemala"+"\n"+
                        "* 09-09-2017"+"\n"+
                        "* Jose Jo 14343"+"\n"+
                        " * Descripción: Segundo proyecto. Generador de analizador léxico"+"\n"+
                        "**/"+"\n"+
                        ""+"\n"+
                        "import java.util.Map;"+"\n"+
                        "import java.util.HashSet;"+"\n"+
                        "import java.util.Comparator;"+"\n"+
                        "import java.util.ArrayList;"+"\n"+
                        "import java.util.TreeMap;"+"\n"+
                        "import java.util.HashMap;"+"\n"+



                        ""+"\n"+
                        "public class "+this.nombreArchivo+" {"+"\n"+
                        ""+"\n"+
                        "\t"+"private Simulacion sim = new Simulacion();"+"\n"+
                        "\t"+"private ArrayList<Automata> automatas = new ArrayList();"+"\n"+
                        "\t"+"private HashMap<Integer,String> input;"+"\n"+
                        "\t"+"private ArrayList keywords = new ArrayList();"+"\n");

        scanner_total +=
                "\t"+"private String ignoreSets = \""+ignoreSets.substring(0, ignoreSets.length()-1)+"\";"+"\n"+
                        "\t"+"private ArrayList<Token> tokensAcumulados = new ArrayList();"+"\n"+
                        "\t"+"private ArrayList<Token> tokens = new ArrayList();"+"\n"+
                        "\t"+"private String tk  = \"\";"+"\n"+
                        "\t"+"private Errors errores = new Errors();"+"\n"+"\n"+

                        "\t"+"public " + this.nombreArchivo+"(HashMap input){"+"\n"+
                        "\t"+"\t"+"this.input=input;"+"\n"+
                        "\t"+"\n"+

                        "\t" + "}"

        ;

        scanner_total+="\n"+"}";

        LeerArchivo fileCreator = new LeerArchivo();
        fileCreator.crearArchivo(scanner_total, nombreArchivo);

    }
    public void generarMain(){
        String res = "\n"+

                "import java.util.HashMap;"+"\n"+
                "import javax.swing.JFileChooser;"+"\n"+
                "import java.io.File;"+"\n"+

                "/**"+"\n"+
                "*"+"\n"+
                "*/"+"\n"+
                "public class "+ "resultadoGenerador" +"Main"+" {"+"\n"+

                "public static String EPSILON = \"ε\";"+"\n"+
                "public static char EPSILON_CHAR = EPSILON.charAt(0);"+"\n"+
                "/**"+"\n"+
                " */"+"\n"+
                "public static void main(String[] args) {"+"\n"+
                "\t"+"LeerArchivo leer = new LeerArchivo();"+"\n"+
                "\t"+"File file = new File(\"input\"+\".txt\");"+"\n"+
                "\t"+"HashMap input = leer.leerArchivo(file);"+"\n"+
                "\t"+this.nombreArchivo+" resGenerator = new "+this.nombreArchivo+"(input);"+"\n"+
                "\t"+"resGenerator.automatas();"+"\n"+
                "\t"+"resGenerator.revisar();"+"\n"+
                "\t"+"}"+"\n"+
                "}"+"\n";

        LeerArchivo fileCreator = new LeerArchivo();
        fileCreator.crearArchivo(res, "resGenMain");

    }
    public void generarClaseToken(){
        String token =""+

                "/**"+"\n"+
                "* Universidad Del Valle de Guatemala"+"\n"+
                "* 09-09-2017"+"\n"+
                "* Jose Jo 14343"+"\n"+
                "*/"+"\n"+
                "import java.util.ArrayList;"+"\n"+
                "import java.util.HashSet;"+"\n"+
                "import java.util.Objects;"+"\n"+
                "import java.util.TreeMap;"+"\n"+

                "/**"+"\n"+
                " *"+"\n"+
                " */"+"\n"+
                "public class Token<T> {"+"\n"+

                "\t"+"private T id;"+"\n"+
                "\t"+"private T lexema;"+"\n"+
                "\t"+"private ArrayList keywords = new ArrayList();"+"\n"+
                "\t"+"private HashSet<Token> tokens = new HashSet();"+"\n"+
                "\t"+"private TreeMap<String,String> keyMap = new TreeMap();"+"\n"+

                "\t"+"public Token(T id, T lexema,boolean revisarKey) {"+"\n"+
                "\t"+"\t"+"if (revisarKey)"+"\n"+
                "\t"+"\t"+"\t"+"keyWords();"+"\n"+
                "\t"+"\t"+"ArrayList var = revisarKeywords(id,lexema);"+"\n"+
                "\t"+"\t"+"this.id = (T) var.get(0);"+"\n"+
                "\t"+"\t"+"this.lexema = (T) var.get(1);"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"public T getId() {"+"\n"+
                "\t"+"\t"+"return id;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"public void setId(T id) {"+"\n"+
                "\t"+"\t"+"this.id = id;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"public T getLexema() {"+"\n"+
                "\t"+"\t"+"return lexema;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"public void setLexema(T lexema) {"+"\n"+
                "\t"+"\t"+"this.lexema = lexema;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"public ArrayList revisarKeywords(T id, T lexema){"+"\n"+
                "\t"+"\t"+"ArrayList returnArray = new ArrayList();"+"\n"+

                "\t"+"\t"+"if (keyMap.containsKey((String)lexema)){"+"\n"+
                "\t"+"\t"+"\t"+"returnArray.add(keyMap.get((String)lexema));"+"\n"+
                "\t"+"\t"+"\t"+"returnArray.add(lexema);"+"\n"+
                "\t"+"\t"+"\t"+"return returnArray;"+"\n"+
                "\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"returnArray.add(id);"+"\n"+
                "\t"+"\t"+"returnArray.add(lexema);"+"\n"+
                "\t"+"\t"+"return returnArray;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+ "public void keyWords(){"+"\n";
        for (Map.Entry<String, String> entry : keyMap.entrySet()) {
            token+= "\t"+"\t"+"keyMap.put(\""+entry.getValue()+"\",\""+entry.getKey()+"\");"+"\n";

        }
        token+="\t"+"}"+"\n";
        token+="\t"+"@Override"+"\n"+
                "\t"+"public String toString() {"+"\n"+
                //  "\t"+"\t"+"return \"<\" +id +\">\";"+"\n"+
                "\t"+"\t"+"return \"<\" + id + \", \\\"\" + lexema + \"\\\">\";"+"\n"+
                "\t"+ "}"+"\n"+

                "\t"+"@Override"+"\n"+
                "\t"+"public int hashCode() {"+"\n"+
                "\t"+"\t"+"int hash = 3;"+"\n"+
                "return hash;"+"\n"+
                "\t"+"}"+"\n"+

                "\t"+"@Override"+"\n"+
                "\t"+"public boolean equals(Object obj) {"+"\n"+
                "\t"+"\t"+"if (obj == null) {"+"\n"+
                "\t"+"\t"+"\t"+ "return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"if (getClass() != obj.getClass()) {"+"\n"+
                "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"final Token<?> other = (Token<?>) obj;"+"\n"+
                "\t"+"\t"+"if (!Objects.equals(this.id, other.id)) {"+"\n"+
                "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"if (!Objects.equals(this.lexema, other.lexema)) {"+"\n"+
                "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"}"+"\n"+



                "}"+"\n";
        LeerArchivo CreadorArchivo = new LeerArchivo();
        CreadorArchivo.crearArchivo(token, "Token");


    }
    public Integer proximaLinea(int lineaActual){
        while (true){
            if (this.cadena.containsKey(++lineaActual))
                return lineaActual;
        }
    }
    public String formatodeRegex(String eval){
        String returnString="";

        for (int i =0;i<eval.length();i++){
            Character ch = eval.charAt(i);

            if (i>0){
                if (ch=='('&&eval.charAt(i-1)!='\"'){
                    ch = CInicioParentesis;
                }
                if (i>1){
                    if (ch=='\"'&&(eval.charAt(i-1)!='\\')){
                        continue;
                    }
                    if (ch=='\"'&&eval.charAt(i-2)=='\\'){
                        continue;
                    }
                }
                if (ch=='\''&&(eval.charAt(i-1)!='\\')&&eval.charAt(i-1)!='\"')
                    continue;

            }
            if (i==0){
                if (ch=='(')
                    ch = CInicioParentesis;
                if (ch=='\"')
                    continue;
            }
            if (i+1<eval.length()){
                if (ch==')'&&eval.charAt(i+1)!='\"'){
                    ch = CFinalParentesis;
                }
            }
            else{
                if (ch==')')
                    ch = CFinalParentesis;
            }
            returnString += ch;
        }


        return returnString;
    }
    public String fixString(String eval){
        String returnString = "";

        for (int i=0;i<eval.length();i++){
            String pos = "";
            Character ch = eval.charAt(i);
            if (ch == '\"'){
                if (eval.charAt(i-1)!='\\'){
                    pos="\\";
                }
            }
            if (i+1<eval.length()){
                if (ch==COr&&eval.charAt(i+1)==COr)
                    continue;
            }

            returnString += pos +ch;
        }

        return returnString;
    }
    public void ignoreSets(){
        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            int lineaActual = entry.getKey();
            if (this.cadena.get(lineaActual).contains("IGNORE")){
                if (value.contains("\'")){
                    value = value.replaceAll("\'", "");
                }else if (value.contains("\"")){
                    value = value.replaceAll("\"","");
                }
                tokensExpr.put("WHITESPACE", value.substring(6,value.indexOf(".")));

                ignoreSets = (value.substring(6,value.indexOf(".")));

            }

        }
        System.out.println(ignoreSets);
    }










    public String crearCadenasOr(String cadena){

        String or = "";

        or = cadenasOrLista(cadena);
        if (!or.isEmpty()&&!cadena.contains("+")){
            return or;
        }
        if (cadena.equals("'+'")){
            return "+";
        }
        if (cadena.equals("'-'")){
            return "-";
        }
        if ((cadena.startsWith("\"")||cadena.startsWith("\'"))&&(!cadena.contains("+"))){

            try{

                cadena=  cadena.substring(cadena.indexOf("\"")+1,cadena.lastIndexOf("\""));

            }catch(Exception e){}

            try{

                cadena=  cadena.substring(cadena.indexOf("\'")+1,cadena.lastIndexOf("\'"));

            }catch(Exception e){}

            for (int i = 0;i<cadena.length();i++){
                Character c = cadena.charAt(i);


                if (c=='\\'){

                    or += c;
                    or += cadena.charAt(i+1);
                    i++;

                }
                if (c == '$'){
                    or += "\\" + c;
                }
                else if (i<=cadena.length()-2){
                    or += c;
                    or += COr;
                }
                else if (i>cadena.length()-2)
                    or +=c;



            }



        }
        else {
            or = cadena;
            if(cadena.contains("+")&&!cadena.contains("-")){
                if ((cadena.contains("\"")||cadena.contains("\'"))&&!cadena.contains("..")){
                    int cantidadConcatenaciones = count(cadena,'+');
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                    }
                    int preIndex=0;
                    if (cadena.contains("\""))
                        preIndex = cadena.indexOf(("\""));
                    if (cadena.contains("\'"))
                        preIndex = cadena.indexOf(("\'"));
                    String w = cadena.substring(preIndex+1);
                    int postIndex = w.length()-1;
                    if (w.contains("\""))
                        postIndex = w.indexOf(("\""));
                    if (w.contains("\'"))
                        postIndex = w.indexOf(("\'"));
                    String wFinal = cadena.substring(preIndex,preIndex+postIndex+2);

                    String cadenaOr = crearCadenasOr(wFinal);
                    int lado = calcularConcatenacion(or);
                    if (lado == -1){

                        or = CInicioParentesis+buscarExpr(or) +CFinalParentesis+COr+CInicioParentesis+ cadenaOr+CFinalParentesis;
                    }
                    else if (lado == 1){

                        or = CInicioParentesis+cadenaOr +CFinalParentesis+COr+CInicioParentesis+ buscarExpr(or)+CFinalParentesis;
                    }

                    while (!pilaConcatenacion.isEmpty()){
                        String faltante = (String)pilaConcatenacion.pop();
                        cantidadConcatenaciones = count(faltante,'+');
                        if (cantidadConcatenaciones>1){
                            pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                            faltante = (faltante.substring(0, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                        }
                        or = concatenacion(or,faltante);
                    }
                }
                else if (!cadena.contains("..")){
                    int cantidadConcatenaciones = count(cadena,'+');
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                    }
                    String subcadena = cadena.substring(0,cadena.indexOf("+"));
                    String ident1 = buscarExpr(subcadena);
                    String subcadena2 = cadena.substring(cadena.indexOf("+")+1);
                    String ident2 = buscarExpr(subcadena2);
                    int lado = calcularConcatenacion(or);
                    if (lado == -1){

                        or = CInicioParentesis+ident1 +CFinalParentesis+COr+CInicioParentesis+ ident2+CFinalParentesis;
                    }
                    else if (lado == 1){

                        or = CInicioParentesis+ident2 +CFinalParentesis+COr+CInicioParentesis+ ident1+CFinalParentesis;
                    }
                    while (!pilaConcatenacion.isEmpty()){
                        String faltante = (String)pilaConcatenacion.pop();
                        cantidadConcatenaciones = count(faltante,'+');
                        if (cantidadConcatenaciones>1){
                            pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                            faltante = (faltante.substring(0, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                        }
                        else
                            faltante = (faltante.substring(faltante.indexOf("+")+1));
                        or = concatenacionIdent(or,faltante);
                    }

                }
                else {
                    int cantidadConcatenaciones = count(cadena,'+');
                    String subcadena2="";
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                        subcadena2 = cadena.substring(cadena.indexOf("+")+1,cadena.indexOf("+", cadena.indexOf("+") + 1));
                    }
                    else
                        subcadena2 = cadena.substring(cadena.indexOf("+")+1);
                    String subcadena = cadena.substring(0,cadena.indexOf("+"));

                    String list1 = cadenasOrLista(subcadena);
                    String list2 = cadenasOrLista(subcadena2);
                    if (list1.isEmpty())
                        list1 = crearCadenasOr(subcadena);
                    if (list2.isEmpty())
                        list2 = crearCadenasOr(subcadena2.trim());
                    or = list1+COr+list2;
                    while (!pilaConcatenacion.isEmpty()){
                        String faltante = (String)pilaConcatenacion.pop();
                        cantidadConcatenaciones = count(faltante,'+');
                        if (cantidadConcatenaciones>1){
                            pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                            faltante = (faltante.substring(faltante.indexOf("+")+1, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                        }
                        else
                            faltante = (faltante.substring(faltante.indexOf("+")+1));
                        or =  or +COr+crearCadenasOr(faltante.trim());

                    }

                    return CInicioParentesis+or+CFinalParentesis;
                }
            }else if (cadena.contains("-")&&!cadena.contains("+")){
                if ((cadena.contains("\"")||cadena.contains("\'"))&&!cadena.contains("..")){

                    int cantidadConcatenaciones = count(cadena,'-');
                    String subcadena2="";
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(cadena.substring(cadena.indexOf("-", cadena.indexOf("-") + 1)));
                        subcadena2 = cadena.substring(cadena.indexOf("-")+1,cadena.indexOf("-", cadena.indexOf("-") + 1));
                    }
                    else
                        subcadena2 = cadena.substring(cadena.indexOf("-")+1);
                    String subcadena = cadena.substring(0,cadena.indexOf("-"));
                    int preIndex=0;
                    String expr  =  buscarExpr(subcadena2);
                    subcadena2 = subcadena2.trim();

                    if (!expr.isEmpty()){
                        expr = expr.replaceAll(CInicioParentesis+"", "");
                        expr = expr.replaceAll(CFinalParentesis+"", "");
                        subcadena2 = expr;
                    }
                    if (subcadena2.startsWith("\""))
                        subcadena2 = subcadena2.substring(1);
                    if (subcadena2.endsWith("\""))
                        subcadena2 =subcadena2.substring(0,subcadena2.length()-1);
                    else if (subcadena2.startsWith("\'")){
                        subcadena2 = subcadena2.substring(1);
                        if (subcadena2.endsWith("\'"))
                            subcadena2 =subcadena2.substring(0,subcadena2.length()-1);
                    }
                    or = buscarExpr(subcadena);
                    int indexQuitar=0;
                    if (or.contains(subcadena2)){
                        or = or.replaceAll(subcadena2, "");
                    }
                    or = balancear(or);
                    while (!pilaConcatenacion.isEmpty()){
                        String faltante = (String)pilaConcatenacion.pop();
                        cantidadConcatenaciones = count(faltante,'-');
                        if (cantidadConcatenaciones>1){
                            pilaConcatenacion.push(faltante.substring(faltante.indexOf("-", faltante.indexOf("-") + 1)));
                            faltante = (faltante.substring(faltante.indexOf("-")+1, faltante.indexOf("-", faltante.indexOf("-") + 1)));

                        }  else
                            faltante = (faltante.substring(faltante.indexOf("-")+1));
                        expr  =  buscarExpr(faltante);
                        faltante = faltante.trim();

                        if (!expr.isEmpty()){
                            expr = expr.replaceAll(CInicioParentesis+"", "");
                            expr = expr.replaceAll(CFinalParentesis+"", "");
                            faltante = expr;
                        }

                        if (faltante.startsWith("\'"))
                            faltante = faltante.substring(1);

                        if (faltante.endsWith("\'"))
                            faltante =faltante.substring(0,faltante.length()-1);

                        else if (faltante.startsWith("\"")){
                            faltante = faltante.substring(1);
                            if (faltante.endsWith("\""))
                                faltante =faltante.substring(0,faltante.length()-1);
                        }
                        if (or.contains((faltante))){
                            or = or.replaceAll(faltante,"");

                        }
                        System.out.println(or);
                        or = balancear(or);
                        System.out.println(or);
                    }
                }
            }else if (cadena.contains("+")&&cadena.contains("-")){
                String mutador="";
                String original="";
                int indexOperadoresPlus = cadena.indexOf("+");
                int indexOperadoresMin  =  cadena.indexOf("-");
                if (indexOperadoresPlus<indexOperadoresMin){
                    mutador = cadena.substring(0,indexOperadoresPlus);
                    original = cadena.substring(indexOperadoresPlus);
                }
                else{
                    mutador = cadena.substring(0,indexOperadoresMin);
                    original = cadena.substring(indexOperadoresMin);
                }
                pilaAvanzada.push(original);
                while (!pilaAvanzada.isEmpty()){
                    String actual = (String)pilaAvanzada.pop();

                }

            }

        }

        return CInicioParentesis+or+CFinalParentesis;
    }
    public String buscarExpr(String search){
        String res = "";
        for (Map.Entry<String, String> entry : cadenaCompleta.entrySet()) {
            String value = entry.getKey();
            if (search.trim().contains(value)){
                return entry.getValue();



            }

        }
        return res;
    }

    public String balancear(String subcadena){
        String subcadenaBal = "";
        for (int i = 0;i<subcadena.length();i++){
            Character ch = subcadena.charAt(i);
            if (i+1<subcadena.length()){
                if (ch != subcadena.charAt(i+1)){
                    subcadenaBal += ch;
                }
            }else
                subcadenaBal += ch;
        }
        return subcadenaBal;
    }

    /**
     * Método para calcular el número de ocurrencias de un character
     * @param s string completo
     * @param c character a calcular ocurrencias
     * @return
     */
    public  int count( final String s, final char c ) {
        final char[] chars = s.toCharArray();
        int count = 0;
        for(int i=0; i<chars.length; i++) {
            if (chars[i] == c) {
                count++;
            }
            if (i+1<chars.length){
                if (chars[i]=='\''&&chars[i+1]=='+'&&chars[i+2]=='\'')
                    count--;
            }
        }
        return count;
    }
    public int calcularConcatenacion(String str){
        int posicion = 0;
        int posConc = str.indexOf("+")+1;
        String ident = buscarIdent(str);
        int indexIdent = str.indexOf(ident) + ident.length();
        if (indexIdent<posConc)
            return -1;
        else if (indexIdent>posConc)
            return 1;

        return posicion;
    }
    public String concatenacion(String anterior, String actual){
        String resultado = "";
        String cadenaOr=anterior;
        if (actual.contains("\""))
            cadenaOr = crearCadenasOr(actual);
        if (actual.contains("\'"))
            cadenaOr = crearCadenasOr(actual);
        int lado = calcularConcatenacion(actual);
        if (lado == -1){

            resultado = CInicioParentesis+buscarExpr(actual) +CFinalParentesis+COr+CInicioParentesis+ cadenaOr+CFinalParentesis;
        }
        else if (lado == 1){

            resultado = CInicioParentesis+cadenaOr +CFinalParentesis+COr+CInicioParentesis+ buscarExpr(actual)+CFinalParentesis;
        }
        return resultado;
    }
    public String buscarIdent(String search){
        String res = "";
        for (Map.Entry<String, String> entry : cadenaCompleta.entrySet()) {
            String value = entry.getKey();
            if (search.contains(value)){
                return value;
                // res = entry.getValue();


            }

        }
        ErrorSintactico errores = new ErrorSintactico();
        errores.WarningIdententificador("identificador no declarado");
        return res;

    }
    public String cadenasOrLista(String cadena){
        String or ="";

        if (cadena.contains("CHR")||cadena.contains("..")){
            if (cadena.contains("CHR")){
                int empieza = Integer.parseInt(cadena.substring(cadena.indexOf("(")+1,cadena.indexOf(")")));
                int termina = Integer.parseInt(cadena.substring(cadena.lastIndexOf("(")+1,cadena.lastIndexOf(")")));


                Regex convert = new Regex();
                or = convert.ab.abreviacionOr("["+(char)(empieza)+"-"+(char)(termina)+"]");
            }
            else{
                String empieza = (cadena.substring(cadena.indexOf("\'")+1,cadena.indexOf("\'", cadena.indexOf("\'") + 1)));

                String termina = (cadena.substring(cadena.lastIndexOf("\'")-1,cadena.lastIndexOf("\'")));
                Regex convert = new Regex();
                or =  convert.ab.abreviacionOr("["+(empieza)+"-"+(termina)+"]");
            }

        }
        return or;
    }

    public String concatenacionIdent(String anterior, String actual){
        return anterior + COr + buscarExpr(actual);
    }


    public void AnalizadorLexico(){
        System.out.println("");
        System.out.println("Generando Analizador Léxico....");
        encontrarNombre();
        generarCharactersYKeywords();
        generarTokens();
        generarClaseAnalizadora();
        generarMain();
        generarClaseToken();
        System.out.println("");
        System.out.println("Ejecute el Main de la carpeta generador para probar el input");

    }


}
