/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Algoritmo de thomson
 */
import java.util.ArrayList;

public class Estados<T> {


    private T id;
    private ArrayList<Transiciones> transiciones = new ArrayList();

    public Estados(T id, ArrayList<Transiciones> transiciones) {
        this.id = id;
        this.transiciones = transiciones;
    }
    public Estados(T identificador) {
        this.id = identificador;

    }
    public ArrayList<Transiciones> getTransiciones() {

        return transiciones;
    }
    public void setTransiciones(Transiciones tran) {
        this.transiciones.add(tran);
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return this.id.toString();
    }

}
