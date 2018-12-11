package utils.stuff;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static utils.arrays.Objs.empty;
import static utils.arrays.Objs.forEach;
import static utils.stuff.Support.trySleep;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Wrap watchers to allow them to use lambdas effectively. 
 * This is grim, but considering that Watchers are from 1.7 they are rubbish */
public class Watcher {
	public final Path dir;
	final Consumer<Path> action;
	final Predicate<Path> matches;
	
	public Watcher(Path dir, Consumer<Path> action, Predicate<Path> matches) {
		this.dir = dir;
		this.action = action;
		this.matches = matches;
	}
	
	public static Predicate<Path> endsWith(String end) {
		return p->p.toString().endsWith(end);
	}
	
	public static Predicate<Path> equals(String end) {
		return p->p.toString().equals(end);
	}
	
	public Stream<Path> allMatching() {
		return Arrays.stream(dir.toFile().list())
			.map(Paths::get)
			.filter(matches);
	}

	public void run(ScheduledExecutorService runSes) {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

			runSes.scheduleWithFixedDelay(
				()->this.check(key), 500, 500, MILLISECONDS);
		} catch (IOException ioe) {
			throw new Error(ioe);
		}
	}
	
	private void check(WatchKey key) {
		
		@SuppressWarnings("unchecked")
		var evts = key.pollEvents().stream()
			.filter(e->e.kind()!=OVERFLOW)
			.map(e->((WatchEvent<Path>)e).context())
			.filter(matches)
			.toArray(Path[]::new);
		
		//Don't think this is required if you call WatchKey.pollEvents
		//key.reset();
		
		//give the file a chance to finish saving. Not a great solution since 
		//nothing updates in this time
		if (!empty(evts)) trySleep(500);
			 
		forEach(evts, action);
	}
}