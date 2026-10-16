package main;

public class Edge {
    public Point3D start;
    public Point3D end;
    public Edge(Point3D start, Point3D end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int hashCode() {
        long h1 = (long) start.hashCode() * 0x9e3779b97f4a7c15L;
        long h2 = (long) end.hashCode() * 0x9e3779b97f4a7c15L;

        long combined = h1 + h2;

        combined ^= (combined >>> 33);
        combined *= 0xff51afd7ed558ccdL;
        combined ^= (combined >>> 33);
        combined *= 0xc4ceb9fe1a85ec53L;
        combined ^= (combined >>> 33);

        return (int) combined;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Edge other = (Edge) obj;
        return (start.equals(other.start) && end.equals(other.end)) ||
               (start.equals(other.end) && end.equals(other.start));
    }
}
