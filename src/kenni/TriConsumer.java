package kenni;

public interface TriConsumer<T, U, V> {
	/**
	 * Executes a given set of statements for the three given parameters.
	 * @param t
	 * @param u
	 * @param v
	 */
	public void accept(T t, U u, V v);
}
