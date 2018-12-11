package utils.stuff;

public class Fns {

	@FunctionalInterface
	public static interface ToShortFn<A> {
		public short appy(A a);
	}

	@FunctionalInterface
	public static interface ToByteFn<A> {
		public byte appy(A a);
	}

	@FunctionalInterface
	public static interface SideEffect {
		public void send();
	}

	@FunctionalInterface
	public static interface LogFn {
		public void log(String s);
	}

	@FunctionalInterface
	public static interface TriFunction<A, B, C, D> {
		public D apply(A a, B b, C c);
	}

	@FunctionalInterface
	public static interface Int3Consumer {
		public void apply(int a, int b, int c);
	}

	public static Thread thread(SideEffect run) {
		return new Thread() {
			@Override
			public void run() {
				run.send();
			}
		};
	}
}
