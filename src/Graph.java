import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.zip.DataFormatException;

import static java.lang.Math.sqrt;

public class Graph {

    private ArrayList<Vertex> graph;
    private int [][] field;
    private int [][] mm;

    Graph(int m[][]) {
        this.mm = m;
        this.graph = new ArrayList<>();
        this.field = new int[mm.length][mm[0].length];

        for (int i = 0; i < this.field.length; i++) {
            for (int j = 0; j < this.field[i].length; j++) {
                this.field[i][j] = mm[i][j];
            }
        }

        // Заполнение графа вершинами, в соответствии с лабиринтом
        int k = 0;
        for (int i = 0; i < this.field.length; i++){
            for (int j = 0; j < this.field[i].length; j++){
                if (this.field[i][j]==0) {
                    graph.add(new Vertex(k, i, j));
                    this.field[i][j] = k;
                    k++;
                }
            }
        }

        // Проверка соседних ячеек лабиринта
        for (int i = 0; i < this.field.length; i++) {
            for (int j = 0; j < this.field[i].length; j++) {
                if (this.field[i][j]!= -1){
                    if (i-1 > 0 && this.field[i-1][j] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i-1][j]),1));
                    }
                    if (i+1 < this.field.length - 1 && this.field[i+1][j] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i+1][j]),1));
                    }
                    if (j-1 > 0 && this.field[i][j-1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i][j-1]),1));
                    }
                    if (j+1 < this.field[i].length - 1 && this.field[i][j+1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i][j+1]),1));
                    }

                    if (i-1 >= 0 && j-1 >= 0 && this.field[i-1][j-1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i-1][j-1]),sqrt(2)));
                    }
                    if (i-1 >=0 && j+1 < this.field[i].length && this.field[i-1][j+1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i-1][j+1]),sqrt(2)));
                    }
                    if (i+1 < this.field.length && j-1 >= 0 && this.field[i+1][j-1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i+1][j-1]),sqrt(2)));
                    }
                    if (i+1 < this.field.length && j+1 < this.field[i].length && this.field[i+1][j+1] != -1) {
                        graph.get(this.field[i][j]).addArk(new Ark(graph.get(this.field[i][j]),graph.get(this.field[i+1][j+1]),sqrt(2)));
                    }
                }

            }
        }

    }

    // Алгоритм Дейкстры поиска минимального расстояния
    public ArrayList<Point> dijkstra(int x1, int y1, int x2, int y2) throws Exception {
        ArrayList<Point> result = new ArrayList<>();
        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0 && x1 < this.field.length && y1 < this.field[x1].length && x2 < this.field.length && y2 < this.field[x2].length) {
            int a = this.field[x1][y1];
            int b = this.field[x2][y2];

            // Проверка на корректность введённых параметров
            if (a == -1 || b == -1 || graph.get(a).getList().isEmpty() || graph.get(b).getList().isEmpty()) {
                throw new Exception();
            } else {

                PriorityQueue<Ark> qu = new PriorityQueue(new Comparator<Ark>() {
                    @Override
                    public int compare(Ark a1, Ark a2) {
                        double g = a1.getFrom().getDistance() + a1.getCapacity() - a2.getFrom().getDistance() - a2.getCapacity();
                        double h = sqrt((a1.getTo().getX() - graph.get(b).getX())*(a1.getTo().getX() - graph.get(b).getX()) +
                                        (a1.getTo().getY() - graph.get(b).getY())*(a1.getTo().getY() - graph.get(b).getY())) -
                                    sqrt((a2.getTo().getX() - graph.get(b).getX())*(a2.getTo().getX() - graph.get(b).getX()) +
                                         (a2.getTo().getY() - graph.get(b).getY())*(a2.getTo().getY() - graph.get(b).getY()));
                        if (g + h < 0) return -1;
                        else if (g + h > 0) return 1;
                        else return 0;
                    }
                });

                // Инициализация бесконечной начальной дистанции. Очевидно, что в лабиринте
                // максимальный путь будет хотя бы на единицу меньше размера лабиринта.
                // Поэтому бесконечной начальной дистанцией принимается размер лабиринта.
                for (Vertex v : graph) {
                    v.setDistance(graph.size());
                    for (Ark t : v.getList()) {
                        t.getTo().setDistance(graph.size());
                    }
                    v.setMark(false);
                }

                // А начальная вершина имеет дистанцию 0.
                graph.get(a).setDistance(0);

                // Заполнение очереди с приоритетом рёбрами, инцидентные начальной вершине.
                for (Ark t : graph.get(a).getList()) {
                    if (!t.getTo().isMark()) qu.offer(t);
                }
                graph.get(a).setMark(true);

                Ark t;

                // Пока очередь не пуста, вынимаем из неё ребро, изменяем дистанцию
                // до смежной непосещённой вершины, и добавляем в очередь смежные для этой вершины рёбра,
                // если эти рёбра были не пройдены.
                while (!qu.isEmpty()) { // && graph.get(b).getDistance() == graph.size()
                    t = qu.poll();
                 //   System.out.println(t);
                    if (t.getTo().getDistance() > t.getFrom().getDistance() + t.getCapacity()) {
                        t.getTo().setDistance(t.getFrom().getDistance() + t.getCapacity());
                        t.getTo().setPrev(t.getFrom());
                        for (Ark k : t.getTo().getList()) {
                            if (!k.getTo().isMark()) qu.offer(k);
                        }
                    }

                    t.getTo().setMark(true);
                    if (t.getTo().getNumber() == b) break;
                }

                Vertex v = graph.get(b);
                while (v.getNumber() != a) {
                //    System.out.println(v.getNumber() + " " + v.getX() + " " + v.getY());
                    mm[v.getX()][v.getY()] = 2;
                    result.add(new Point(v.getX()/(double)(mm.length - 1), v.getY()/(double)(mm.length - 1)));
                    v = v.getPrev();
                }
            //    System.out.println(v.getNumber() + " " + v.getX() + " " + v.getY());
                mm[v.getX()][v.getY()] = 2;
                result.add(new Point(v.getX()/(double)(mm.length - 1), v.getY()/(double)(mm.length - 1)));
                System.out.println("Distance " + graph.get(b).getDistance());

                Collections.reverse(result);
                return result;
            }
        } else throw new Exception();
    }
}
