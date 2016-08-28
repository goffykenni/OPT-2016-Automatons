package kenni;

public class Pair<T> {
	public final T first;
	public final T second;
	private final String stringRepresentation;
	
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
		
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(first).append(", ");
		sb.append(second).append("]");
		stringRepresentation = sb.toString();
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}
}
