package nz.co.stqry.stqrylibrary.list;

import java.util.Collection;

/**
 * Created by Marc Giovannoni on 7/07/14.
 */
public class ListUtils {

    public static <T, E> T find(Collection<T> c, E toCompare, IComparator<E, T> comparator) {
        for (T o : c) {
            if (comparator.compare(toCompare, o)) {
                return o;
            }
        }
        return null;
    }
}
