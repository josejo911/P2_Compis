import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 */

public class Lexer extends Operadores {

    private Automata letra__;
    private Automata digito__;
    private Automata ident_;
    private Automata string_;
    private Automata character_;
    private Automata numero__;
    private Automata SET_basic_;
    private Automata igual_;
    private Automata mas_menos;
    private Automata espacio_;
    private Automata charBasico_;
    private Automata compilador_;
    private Automata final_;
    private final String espacio = CInicioParentesis  +" "+CFinalParentesis+CKleene;
    private final String ANY = this.espacio+"[ -.]"+COr+"[@-z]"+this.espacio;
    private final HashMap<Integer,String> cadena;
    private ArrayList<Automata> generador = new ArrayList();
    private boolean union = false;
    private final Stack compare = new Stack();
    private Simulaciones sim;
    private boolean output = true;

    public Lexer(HashMap cadena){
        this.sim = new Simulaciones();
        this.cadena=cadena;
    }

    public void vocabulario(){
        Regex convert = new Regex();

        String regex = convert.infixToPostfix(ANY);
        NFA thomson = new NFA(regex);
        thomson.ConstructorAutomata();
        letra__ = thomson.getAfn();
        letra__.setTipo("Letra");

        regex = convert.infixToPostfix(CInicioParentesis+" "+ CFinalParentesis+CKleene);
        thomson.setRegex(regex);
        thomson.ConstructorAutomata();
        espacio_  = thomson.getAfn();
        espacio_.setTipo("Espacio");

        regex = convert.infixToPostfix("[0-9]");
        thomson.setRegex(regex);
        thomson.ConstructorAutomata();
        digito__ = thomson.getAfn();
        digito__.setTipo("Digito");

        Automata digitKleene = thomson.cerraduraKleene(digito__);
        numero__ = thomson.concatenacion(digito__, digitKleene);
        numero__.setTipo("Número");
        Automata letterOrDigit = thomson.union(letra__, digito__);
        Automata letterOrDigitKleene = thomson.cerraduraKleene(letterOrDigit);
        ident_ = thomson.concatenacion(letra__, letterOrDigitKleene);
        ident_.setTipo("Identificador");
        Automata ap1 = thomson.afnSimple("\"");
        Automata ap2 = thomson.afnSimple("\"");
        Automata stringKleene = thomson.union(numero__, letra__);
        string_ = thomson.cerraduraKleene(stringKleene);
        string_ = thomson.concatenacion(ap1, string_);
        string_ = thomson.concatenacion(string_,ap2);
        string_.setTipo("string");

        Automata apch1 = thomson.afnSimple("\'");
        Automata apch2 = thomson.afnSimple("\'");
        character_ = thomson.union(numero__, letra__);
        character_ = thomson.concatenacion(ap1, character_);
        character_ = thomson.concatenacion(character_,ap2);
        regex = convert.infixToPostfix("CHR()");
        thomson = new NFA(regex);
        thomson.ConstructorAutomata();
        Automata lefChar = thomson.getAfn();
        Automata rightChar = thomson.afnSimple(")");
        lefChar = thomson.concatenacion(numero__,lefChar);
        Automata innerChar = thomson.concatenacion(rightChar,lefChar);
        character_ = thomson.union(character_,innerChar);
        character_.setTipo("character");

        Automata pointChar = thomson.afnSimple(".");
        Automata pointChar2 = thomson.afnSimple(".");
        pointChar = thomson.concatenacion(pointChar, pointChar2);
        pointChar = thomson.concatenacion(pointChar, espacio_);
        pointChar = thomson.concatenacion(espacio_, pointChar);
        charBasico_ = thomson.concatenacion(character_, pointChar);
        charBasico_ = thomson.concatenacion(charBasico_,character_);
        charBasico_.setTipo("Basic Char");

        SET_basic_ = thomson.union(string_, ident_);
        SET_basic_ = thomson.union(SET_basic_,charBasico_);
        SET_basic_.setTipo("Basic Set");

        regex = convert.infixToPostfix(espacio+"="+espacio);
        thomson.setRegex(regex);
        thomson.ConstructorAutomata();
        igual_  = thomson.getAfn();
        igual_.setTipo("=");


        Automata plus = thomson.afnSimple("+");
        Automata minus = thomson.afnSimple("-");
        mas_menos = thomson.union(plus, minus);
        mas_menos.setTipo("(+|-)");
        regex = convert.infixToPostfix("COMPILER");
        thomson.setRegex(regex);
        thomson.ConstructorAutomata();
        compilador_ = thomson.getAfn();
        compilador_.setTipo("\"COMPILER\"");
        regex = convert.infixToPostfix("END");
        thomson.setRegex(regex);
        thomson.ConstructorAutomata();
        final_ = thomson.getAfn();
        final_.setTipo("\"END\"");

    }

    public ArrayList<Automata> conjuntoAutomatas(){
        ArrayList<Automata> conjunto = new ArrayList();
        conjunto.add(this.letra__);
        conjunto.add(this.digito__);
        conjunto.add(this.numero__);
        conjunto.add(this.igual_);
        conjunto.add(this.ident_);
        conjunto.add(this.string_);
        conjunto.add(this.character_);
        conjunto.add(this.mas_menos);
        conjunto.add(this.SET_basic_);
        conjunto.add(this.espacio_);
        return conjunto;

    }



    public void revisar(HashMap<Integer,String> cadena) {

        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            String[] parts = value.split(" ");
            for (int i = 0; i < parts.length; i++) {
                this.revisarAutomatasInd(parts[i], generador, key);
            }


        }
    }
    public void construct(HashMap<Integer,String> cadena){
        int linea = 1;
        int index = 0;

        if (!this.cadena.get(linea).contains("COMPILER"))
            System.out.println("No tiene la palabra COMPILER");
        else
            index = this.cadena.get(linea).indexOf("R")+1;

        ArrayList res2 = revisarAutomata(this.ident_,linea,index);
        ArrayList scan = scannerSpecification(linea);
        if (scan.isEmpty())
            output=false;

        linea = (int)scan.get(0);
        int index4 = 0;
        if (!this.cadena.get(linea).contains("END"))
            System.out.println("No tiene la palabra END");
        else
            index4 = this.cadena.get(linea).indexOf("D")+1;


        ArrayList res4 = revisarAutomata(this.ident_,linea,index4);

        if (!res4.isEmpty()&&!res2.isEmpty()){
            if (!res4.get(1).toString().trim().equals(res2.get(1).toString().trim())){
                Main.errores.errorSintactico(linea, "Los identificadores no coinciden");
                System.out.println( res4.get(1).toString().trim()+ " y "+ res2.get(1).toString().trim() +" no coinciden");
                output=false;
            }
        }
    }

    public ArrayList revisarExp (String regex,int linea,int index){
        String cadena_encontrada="";
        String cadena_revisar = this.cadena.get(linea).substring(index);
        ArrayList res = new ArrayList();
        try{
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cadena_revisar);
            Pattern p = Pattern.compile("."+"|"+"."+this.espacio);
            Matcher m = p.matcher(cadena_revisar);

            if (m.matches())
                return new ArrayList();
            if (matcher.find()) {
                cadena_encontrada=matcher.group();


                res.add(matcher.end());
                res.add(cadena_encontrada);
                return res;
            }
            else{
                if (!cadena_revisar.isEmpty())//si
                    System.out.println("Error en la linea " + linea + ": la cadena " + cadena_revisar + " es inválida");
                else
                    System.out.println("Error en la línea " + linea + ": falta un identificador");
            }
        } catch(Exception e){
            System.out.println("Error en la linea " + linea+ ": la cadena " + cadena_revisar + " es inválida");
        }
        return res;
    }


    public ArrayList<String> scannerSpecification(int linea) {
        int returnIndex = 0;
        String returnString = "";
        linea = proximaLinea(linea);

        if (!this.cadena.get(linea).contains("CHARACTERS")) {
            Main.errores.errorSintactico(linea, "NO contiene la palabra CHARACTERS");
            return new ArrayList();
        }
        linea = proximaLinea(linea);
        while (true) {
            boolean res2 = setDeclaration(linea);
            if (!res2) {
                linea = regresarLinea(linea);
                break;
            }
            linea = proximaLinea(linea);
        }
        linea = proximaLinea(linea);
        if (!this.cadena.get(linea).contains("KEYWORDS")) {
            Main.errores.errorSintactico(linea, " NO contiene la palabra KEYWORDS");
            return new ArrayList();
        }
        linea = proximaLinea(linea);
        while (true) {
            boolean keywords = declaracionDeKeywords(linea);
            if (!keywords) {
                linea = regresarLinea(linea);
                break;
            }
            linea = proximaLinea(linea);

        }

        linea = proximaLinea(linea);
        if (!this.cadena.get(linea).contains("TOKENS")) {
            Main.errores.errorSintactico(linea, "NO Contiene la palabra TOKENS");
            return new ArrayList();
        }
        linea = proximaLinea(linea);
        while (true) {
            boolean token = declaracionDeTokens(linea);
            if (token)
                linea = proximaLinea(linea);
            else
                break;
        }

        linea = proximaLinea(linea);
        if (cadena.get(linea).contains("IGNORE")) {
            while (true) {
                boolean space = whiteSpaceDeclaration(linea);
                if (space)
                    linea = proximaLinea(linea);
                else {
                    break;
                }
            }
        }
        ArrayList outputScan = new ArrayList();
        outputScan.add(linea);
        outputScan.add(true);
        return outputScan;
    }

    public boolean declaracionDeKeywords(int linea){

        if (this.cadena.get(linea).contains("TOKENS") ||this.cadena.get(linea).contains("IGNORE")||
                this.cadena.get(linea).contains("END")){
            return false;
        }
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")-1;
            while (this.cadena.get(linea).substring(0, indexSearch).contains(" "))
                indexSearch--;

            boolean identifier = revisarAutomata(this.ident_,this.cadena.get(linea).substring(0,indexSearch));
            if (!identifier){
                return false;
            }

        }catch(Exception e){}
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")+1;
            String CaRevisar = this.cadena.get(linea).substring(indexSearch);
            while(CaRevisar.startsWith(" "))
                CaRevisar = this.cadena.get(linea).substring(++indexSearch);
            if (CaRevisar.substring(0, CaRevisar.length()).contains("."))
                CaRevisar = CaRevisar.substring(0, CaRevisar.length()-1);
            else{
                Main.errores.errorSintactico(linea, "NO CONTIENE PUNTO AL FINAL");
            }
            boolean resSet = revisarAutomata(this.string_,CaRevisar);
        }catch(Exception e){
            Main.errores.errorSemantico(linea, "LA EXPRESION ESTA MAL INGRESADA");
            this.output=false;
        }

        return true;
    }

    public boolean whiteSpaceDeclaration(int linea){
        return this.cadena.get(linea).contains("IGNORE");
    }

    public boolean declaracionDeTokens(int linea){

        if (this.cadena.get(linea).contains("END")||this.cadena.get(linea).contains("IGNORE"))
            return false;
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")-1;
            while (this.cadena.get(linea).substring(0, indexSearch).contains(" "))
                indexSearch--;
            boolean identifier = revisarAutomata(this.ident_,this.cadena.get(linea).substring(0,indexSearch));
            if (!identifier){
                return false;
            }

        }catch(Exception e){}

        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")+1;
            String CaRevisar = this.cadena.get(linea).substring(indexSearch);
            while(CaRevisar.startsWith(" "))
                CaRevisar = this.cadena.get(linea).substring(++indexSearch);
            if (CaRevisar.substring(0, CaRevisar.length()).contains("."))
                CaRevisar = CaRevisar.substring(0, CaRevisar.length()-1);
            else{
                Main.errores.Warning(linea, "NO TIENE PUNTO AL FINAL");
            }

            if (CaRevisar.contains("EXCEPT"))
                CaRevisar = CaRevisar.substring(0,CaRevisar.indexOf("EXCEPT")).trim();
            boolean tkExpr = expresionToken(linea, CaRevisar);

            return tkExpr;
        }catch(Exception e){
            Main.errores.errorSemantico(linea, this.cadena.get(linea));
            this.output=false;
        }
        return false;
    }

    public boolean expresionToken(int linea,String CaRevisar){
        String antesRevisar = CaRevisar;
        CaRevisar = CaRevisar.replaceAll("\\{", CInicioParentesis+"");
        CaRevisar = CaRevisar.replaceAll("\\}", CFinalParentesis+""+CKleene);
        CaRevisar = CaRevisar.replaceAll("\\[", CInicioParentesis+"");
        CaRevisar = CaRevisar.replaceAll("\\]",CFinalParentesis+"" +CInt);
        CaRevisar = CaRevisar.replaceAll("\\|",COr+"");
        CaRevisar = CaRevisar.replaceAll("\\(",CInicioParentesis+"");
        CaRevisar = CaRevisar.replaceAll("\\)",CFinalParentesis+"");
        String regex;
        Regex convert = new Regex();
        regex = convert.infixToPostfix(CaRevisar);

        if (regex.isEmpty()){
            Main.errores.errorSintactico(linea, "LA EXPRESION ESTA MAL INGRESADA"+"\n" + antesRevisar);
            return false;
        }
        return true;
    }

    public boolean setDeclaration(int linea){

        if (this.cadena.get(linea).contains("END")||this.cadena.get(linea).contains("KEYWORDS")||
                this.cadena.get(linea).contains("TOKENS"))
            return false;
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")-1;
            while (this.cadena.get(linea).substring(0, indexSearch).contains(" "))
                indexSearch--;

            boolean identifier = revisarAutomata(this.ident_,this.cadena.get(linea).substring(0,indexSearch));
            if (!identifier){
                return false;
            }
        }catch(Exception e){
            Main.errores.errorSintactico(linea, "NO CONTIENE EL SIGNO '=' ");
            this.output=false;
        }
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=");
            int indexSearch2 = indexSearch + 1;
            boolean equals = revisarAutomata(this.igual_,this.cadena.get(linea).substring(indexSearch, indexSearch2));
            if (!equals)
                return false;
        }catch(Exception e){
            Main.errores.errorSintactico(linea, "NO CONTIENE EL SIGNO '='  ");
            this.output=false;
        }
        try{
            int indexSearch = this.cadena.get(linea).indexOf("=")+1;
            String CaRevisar = this.cadena.get(linea).substring(indexSearch);
            while(CaRevisar.startsWith(" "))
                CaRevisar = this.cadena.get(linea).substring(++indexSearch);
            if (CaRevisar.substring(0, CaRevisar.length()).contains("."))
                CaRevisar = CaRevisar.substring(0, CaRevisar.length()-1);
            else{
                Main.errores.Warning(linea, "NO CONTIENE EL PUNTO AL FINAL  ");

            }
            boolean resSet = set(linea,CaRevisar);
        }catch(Exception e){
            this.output=false;
        }
        return true;
    }

    public ArrayList<String> Char(int linea,int lastIndex){

        ArrayList res = revisarAutomata(this.character_,linea,lastIndex);
        if (!res.isEmpty()){
            return res;
        }
        return new ArrayList();
    }

    public int regresarArreglo(ArrayList param){
        if (!param.isEmpty()){
            return (int)param.get(0);
        }
        return 0;
    }

    public boolean set(int linea,String regex){
        int index = 0;

        String revisar =regex;
        if (regex.contains("+"))
            revisar = regex.substring(0,regex.indexOf("+"));
        else if (regex.contains("-"))
            revisar = regex.substring(0,regex.indexOf("-"));
        boolean basic = basicSet(linea,revisar);
        if (!basic)
            return false;
        if (regex.contains("+")){
            String[] parts = regex.split("\\+");
            for (String part : parts) {
                revisarAutomata(this.SET_basic_, part);
            }
        }return true;
    }

    public ArrayList revisarAutomata(Automata param,int linea, int index){

        String cadena_revisar = this.cadena.get(linea).substring(index);
        int preIndex = 0;
        try{
            while (cadena_revisar.startsWith(" ")){
                preIndex++;
                cadena_revisar = cadena_revisar.substring(preIndex, cadena_revisar.length());
            }
            if (cadena_revisar.contains(" "))
                cadena_revisar = cadena_revisar.substring(0, cadena_revisar.indexOf(" ")+1);
        }catch(Exception e){}
        try{
            cadena_revisar = cadena_revisar.substring(0, cadena_revisar.lastIndexOf("."));
        }catch(Exception e){}

        ArrayList resultado = new ArrayList();
        boolean returnValue=sim.simular(cadena_revisar.trim(), param);

        if (returnValue){
            resultado.add(cadena_revisar.length()+preIndex);
            resultado.add(cadena_revisar);
            return resultado;
        }
        else{
            if (!cadena_revisar.isEmpty()){
                System.out.println("ERROR EN LINEA >>> " + linea + ": la cadena " + cadena_revisar + " es no es:" + param.getTipo());
                this.output=false;
            }
        }return resultado;
    }


    public void revisarAutomatasInd(String regex, ArrayList<Automata> conjunto,int linea){
        ArrayList<Boolean> resultado = new ArrayList();
        for (int j = 0;j<conjunto.size();j++){
            resultado.add(sim.simular(regex, conjunto.get(j)));
        }
        ArrayList<Integer> posiciones = checkBoolean(resultado);

        for (int k = 0;k<posiciones.size();k++){
            System.out.println(regex+ ": " + conjunto.get(posiciones.get(k)).getTipo());
        }
        if (posiciones.isEmpty()){
            System.out.println("ERROR EN LINEA >>> " + linea +" : "+regex+ " no fue reconocido");
        }
    }


    public ArrayList<Integer>  checkBoolean(ArrayList<Boolean> bool){
        ArrayList<Integer> posiciones = new ArrayList();

        for (int i = 0;i<bool.size();i++){
            if (bool.get(i))
                posiciones.add(i);
        }
        return posiciones;
    }

    public boolean basicSet(int linea,String regex){

        boolean resBasicSet = this.revisarAutomata(this.SET_basic_,regex.trim());

        if (!resBasicSet)
            Main.errores.errorSintactico(linea, "NO FUE PUDO SER RECONOCIDO " + regex);
        return resBasicSet;

    }
    public ArrayList<Automata> getGenerador() {
        return generador;
    }
    public boolean getOutput(){
        if (output){
            System.out.println("El archivo ingresado en el lenguaje COCOL/R ha sido aceptado ");
            return true;
        }
        else{
            System.out.println("El archivo ingresado en el lenguaje COCOL/R  NO ha sido aceptado ya que su estructura no coincide para lectura");
            return false;
        }
    }

    public Integer proximaLinea(int linea){
        while (true){
            if (this.cadena.containsKey(++linea))
                return linea;
        }
    }

    public Integer regresarLinea(int linea){
        while (true){
            if (this.cadena.containsKey(--linea))
                return linea;
        }
    }
    public boolean revisarAutomata(Automata param, String regex){
        return sim.simular(regex, param);
    }

    public boolean basicChar(int linea, String regex){
        String letterChar = "abcdefghlmnopqrstuvgwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\".$%&/()=?¿+-[]-´Ç-., ";
        if (regex.startsWith("\'"))
            regex = regex.replaceAll("\'", "");
        if (letterChar.contains(regex))
            return true;
        if (regex.contains("CHR(012345679)"))
            return true;
        return false;
    }
}
