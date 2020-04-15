/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * En esta clase tomamos la expresion ingresada y la volvemos a posfix
 * tomamos la precedencia de la lista de operadores ya ingresos en el constructor
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Regex extends Operadores{
    private final Map<Character, Integer> precedenciaOperadores;
    public Abreviaciones ab = new Abreviaciones();
    private final String escapeChars = "\"" + "\'" +"\\";

    public Regex()
    {
        Map<Character, Integer> map = new HashMap<>();
            map.put(CInicioParentesis, 1);
            map.put(COr, 2);
            map.put(CConcatenacion, 3);
            map.put(CInt, 4);
            map.put(CKleene, 4);
            map.put(CPlus, 4);
            precedenciaOperadores = Collections.unmodifiableMap(map);
    }

    private Integer getPrecedencia(Character c) {
        Integer precedencia = precedenciaOperadores.get(c);
        return precedencia == null ? 6 : precedencia;
    }

    public String insertCharAt(String s, int pos, Object ch){
        return s.substring(0,pos)+ch+s.substring(pos+1);
    }

    public String appendCharAt(String s, int pos, Object ch){
        String val = s.substring(pos,pos+1);
        return s.substring(0,pos)+val+ch+s.substring(pos+1);
    }

    private int parentesisDer (String regex){
        int P1=0;
        for (int i = 0;i<regex.length();i++){
            Character ch = regex.charAt(i);
            if (ch.equals(CFinalParentesis)){
                P1++;
            }
        }
        return P1;
    }
    private int parentesisIzq (String regex){
        int P1=0;
        for (int i = 0;i<regex.length();i++){
            Character ch = regex.charAt(i);
            if (ch.equals(CInicioParentesis)){
                P1++;
            }

        }
        return P1;
    }

    public boolean balancearParentesis(String regex){
        int P1 = parentesisIzq(regex);
        int P2 = parentesisDer(regex);


        if(P1 != P2){
            return false;
        }
        return true;
    }

    public  String formatRegEx(String regex) {
        regex = regex.trim();
        regex = abreviaturaCerraduraPositiva(regex);
        regex = abreviaturaInterrogacion(regex);
        String  regexExplicit = new String();
        List<Character> operadores = Arrays.asList(COr, CPlus, CKleene);
        List<Character> operadoresBinarios = Arrays.asList(COr);
        for (int i = 0; i < regex.length(); i++)
        {
            Character c1 = regex.charAt(i);

            if (i + 1 < regex.length())
            {

                Character c2 = regex.charAt(i + 1);

                regexExplicit += c1;

                if (!c1.equals(CInicioParentesis) && !c2.equals(CFinalParentesis) && !operadores.contains(c2) && !operadoresBinarios.contains(c1))
                {
                    regexExplicit += this.CConcatenacion;

                }

            }
        }
        regexExplicit += regex.charAt(regex.length() - 1);


        return regexExplicit;
    }

    public String abreviaturaCerraduraPositiva(String regex){
        int compare = 0;
        for (int i = 0; i<regex.length();i++){
            Character ch = regex.charAt(i);
            if (ch.equals(CPlus))
            {
                if (regex.charAt(i-1) == CFinalParentesis){
                    int fixPosicion = i;
                    while (fixPosicion != -1)
                    {
                        if (regex.charAt(fixPosicion)==CFinalParentesis)
                        {
                            compare++;
                        }
                        if (regex.charAt(fixPosicion)==CInicioParentesis)
                        {
                            compare--;
                            if (compare ==0)
                                break;
                        }
                        fixPosicion--;
                    }
                    String regexAb = regex.substring(fixPosicion,i);
                    regex = insertCharAt(regex,i,regexAb+CKleene);
                }
                else
                {
                    regex = insertCharAt(regex,i,regex.charAt(i-1)+CKleene);
                }


            }

        }return regex;
    }

    public String abreviaturaInterrogacion(String regex)
    {
        for (int i = 0; i<regex.length();i++){
            Character ch = regex.charAt(i);
            if (ch.equals(CInt))
            {
                if (regex.charAt(i-1) == CFinalParentesis)
                {
                    regex = insertCharAt(regex,i,COr+""+Main.EPSILON+CFinalParentesis);
                    int j =i;
                    while (j!=0)
                    {
                        if (regex.charAt(j)==CInicioParentesis)
                        {
                            break;
                        }
                        j--;
                    }
                    regex=appendCharAt(regex,j,CInicioParentesis);
                }
                else
                {
                    regex = insertCharAt(regex,i,COr+Main.EPSILON+CFinalParentesis);
                    regex = insertCharAt(regex,i-1,CInicioParentesis+""+regex.charAt(i-1));
                }
            }
        }return regex;
    }

    public  String infixToPostfix(String regex) { if (!balancearParentesis(regex))
        return "";
        String postfix = new String();
        regex = ab.abreviacionOr(regex);
        Stack<Character> stack = new Stack<>();

        String formattedRegEx = formatRegEx(regex);

        for (int i = 0;i<formattedRegEx.length();i++) {
            Character c = formattedRegEx.charAt(i);
            switch (c) {
                case CInicioParentesis:
                    stack.push(c);
                    break;

                case CFinalParentesis:
                    while (!stack.peek().equals(CInicioParentesis)) {
                        postfix += stack.pop();
                    }
                    stack.pop();
                    break;

                default:
                    while (stack.size() > 0)
                    {
                        Character peekedChar = stack.peek();

                        Integer peekedCharPrecedence = getPrecedencia(peekedChar);
                        Integer currentCharPrecedence = getPrecedencia(c);

                        if (peekedCharPrecedence >= currentCharPrecedence)
                        {
                            postfix += stack.pop();

                        }
                        else
                        {
                            break;
                        }
                    }
                    stack.push(c);
                    break;
            }

        }

        while (stack.size() > 0)
            postfix += stack.pop();

        return postfix;
    }


}
