/**
 * Universidad del Valle de Guatemala
 * Jose Javier Jo Escobar,14343
 * Clase encargada de la administracion de nodos en el arbol
 */

public class Nodo<T> implements Comparable<Nodo> {

    private T id;
    private T regex;
    private int numNodo;
    private Nodo izq, der;
    private boolean isLeaf;


    public Nodo(T regex) {
        this.regex = regex;
        this.izq = new Nodo();
        this.der = new Nodo();
    }

        public Nodo() {    }

        public T getId() { return id; }

        public void setId(T id) { this.id = id; }

        public T getRegex() { return regex; }

        public void setRegex(T regex) { this.regex = regex; }

        public int getNumeroNodo() { return numNodo; }

        public void setNumeroNodo(int numeroNodo) { this.numNodo = numeroNodo; }

        public Nodo getIzquierda() { return izq; }

        public void setIzquierda(Nodo izquierda) { this.izq = izquierda; }

        public Nodo getDerecha() { return der; }

        public void setDerecha(Nodo derecha) { this.der = derecha; }

        public boolean isIsLeaf() { return isLeaf; }

        public void setIsLeaf(boolean isLeaf) { this.isLeaf = isLeaf; }


    public String postOrder() {

            String res = "";
            if (this.izq.getId() != null)
                res += "" + this.izq.postOrder();
            if (this.id != null)
                res += this.id + "";
            if (this.der.getId() != null)
                res += this.der.postOrder() + "";
            return res;
    }
    public String preOrder() {

            String res = "";
            if (id != null)
                res += this.id;
            if (izq.getId() != null)
                res += this.izq.preOrder();
            if (der.getId() != null)
                res += this.der.preOrder();
            return res;
    }
    @Override
    public int compareTo(Nodo o) {
        return Integer.compare(numNodo, o.getNumeroNodo());
    }


    @Override
    public String toString() {
        String regexd = "" + numNodo;
        return regexd;
    }
}


