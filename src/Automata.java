/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * En esta clase generamos un arreglo de conjuntos finales
 * segun el algoritmo de thomson siempre se mantiene un unico de aceptacion o final
 */
import java.util.ArrayList;
import java.util.HashSet;

public class Automata {

    private Estados inicial;
    private final ArrayList<Estados> aceptacion;
    private final ArrayList<Estados> estados;
    private HashSet alfabeto;
    private String tipo;
    private String[] resultadoRegex;
    private String lenguajeR;

    public Automata()
    {
        this.estados = new ArrayList();
        this.aceptacion = new ArrayList();
        this.alfabeto = new HashSet();
        this.resultadoRegex = new String[3];

    }

    public Estados getEstadoInicial() {
        return inicial;
    }

    public void setEstadoInicial(Estados inicial) {
        this.inicial = inicial;
    }

    public Estados getInicial() {
        return inicial;
    }

    public void setInicial(Estados inicial) {
        this.inicial = inicial;
    }

    public ArrayList<Estados> getEstadosAceptacion() {
        return aceptacion;
    }

    public void addEstadosAceptacion(Estados fin) {
        this.aceptacion.add(fin);
    }

    public ArrayList<Estados> getEstados() {
        return estados;
    }

    public Estados getEstados(int index){
        return estados.get(index);
    }

    public void addEstados(Estados estado) {
        this.estados.add(estado);
    }


    public HashSet getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(HashSet alfabeto){
        this.alfabeto=alfabeto;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public void createAlfabeto(String regex) {
        for (Character ch: regex.toCharArray()){

            if (ch != '|' && ch != '.' && ch != '*' && ch != Main.EPSILON_CHAR)
                this.alfabeto.add(Character.toString(ch));
        }
    }
    public String getTipo(){
        return this.tipo;
    }

    public String[] getResultadoRegex() {
        return resultadoRegex;
    }

    public void addResultadoRegex(int key, String value) {
        this.resultadoRegex[key] = value;
    }

    public String getLenguajeR() {
        return lenguajeR;
    }

    public void setLenguajeR(String lenguajeR) {
        this.lenguajeR = lenguajeR;
    }

}
