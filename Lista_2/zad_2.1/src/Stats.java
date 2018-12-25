public class Stats {
    private int all=3;
    private int prus=1;

    public int getAll() {
        return all;
    }

    public int getPrus() {
        return prus;
    }

    public int getOrzeszkowa() {
        return orzeszkowa;
    }

    public int getSienkiewicz() {
        return sienkiewicz;
    }

    private int orzeszkowa=1;
    private int sienkiewicz=1;
    public void increment(Author author){
        switch (author){
            case PRUS:
                prus++;
                break;
            case ORZESZKOWA:
                orzeszkowa++;
                break;
            case SIENKIEWICZ:
                sienkiewicz++;
                break;
        }
        all++;
    }
}
