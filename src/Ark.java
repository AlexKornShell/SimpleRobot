public class Ark{
    private Vertex from;
    private Vertex to;
    private double capacity;

    Ark(Vertex from, Vertex to, double capacity){
        this.from = from;
        this.to = to;
        this.capacity = capacity;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public double getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Ark{" +
                "from=" + from +
                ", to=" + to +
                ", capacity=" + capacity +
                "}";
    }
}
