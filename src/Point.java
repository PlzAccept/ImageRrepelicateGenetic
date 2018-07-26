
public class Point implements Comparable<Point> {
	int x, y;
	
	Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int compareTo(Point p) {
		if(this.x == p.x) return Integer.compare(this.y, p.y);
		return Integer.compare(this.x, p.x);
	}
}
