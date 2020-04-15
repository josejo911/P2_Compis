/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * En esta clase Simulamos los automatas generados
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Simulaciones {

    private String resultado;

    public Simulaciones(){ }

    public Simulaciones(Automata simulacion_NFA, String regex){
        simular(regex,simulacion_NFA);
    }

    public HashSet<Estados> eClosure(Estados eClosureEstado){
        Stack<Estados> pilaClosure = new Stack();
        Estados actual = eClosureEstado;
        actual.getTransiciones();
        HashSet<Estados> resultado = new HashSet();

        pilaClosure.push(actual);
        while(!pilaClosure.isEmpty()){
            actual = pilaClosure.pop();

            for (Transiciones t: (ArrayList<Transiciones>)actual.getTransiciones()){

                if (t.getSimbolo().equals(Main.EPSILON)&&!resultado.contains(t.getFin())){
                    resultado.add(t.getFin());
                    pilaClosure.push(t.getFin());
                }
            }
        }
        resultado.add(eClosureEstado);
        return resultado;
    }

    public HashSet<Estados> move(HashSet<Estados> estados, String simbolo){

        HashSet<Estados> alcanzados = new HashSet();
        Iterator<Estados> iterador = estados.iterator();
        while (iterador.hasNext()){
            for (Transiciones t: (ArrayList<Transiciones>)iterador.next().getTransiciones()){
                Estados siguiente = t.getFin();
                String simb = (String) t.getSimbolo();
                if (simb.equals(simbolo)){
                    alcanzados.add(siguiente);
                }
            }
        }   return alcanzados;
    }
    public boolean simular(String regex, Automata automata)
    {
        Estados inicial = automata.getEstadoInicial();
        ArrayList<Estados> estados = automata.getEstados();
        ArrayList<Estados> aceptacion = new ArrayList(automata.getEstadosAceptacion());

        HashSet<Estados> conjunto = eClosure(inicial);
        for (Character ch: regex.toCharArray()){
            conjunto = move(conjunto,ch.toString());
            HashSet<Estados> temp = new HashSet();
            Iterator<Estados> iter = conjunto.iterator();

            while (iter.hasNext()){
                Estados siguiente = iter.next();
                temp.addAll(eClosure(siguiente));
            }
            conjunto=temp;
        }
        boolean res = false;
        for (Estados estado_aceptacion : aceptacion){
            if (conjunto.contains(estado_aceptacion)){
                res = true;
            }
        }
        if (res){ return true; }

        else{ return false; }
    }
    public Estados move(Estados estado, String simbolo){
        ArrayList<Estados> alcanzados = new ArrayList();

        for (Transiciones t: (ArrayList<Transiciones>)estado.getTransiciones()){
            Estados siguiente = t.getFin();
            String simb = (String) t.getSimbolo();

            if (simb.equals(simbolo)&&!alcanzados.contains(siguiente)){
                alcanzados.add(siguiente);
            }
        }return alcanzados.get(0);
    }

    public String getResultado() {
        return resultado;
    }
}

