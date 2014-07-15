package tools.jitterbug;

import java.util.EventListener;

public interface StateChangeListener extends EventListener {
	public void stateChanged(StateChangeEvent event);
}
