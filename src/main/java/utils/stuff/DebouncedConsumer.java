package utils.stuff;

import static java.lang.System.currentTimeMillis;

import java.util.function.Consumer;

public class DebouncedConsumer<T> {
	private final long interval;
	private final Consumer<T> action;
	private long lastAlerted = 0;
	
	public DebouncedConsumer(long interval, Consumer<T> action) {
		this.interval = interval;
		this.action = action;
	}

	public void accept(T message) {
		if (lastAlerted < currentTimeMillis() - interval) {
			lastAlerted = currentTimeMillis();
			action.accept(message);
		}
	}
}