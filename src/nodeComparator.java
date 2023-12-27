import java.util.Comparator;

class nodeComparator implements Comparator<node> {
    @Override
    public int compare(node a, node b) {
        return a.freq - b.freq;
    }
}