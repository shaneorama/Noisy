package tools.jitterbug;

import java.util.EventObject;

public class StateChangeEvent extends EventObject {
	private StateChangeType stateChangeType;

	public static enum StateChangeType {
		INITIALIZED, MODIFIED, RESET
	}

	public StateChangeEvent(Object source, StateChangeType stateChangeType) {
		super(source);
		this.stateChangeType = stateChangeType;
	}

	public StateChangeType getStateChangeType() {
		return stateChangeType;
	}
}
