import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import static java.lang.Math.*;

public class SimpleRobot {
    public static void main(String[] args) throws Exception {

        String input = "{\n" +
                "                \"name\": \"bumps\",\n" +
                "                \"dt\": 0.001,\n" +
                "                \"Fmax\": 0.1,\n" +
                "                \"circles\": [\n" +
                "                    {\"X\": 0.33218833804130554, \"Y\": 0.15921106934547424, \"R\": 0.23818166553974152},\n" +
                "                    {\"X\": 0.9211785793304443, \"Y\": 0.21001200377941132, \"R\": 0.24298787117004395},\n" +
                "                    {\"X\": 0.6558014154434204, \"Y\": 0.7025460004806519, \"R\": 0.21127113699913025},\n" +
                "                    {\"X\": 0.05513463541865349, \"Y\": 0.7919896245002747, \"R\": 0.20693418383598328}\n" +
                "                ]\n" +
                "            }";

        JSONObject object = (JSONObject) (new JSONParser()).parse(input);

        double fm = (double) object.get("Fmax");
        double dt = (double) object.get("dt");

        JSONArray pItem = (JSONArray) object.get("circles");

        ArrayList<Obstacle> obstacles = new ArrayList<>(); //Obstacle[pItem.size()];

        Iterator<JSONObject> iterator = pItem.iterator();
        while (iterator.hasNext()) {
            JSONObject jsn = iterator.next();
            obstacles.add(new Obstacle((double) jsn.get("X"), (double) jsn.get("Y"), (double) jsn.get("R")));
        }

        Obstacle obstacles1[] = new Obstacle[]{
                new Obstacle(0.5, 0.4, 0.45)
        };

        Obstacle obstacles2[] = new Obstacle[]{
                new Obstacle(0.33218833804130554, 0.15921106934547424, 0.23818166553974151),
                new Obstacle(0.9211785793304443, 0.21001200377941131, 0.24298787117004394),
                new Obstacle(0.6558014154434204, 0.7025460004806518, 0.21127113699913025),
                new Obstacle(0.05513463541865349, 0.7919896245002747, 0.20693418383598328)
        };

        Obstacle obstacles3[] = new Obstacle[]{
                new Obstacle(0.9686732292175293, 0.5127896666526794, 0.22724536061286926),
                new Obstacle(0.5128470063209534, 0.5292640328407288, 0.20050999522209167),
                new Obstacle(0.012142913416028023, 0.752185046672821, 0.3144221603870392),
                new Obstacle(0.2146744728088379, 0.06266354769468307, 0.15026962995529175),
                new Obstacle(0.709238588809967, 0.1207527220249176, 0.20471514761447906),
                new Obstacle(0.5442208647727966, 0.9810458421707153, 0.23059172928333282)
        };

        //    double dx = dt*dt*fm;
        int n1 = (int) (0.5 + sqrt((2 * 0.45) / (fm * dt * dt)));
        System.out.println("n1 " + n1);
        int n = 1001;
        if (n1 < n) n = n1;

        int[][] field = new int[n][n];

        for (int i = 0; i < n; i++) {
            field[i][0] = -1;
            field[i][n - 1] = -1;
            field[0][i] = -1;
            field[n - 1][i] = -1;
        }
        field[0][0] = 0;
        field[n - 1][n - 1] = 0;

        for (Obstacle o : obstacles) {
            double oX = (o.getX() * (n - 1));
            double oY = (o.getY() * (n - 1));
            double oR = (o.getR() * (n - 1));
            for (int i = (int) (oX - oR); i <= (int) (oX + oR) + 1; i++) {
                for (int j = (int) (oY - oR); j <= (int) (oY + oR) + 1; j++) {
                    if ((i - oX) * (i - oX) + (j - oY) * (j - oY) <= oR * oR && (i < n) && (j < n) && (i > -1) && (j > -1)) {
                        field[i][j] = -1;
                    }
                }
            }
        }

        Graph graph = new Graph(field);
        ArrayList<Point> points = graph.dijkstra(0, 0, field.length - 1, field[0].length - 1);

        FileWriter nFile = new FileWriter("file1.txt");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (field[i][j] == -1) nFile.write(("1 "));
                else nFile.write((field[i][j] + " "));
            }
            nFile.write("\n");
        }
        nFile.close();

        System.out.println("Path " + points.size());

    /*    ArrayList<Point> lined = lining(points);
        for (Point point : lined) {
            System.out.println(point.getX() + ", " + point.getY());
        //    System.out.println(round(1 + point.getX()*(n - 1)) + " " + round(1 + point.getY()*(n - 1)));
        }
        System.out.println();
        System.out.println("Lined " + lined.size());
    */
        //    ArrayList<Point> discarded = discarding(lined, obstacles);
        ArrayList<Point> discarded = discarding(points, obstacles);
        System.out.println();
        System.out.println("Discarded " + discarded.size());

        for (Point point : discarded) {
            System.out.println(point.getX() + ", " + point.getY());
            //    System.out.println(round(1 + point.getX()*(n - 1)) + " " + round(1 + point.getY()*(n - 1)));
        }

        ArrayList<Point> forces = forcing(discarded, fm, dt);
        System.out.println();
        System.out.println("Forces " + forces.size());
    /*    for (Point point : forces) {
            System.out.println(point.getX() + ", " + point.getY());
            //    System.out.println(round(1 + point.getX()*(n - 1)) + " " + round(1 + point.getY()*(n - 1)));
        }
    */

        FileWriter nFile1 = new FileWriter("file2.txt");
        for (Point point : forces) {
            nFile1.write(point.getX() + ", " + point.getY() + "\n");
        }
        nFile1.close();

    }

    /*public static ArrayList<Point> lining(ArrayList<Point> points) {
        ArrayList<Point> lined = new ArrayList<>();

        Point first = points.get(0);
        Point second = points.get(1);
        lined.add(first);

        for (int i = 2; i < points.size(); i++) {
        //    System.out.println(first + " " + second + " " + points.get(i));
        //    System.out.println(points.get(i).lines(first, second));
            if (!points.get(i).lines(first, second)) {
                lined.add(second);
                first = second;
            }
            second = points.get(i);
        }
        lined.add(second);

        return lined;
    }*/

    public static ArrayList<Point> discarding(ArrayList<Point> lined, ArrayList<Obstacle> obstacles) {
        ArrayList<Point> discarded = new ArrayList<>();
        ArrayList<Point> discardedr = new ArrayList<>();
        double a, b, c, k, d, x1, x2, y1, y2;
        int p;

        Point first = lined.get(0);
        Point second = lined.get(1);
        Point last = lined.get(lined.size() - 1);
        Point prelast = lined.get(lined.size() - 2);

        discarded.add(first);
        discardedr.add(last);

        for (int i = 2; i < lined.size() / 2; i++) {
            p = 0;
            for (Obstacle o : obstacles) {
                if (!(max(first.getX(), lined.get(i).getX()) < o.getX() - o.getR() || max(first.getY(), lined.get(i).getY()) < o.getY() - o.getR() || min(first.getX(), lined.get(i).getX()) > o.getX() + o.getR() || min(first.getY(), lined.get(i).getY()) > o.getY() + o.getR())) {
                    a = first.getY() - lined.get(i).getY();
                    b = lined.get(i).getX() - first.getX();
                    c = first.getX() * lined.get(i).getY() - lined.get(i).getX() * first.getY();
                    if (b != 0) {
                        k = -a / b;
                        b = -c / b;
                    } else {
                        k = -b / a;
                        b = -c / a;
                    }
                    d = (2 * k * b - 2 * o.getX() - 2 * o.getY() * k) * (2 * k * b - 2 * o.getX() - 2 * o.getY() * k) - (4 + 4 * k * k) * (b * b - o.getR() * o.getR() + o.getX() * o.getX() + o.getY() * o.getY() - 2 * o.getY() * b);
                    if (d >= 0) {
                        x1 = ((-(2 * k * b - 2 * o.getX() - 2 * o.getY() * k) - sqrt(d)) / (2 + 2 * k * k));
                        x2 = ((-(2 * k * b - 2 * o.getX() - 2 * o.getY() * k) + sqrt(d)) / (2 + 2 * k * k));
                        if (x1 >= min(first.getX(), lined.get(i).getX()) && x1 <= max(first.getX(), lined.get(i).getX()) || x2 >= min(first.getX(), lined.get(i).getX()) && x1 <= max(first.getX(), lined.get(i).getX())) { // Полная проверка, можно оптимизировать!
                            System.out.println("triggered " + first.getX() + " " + first.getY() + " " + lined.get(i).getX() + " " + lined.get(i).getY() + " in " + x1 + " " + x2);
                            p++;
                            break;
                        }
                    }
                }
            }
            if (p != 0) {
                discarded.add(second);
                first = second;
            }
            second = lined.get(i);
        }
        discarded.add(second);

        //    if (lined.size() % 2 == 1) discarded.add(lined.get(lined.size() / 2 + 1));
        Collections.reverse(discardedr);
        discarded.addAll(discardedr);

        return discarded;
    }

    public static ArrayList<Point> forcing(ArrayList<Point> lined, double fm, double dt) {
        ArrayList<Point> tempF, forces = new ArrayList<>();
        double x0, x1, y0, y1, x, y, vx, fx, vy, fy, sgnX, sgnY;
        double fd = fm / sqrt(2);

        for (int i = 0; i < lined.size() - 1; i++) {
            x1 = lined.get(i + 1).getX();
            x0 = lined.get(i).getX();
            y1 = lined.get(i + 1).getY();
            y0 = lined.get(i).getY();
            sgnX = (x1 - x0) / abs(x1 - x0);
            sgnY = (y1 - y0) / abs(y1 - y0);
            x = x0;
            y = y0;
            vx = 0;
            vy = 0;
            fx = 0;
            fy = 0;
            tempF = new ArrayList<>();
            int m;

            if (x1 - x0 == y1 - y0) {
                m = (int) (0.5 + sqrt((x1 - x0) / (fd * dt * dt)));

                for (int k = 0; k < m; k++) {
                    if (abs((sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)) < fd) {
                        //    System.out.println((sgnX * (x1 - x0) / (dt * dt) - (m - k + 2) * (m - k + 1) * fm) / ((k + 1) * k));  <- Тут ошибка (смотреть черновик)
                        //    System.out.println((sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt));
                        //    System.out.println(k + " " + (m - k));
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point(sgnX * fd, sgnY * fd));
                        }
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), (sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        forces.add(new Point(0, 0));
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((-1) * (sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), (-1) * (sgnX * (x1 - x0) - (m - k) * fd * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point((-1) * sgnX * fd, (-1) * sgnY * fd));
                        }
                        break;
                    }
                }
            } else if (y1 - y0 == 0) {
                m = (int) (0.5 + sqrt(sgnX * (x1 - x0) / (fm * dt * dt)));

                for (int k = 0; k < m; k++) {
                    if (abs((sgnX * (x1 - x0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)) < fm) {
                        //    System.out.println((sgnX * (x1 - x0) / (dt * dt) - (m - k + 2) * (m - k + 1) * fm) / ((k + 1) * k));  <- Тут ошибка (смотреть черновик)
                        //    System.out.println((sgnX * (x1 - x0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt));
                        //    System.out.println(k + " " + (m - k));
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point(sgnX * fm, 0));
                        }
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((sgnX * (x1 - x0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), 0));
                        }
                        forces.add(new Point(0, 0));
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((-1) * (sgnX * (x1 - x0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), 0));
                        }
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point((-1) * sgnX * fm, 0));
                        }
                        break;
                    }
                }
            } else if (x1 - x0 == 0) {
                m = (int) (0.5 + sqrt(sgnY * (y1 - y0) / (fm * dt * dt)));

                //    System.out.println("here: " + i + " m=" + m + " y1=" + y1 + " y0=" + y0 + " x1=" + x1 + " x0=" + x0);

                for (int k = 0; k < m; k++) {
                    if (abs((sgnY * (y1 - y0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)) < fm) {
                        //    System.out.println((sgnX * (x1 - x0) / (dt * dt) - (m - k + 2) * (m - k + 1) * fm) / ((k + 1) * k));  <- Тут ошибка (смотреть черновик)
                        //    System.out.println((sgnY * (y1 - y0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt));
                        //    System.out.println(k + " " + (m - k));
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point(0, sgnY * fm));
                        }
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point(0, (sgnY * (y1 - y0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        forces.add(new Point(0, 0));
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point(0, (-1) * (sgnY * (y1 - y0) - (m - k) * fm * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point(0, (-1) * sgnY * fm));
                        }

                        double xt = x0;
                        double vxt = 0;
                        double yt = y0;
                        double vyt = 0;
                        for (int t = 0; t < m - k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += sgnX * fx * dt;
                            vyt += sgnY * fy * dt;
                        }
                        for (int t = 0; t < k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                            vyt += (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                        }
                        xt += vxt * dt;
                        yt += vyt * dt;
                        for (int t = 0; t < k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (-1) * (sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                            vyt += (-1) * (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                        }
                        for (int t = 0; t < m - k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (-1) * sgnX * fx * dt;
                            vyt += (-1) * sgnY * fy * dt;
                        }
                        //    System.out.println("xt=" + xt + " x1=" + x1 + " yt=" + yt + " y=" + y1 + " vxt=" + vxt + " vyt=" + vyt);


                        break;
                    }
                }
            } else {
                fx = fm / sqrt(1 + ((y1 - y0) * (y1 - y0)) / ((x1 - x0) * (x1 - x0)));
                fy = fm / sqrt(1 + ((x1 - x0) * (x1 - x0)) / ((y1 - y0) * (y1 - y0)));

                int mx = (int) (0.5 + sqrt(sgnX * (x1 - x0) / (fx * dt * dt)));
                int my = (int) (0.5 + sqrt(sgnY * (y1 - y0) / (fy * dt * dt)));
                m = (int) (0.5 + sqrt(sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)) / (fm * dt * dt)));
                //    System.out.println(i + " fx=" + fx + " fy=" + fy + " mx=" + mx + " my=" + my + " m=" + m);

                if (sqrt(fx * fx + fy * fy) > fm) {
                    double df = sqrt(fx * fx + fy * fy) - fm;
                    System.out.println("df=" + df);
                }

                for (int k = 0; k < m; k++) {
                    if (abs((sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)) < fx && abs((sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)) < fy) {
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point(sgnX * fx, sgnY * fy));
                        }
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        forces.add(new Point(0, 0));
                        for (int j = 0; j < k; j++) {
                            forces.add(new Point((-1) * (sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt), (-1) * (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt * dt)));
                        }
                        for (int j = 0; j < m - k; j++) {
                            forces.add(new Point((-1) * sgnX * fx, (-1) * sgnY * fy));
                        }

                        double xt = x0;
                        double vxt = 0;
                        double yt = y0;
                        double vyt = 0;
                        for (int t = 0; t < m - k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += sgnX * fx * dt;
                            vyt += sgnY * fy * dt;
                        }
                        for (int t = 0; t < k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                            vyt += (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                        }
                        xt += vxt * dt;
                        yt += vyt * dt;
                        for (int t = 0; t < k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (-1) * (sgnX * (x1 - x0) - (m - k) * fx * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                            vyt += (-1) * (sgnY * (y1 - y0) - (m - k) * fy * dt * dt * (m + 1 + k)) / ((k + 1) * k * dt);
                        }
                        for (int t = 0; t < m - k; t++) {
                            xt += vxt * dt;
                            yt += vyt * dt;
                            vxt += (-1) * sgnX * fx * dt;
                            vyt += (-1) * sgnY * fy * dt;
                        }
                        //    System.out.println("xt=" + xt + " x1=" + x1 + " yt=" + yt + " y=" + y1 + " vxt=" + vxt + " vyt=" + vyt);
                        if (sqrt(fx * fx + fy * fy) > fm)
                            System.out.println("f=" + sqrt(fx * fx + fy * fy) + " fm=" + fm);
                        break;
                    }
                }


            }


        /*
            while (x < x1 || y < y1) {
                x = x + vx*dt;
                vx = vx + fx*dt;
                y = y + vy*dt;
                vy = vy + fy*dt;
                Point force = new Point(0,0);

                if (y0 == y1) {
                        if (abs((x1 + x0) / 2 - (x + 2 * vx * dt + fm * dt * dt)) > abs((vx + fm * dt) * dt)) {
                    //    if (abs(((x1 - x0) / 2 - x - (k + 1) * vx * dt) / ((k + 1) * k * dt * dt / 2)) < fm) {
                            fx = sgnX * fm;
                            force.setX(fx);
                            tempF.add(force);
                        } else {
                            fx = ((x1 + x0) / 2 - x - (2 + 1) * vx * dt) / ((2 + 1) * 2 * dt * dt / 2);
                            force.setX(fx);
                            tempF.add(force);
                            tempF.add(force);

                            forces.addAll(tempF);
                            forces.add(new Point(0, 0));
                            Collections.reverse(tempF);
                            for (int j = 0; j < tempF.size(); j++) {
                                forces.add(new Point((-1) * tempF.get(j).getX(), 0));
                            }
                            // Подсчёт
                            x = 2 * (x + vx * dt + (vx + fx * dt) * dt + (vx + 2 * fx * dt) * dt);
                            break;
                        }
                } else if (x0 == x1) {
                    if (abs((y1 + y0) / 2 - (y + 2 * vy * dt + fm * dt * dt)) > abs((vy + fm * dt) * dt)) {
                        fy = sgnY * fm;
                        force.setY(fy);
                        tempF.add(force);
                    } else {
                        fy = ((y1 + y0) / 2 - y - 3 * vy * dt) / (3 * dt * dt);
                        force.setY(fy);
                        tempF.add(force);
                        tempF.add(force);

                        forces.addAll(tempF);
                        forces.add(new Point (0,0));
                        Collections.reverse(tempF);
                        for (int j = 0; j < tempF.size(); j++) {
                            forces.add(new Point(0, (-1) * tempF.get(j).getX()));
                        }
                        // Подсчёт
                        y = 2 * (y + vy * dt + (vy + fy * dt) * dt + (vy + 2 * fy * dt) * dt);
                        break;
                    }
                } else {
                    if (abs((x1 + x0) / 2 - (x + 2 * vx * dt + fd * dt * dt)) > abs((vx + fd * dt) * dt)) {
                        fx = sgnX * fd;
                        force.setX(fx);
                        fy = sgnY * fd;
                        force.setY(fy);
                        tempF.add(force);
                    } else {
                        fx = ((x1 + x0) / 2 - x - 3 * vx * dt) / (3 * dt * dt);
                        force.setX(fx);
                        fy = ((y1 + y0) / 2 - y - 3 * vy * dt) / (3 * dt * dt);
                        force.setY(fy);
                        tempF.add(force);
                        tempF.add(force);

                        forces.addAll(tempF);
                        forces.add(new Point (0,0));
                        Collections.reverse(tempF);
                        for (int j = 0; j < tempF.size(); j++) {
                            forces.add(new Point((-1) * tempF.get(j).getX(), (-1) * tempF.get(j).getY()));
                        }
                        // Подсчёт
                        x = 2 * (x + vx * dt + (vx + fx * dt) * dt + (vx + 2 * fx * dt) * dt);
                        y = 2 * (y + vy * dt + (vy + fy * dt) * dt + (vy + 2 * fy * dt) * dt);
                        break;
                    }
            /*        if (abs((y1 - y0) / 2 - (y + 2 * vy * dt + fd * dt * dt)) < abs((vy + fd * dt) * dt)) {
                        fy = sgnY * fd;
                        force.setY(fy);
                    } else {

                    }
            *//*    }

            }

            if (x != x1 && y != y1) System.out.println("Error in line " + i + " x=" + x + ", x1=" + x1 + " y=" + y + ", y1=" + y1);
        */
        }


        return forces;
    }

}
