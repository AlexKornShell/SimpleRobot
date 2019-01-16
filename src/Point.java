public class Point {
    private double x;
    private double y;

    Point (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void minusX() {
        this.x = (-1)*this.x;
    }

    public void minusY() {
        this.y = (-1)*this.y;
    }

    @Override
    public String toString() {
        return "{" + x + " " + y + "}";
    }

    public boolean lines(Point first, Point second) {
        if ((second.x - first.x) == (this.x - second.x) && (second.y - first.y) == (this.y - second.y)) return true;
        else if ((second.x - first.x) == (second.y - first.y) && (this.x - second.x) == (this.y - second.y)) return true;
        else if ((second.x - first.x) == -(second.y - first.y) && (this.x - second.x) == -(this.y - second.y)) return true;
        else if ((second.x - first.x) * (this.y - first.y) - (second.y - first.y) * (this.x - first.x) == 0) return true;
        else return false;
    }
}
