package eu.tomylobo.abstraction.spout.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spout.api.event.Event;
import org.spout.api.event.EventExecutor;
import org.spout.api.exception.EventException;

public class SpoutSimpleEventExecutor implements EventExecutor {
	private final Object listener;
	private final Method handler;

	public SpoutSimpleEventExecutor(Object listener, Method handler) {
		this.listener = listener;
		this.handler = handler;
	}

	@Override
	public void execute(Event backend) throws EventException {
		try {
			handler.invoke(listener,  backend);
		} catch (IllegalAccessException e) {
			throw new EventException("Could not access handler", e);
		} catch (InvocationTargetException e) {
			throw new EventException("Exception caught from handler", e.getCause());
		}
	}
}
