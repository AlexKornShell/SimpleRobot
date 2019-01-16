import java.util.ArrayList;

public class Vertex {
    private int number; // Номер вершины
    private int x; // Координата x вершины
    private int y; // Координата y вершины
    private ArrayList<Ark> list; // Список смежности
    private double distance; // Расстояние от начальной вершины
    private boolean mark; // Маркировка посещённой вершины
    private Vertex prev;

    Vertex(int number, int x, int y){
        this.number = number;
        this.x = x;
        this.y = y;
        this.list = new ArrayList<>();
    }

    public void addArk(Ark a){
        list.add(a);
    }

    public int getNumber() {
        return number;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<Ark> getList() {
        return list;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isMark() {
        return mark;
    }

    public Vertex getPrev() {
        return prev;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "{" + number + " " + x + " " + y + "}";
    }

}
