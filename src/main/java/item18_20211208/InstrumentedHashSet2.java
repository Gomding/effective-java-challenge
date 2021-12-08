package item18_20211208;

import java.util.Collection;
import java.util.Set;

public class InstrumentedHashSet2<E> extends ForwardingSet<E> {

    private int addCount = 0;

    public InstrumentedHashSet2(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return this.addCount;
    }
}
