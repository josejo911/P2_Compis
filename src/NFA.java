/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * Clase encargada de generar el automata utilizano las operaciones de
 * Concatenacion, Kleene y OR
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class NFA<T> {


    private Automata afn;
    private String regex;
    public final char CConcatenacion = '∆';
    public final char CKleene = '∞';
    public final char CInicioParentesis  = '≤';
    public final char CFinalParentesis  = '≥';
    public final char COr  = '∫';
    public final char CPlus  = '∩';
    public final char CInt  = 'Ω';





    public NFA(String regex) {
        this.regex = regex;
    }

    public NFA(){

    }

    public void ConstructorAutomata(){
        try {
            Stack pilaNFA = new Stack();
            for (Character c : this.regex.toCharArray()) {
                switch(c){
                    case CKleene:
                        Automata kleene = cerraduraKleene((Automata) pilaNFA.pop());
                        pilaNFA.push(kleene);
                        this.afn=kleene;
                        break;

                    case CConcatenacion:
                        Automata concat_param1 = (Automata)pilaNFA.pop();
                        Automata concat_param2 = (Automata)pilaNFA.pop();
                        Automata concat_result = concatenacion(concat_param1,concat_param2);

                        pilaNFA.push(concat_result);
                        this.afn=concat_result;
                        break;

                    case COr:
                        Automata union_param1 = (Automata)pilaNFA.pop();
                        Automata union_param2 = (Automata)pilaNFA.pop();
                        Automata union_result = union(union_param1,union_param2);
                        pilaNFA.push(union_result);
                        this.afn = union_result;
                        break;

                    default:
                        Automata simple = afnSimple((T) Character.toString(c));
                        pilaNFA.push(simple);
                        this.afn=simple;
                }
            }
            this.afn.createAlfabeto(regex);
            this.afn.setTipo("NFA");


        }catch(Exception e){
            System.out.println("Expresión mal ingresada");
        }
    }

    public Automata afnSimple(T simboloRegex)
    {
        Automata automataFN = new Automata();
        Estados inicial = new Estados(0);
        Estados aceptacion = new Estados(1);
        Transiciones tran = new Transiciones(inicial, aceptacion,  simboloRegex);
        inicial.setTransiciones(tran);
        automataFN.addEstados(inicial);
        automataFN.setEstadoInicial(inicial);
        automataFN.addEstadosAceptacion(aceptacion);
        automataFN.setLenguajeR(simboloRegex+"");
        automataFN.addEstados(aceptacion);
        return automataFN;
    }

    public Automata concatenacion(Automata NFA1, Automata NFA2){

        Automata concatenacion = new Automata();

        int i=0;
        for (i=0; i < NFA2.getEstados().size(); i++) {
            Estados tmp = (Estados) NFA2.getEstados().get(i);
            tmp.setId(i);

            if (i==0){
                concatenacion.setEstadoInicial(tmp);
            }
            if (i == NFA2.getEstados().size()-1){

                for (int k = 0;k<NFA2.getEstadosAceptacion().size();k++)
                {
                    tmp.setTransiciones(new Transiciones((Estados) NFA2.getEstadosAceptacion().get(k),NFA1.getEstadoInicial(),Main.EPSILON));
                }
            }
            concatenacion.addEstados(tmp);

        }
        for (int j =0;j<NFA1.getEstados().size();j++){
            Estados tmp = (Estados) NFA1.getEstados().get(j);
            tmp.setId(i);

            if (NFA1.getEstados().size()-1==j)
                concatenacion.addEstadosAceptacion(tmp);
            concatenacion.addEstados(tmp);
            i++;
        }

        HashSet alfabeto = new HashSet();
        alfabeto.addAll(NFA1.getAlfabeto());
        alfabeto.addAll(NFA2.getAlfabeto());
        concatenacion.setAlfabeto(alfabeto);
        concatenacion.setLenguajeR(NFA1.getLenguajeR()+" " + NFA2.getLenguajeR());

        return concatenacion;
    }
    public Automata cerraduraKleene(Automata automataFN)
    {
        Automata afn_kleene = new Automata();

        Estados nuevoInicio = new Estados(0);
        afn_kleene.addEstados(nuevoInicio);
        afn_kleene.setEstadoInicial(nuevoInicio);

        for (int i=0; i < automataFN.getEstados().size(); i++) {
            Estados tmp = (Estados) automataFN.getEstados().get(i);
            tmp.setId(i + 1);
            afn_kleene.addEstados(tmp);
        }

        Estados nuevoFin = new Estados(automataFN.getEstados().size() + 1);
        afn_kleene.addEstados(nuevoFin);
        afn_kleene.addEstadosAceptacion(nuevoFin);

        Estados anteriorInicio = automataFN.getEstadoInicial();

        ArrayList<Estados> anteriorFin    = automataFN.getEstadosAceptacion();

        nuevoInicio.getTransiciones().add(new Transiciones<>(nuevoInicio, anteriorInicio, Main.EPSILON));
        nuevoInicio.getTransiciones().add(new Transiciones<>(nuevoInicio, nuevoFin, Main.EPSILON));

        for (int i =0; i<anteriorFin.size();i++){
            anteriorFin.get(i).getTransiciones().add(new Transiciones<>(anteriorFin.get(i), anteriorInicio,Main.EPSILON));
            anteriorFin.get(i).getTransiciones().add(new Transiciones<>(anteriorFin.get(i), nuevoFin, Main.EPSILON));
        }
        afn_kleene.setAlfabeto(automataFN.getAlfabeto());
        afn_kleene.setLenguajeR(automataFN.getLenguajeR());
        return afn_kleene;
    }

    public Automata union(Automata NFA1, Automata NFA2){
        Automata afn_union = new Automata();
        Estados nuevoInicio = new Estados(0);
        nuevoInicio.setTransiciones(new Transiciones(nuevoInicio,NFA2.getEstadoInicial(),Main.EPSILON));

        afn_union.addEstados(nuevoInicio);
        afn_union.setEstadoInicial(nuevoInicio);
        int i=0;
        for (i=0; i < NFA1.getEstados().size(); i++) {
            Estados tmp = (Estados) NFA1.getEstados().get(i);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
        }
        for (int j=0; j < NFA2.getEstados().size(); j++) {
            Estados tmp = (Estados) NFA2.getEstados().get(j);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
            i++;
        }

        Estados nuevoFin = new Estados(NFA1.getEstados().size() +NFA2.getEstados().size()+ 1);
        afn_union.addEstados(nuevoFin);
        afn_union.addEstadosAceptacion(nuevoFin);


        Estados anteriorInicio = NFA1.getEstadoInicial();
        ArrayList<Estados> anteriorFin    = NFA1.getEstadosAceptacion();
        ArrayList<Estados> anteriorFin2    = NFA2.getEstadosAceptacion();

        nuevoInicio.getTransiciones().add(new Transiciones<>(nuevoInicio, anteriorInicio, Main.EPSILON));

        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin.get(k).getTransiciones().add(new Transiciones<>(anteriorFin.get(k), nuevoFin, Main.EPSILON));

        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin2.get(k).getTransiciones().add(new Transiciones<>(anteriorFin2.get(k),nuevoFin,Main.EPSILON));

        HashSet alfabeto = new HashSet();
        alfabeto.addAll(NFA1.getAlfabeto());
        alfabeto.addAll(NFA2.getAlfabeto());
        afn_union.setAlfabeto(alfabeto);
        afn_union.setLenguajeR(NFA1.getLenguajeR()+" " + NFA2.getLenguajeR());
        return afn_union;
    }
    public String getRegex() { return regex; }
    public void setRegex(String regex) { this.regex = regex; }
    public Automata getAfn() { return this.afn; }
    public void setAfn(Automata afn) { this.afn = afn; }
}
