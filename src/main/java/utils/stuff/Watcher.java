package utils.stuff;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Wrap watchers to allow them to use lambdas effectively. 
 * This is grim, but considering that Watchers are from 1.7 they are rubbish */
public class Watcher extends Thread {
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

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

			while (!Thread.interrupted()) try {
				WatchKey poll = watcher.take();
				Thread.sleep(1000); //give the file a chance to finish saving
				
				poll.pollEvents().stream()
				.filter(e->e.kind()!=OVERFLOW)
				.map(e->((WatchEvent<Path>)e).context())
				.filter(matches)
				.forEach(action);
				
				/* Must reset the key to receive further watch events.
				 * This won't play nice if the directory is deleted, so, um,
				 * don't, I guess */
				key.reset();
			} catch (InterruptedException e) {}
		} catch (IOException e) { throw new Error(e); } //TODO
	}
}