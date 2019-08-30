package utils.stuff;

import static java.lang.System.currentTimeMillis;
import static utils.arrays.Longs.append;
import static utils.arrays.Longs.filter;

import java.util.function.Consumer;

import utils.arrays.Longs;

public class OnNInXConsumer<T> {
	private long[] errorList = Longs.EMPTY;
	private final Consumer<T> action;
	private final int minCount;
	private final long period;

	public OnNInXConsumer(Consumer<T> action, int minCount, long period) {
		this.action = action;
		this.minCount = minCount;
		this.period = period;
	}

	public void accept(T message) {
		long now = currentTimeMillis();
		long periodStart = now - period;
		errorList = filter(errorList, l->l>=periodStart);
		errorList = append(errorList, now);
		if (errorList.length>=minCount) {
			action.accept(message);
		}
	}
}