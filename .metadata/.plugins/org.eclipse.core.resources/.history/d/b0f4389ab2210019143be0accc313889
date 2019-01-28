package scripts.entityselector.finders;

/**
 * @author Laniax
 */
public abstract class FinderResult<T> {

    /**
     * Returns an array with all the results. If no filters were set, this will return ALL the current entities we can find.
     * @return Empty if no results found, never null.
     */
    public abstract T[] getResults();

    /**
     * Returns the first index of the results, if it exists.
     * @return the first result or null if there were none.
     */
    public T getFirstResult() {

        T[] results = this.getResults();

        if (results.length > 0)
            return results[0];

        return null;
    }
}