package data.dataInstance;

import java.util.Iterator;

public class NeighbourhoodIterator<Edge> implements Iterator<Edge> {
    private final Iterator<Edge> is[];
    private int current;

    public NeighbourhoodIterator(Iterator<Edge>... iterators)
    {
            is = iterators;
            current = 0;
    }

    public boolean hasNext() {
            while ( current < is.length && !is[current].hasNext() )
                    current++;

            return current < is.length;
    }

    public Edge next() {
            while ( current < is.length && !is[current].hasNext() )
                    current++;

            return is[current].next();
    }

    public void remove() { /* not implemented */ }
}