package nz.co.stqry.stqrylibrary.list;

/**
 * Created by Marc Giovannoni on 7/07/14.
 */
public interface IComparator<T, E>  {

    boolean compare(T a, E b);
}
