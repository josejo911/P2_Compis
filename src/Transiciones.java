/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 * Clase encargada de cad auna de las transiciones en el automata
 */
public class Transiciones<T> {

    private Estados inicio;
    private Estados fin;
    private T token;

    public Transiciones(Estados inicio, Estados fin, T simbolo) {
        this.inicio = inicio;
        this.fin = fin;
        this.token = simbolo;
    }

    public Estados getInicio() {
        return inicio;
    }

    public void setInicio(Estados inicio) {
        this.inicio = inicio;
    }

    public Estados getFin() {
        return fin;
    }

    public void setFin(Estados fin) {
        this.fin = fin;
    }

    public T getSimbolo() {
        return token;
    }

    public void setSimbolo(T simbolo) {
        this.token = simbolo;
    }

    @Override
    public String toString(){
        return "(" + inicio.getId() +"-" + token  +"-"+fin.getId()+")";
    }

}
