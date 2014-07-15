/*
 * Copyright (c) 2005-2009 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.jvnet.substance.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import javax.swing.*;
import javax.swing.JInternalFrame.JDesktopIcon;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * Tracker for pulsating (default and focused) <code>JButton</code>s. This class
 * is <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class PulseTracker implements ActionListener {
	/**
	 * Map (with weakly-referenced keys) of all trackers. For each default
	 * button which has not been claimed by GC, we have a tracker (with
	 * associated <code>Timer</code>).
	 */
	private static WeakHashMap<JButton, PulseTracker> trackers = new WeakHashMap<JButton, PulseTracker>();

	/**
	 * Map (with weakly-referenced keys) of cycle counts. For each default
	 * button which is shown <b>and </b> is in window that owns focus,
	 * <code>this</code> map contains the cycle count (for animation purposes).
	 * On each event of the associated <code>Timer</code> (see
	 * {@link #actionPerformed(ActionEvent)}), the counter is incremented by 1.
	 * For buttons that are in windows that lose focus, the counter is reverted
	 * back to 0 (animation stops).
	 */
	private static WeakHashMap<JButton, Long> cycles = new WeakHashMap<JButton, Long>();

	/**
	 * Waek reference to the associated button.
	 */
	private WeakReference<JButton> buttonRef;

	/**
	 * The associated timer.
	 */
	private Timer timer;

	/**
	 * Simple constructor.
	 * 
	 * @param jbutton
	 */
	private PulseTracker(JButton jbutton) {
		// Create weak reference.
		this.buttonRef = new WeakReference<JButton>(jbutton);
		// Create coalesced timer.
		this.timer = new Timer(50, this);
		this.timer.setCoalesce(true);
		// Store event handler and initial cycle count.
		PulseTracker.trackers.put(jbutton, this);
		PulseTracker.cycles.put(jbutton, (long) 0);
	}

	/**
	 * Recursively checks whether the specified component or one of its inner
	 * components has focus.
	 * 
	 * @param component
	 *            Component to check.
	 * @return <code>true</code> if the specified component or one of its inner
	 *         components has focus, <code>false</code> otherwise.
	 */
	private static boolean isInFocusedWindow(Component component) {
		if (component == null) {
			return false;
		}

		// check if the component itself is focus owner
		if (component.isFocusOwner()) {
			return true;
		}

		// recursively find if has focus owner component
		if (component instanceof Container) {
			for (Component comp : ((Container) component).getComponents()) {
				if (isInFocusedWindow(comp)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Recursively checks whether the specified component has visible glass
	 * pane.
	 * 
	 * @param component
	 *            Component to check.
	 * @return <code>true</code> if the specified component has visible glass
	 *         pane, <code>false</code> otherwise.
	 */
	private static boolean hasGlassPane(Component component) {
		if (component == null) {
			return false;
		}
		// check if the component has visible glass pane
		Component glassPane = null;
		if (component instanceof JDialog) {
			glassPane = ((JDialog) component).getGlassPane();
		}
		if (component instanceof JFrame) {
			glassPane = ((JFrame) component).getGlassPane();
		}
		if ((glassPane != null) && (glassPane.isVisible())) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		// get the button and check if it wasn't GC'd
		JButton jButton = this.buttonRef.get();
		if (jButton == null) {
			return;
		}
		if (PulseTracker.hasGlassPane(jButton.getTopLevelAncestor()))
			return;
		if (!isPulsating(jButton)) {
			// has since lost its default status
			PulseTracker tracker = trackers.get(jButton);
			tracker.stopTimer();
			tracker.buttonRef.clear();
			trackers.remove(jButton);
			cycles.remove(jButton);
		} else {
			if ((!PulseTracker.isInFocusedWindow(jButton.getTopLevelAncestor()))
					&& (!isAttentionDrawingCloseButton(jButton))) {
				// no focus in button window - will restore original (not
				// animated) painting
				PulseTracker.update(jButton);
			} else {
				// check if it's enabled
				if (jButton.isEnabled()) {
					// increment cycle count for pulsating buttons.
					long oldCycle = cycles.get(jButton);
					if (oldCycle == 20) {
						oldCycle = isAttentionDrawingCloseButton(jButton) ? -80
								: 0;
					}
					cycles.put(jButton, oldCycle + 1);
				} else {
					// revert to 0 if it's not enabled
					if (cycles.get(jButton) != 0) {
						cycles.put(jButton, (long) 0);
					}
				}
			}
		}
		// maybe LAF has changed
		if (SubstanceLookAndFeel.isCurrentLookAndFeel())
			jButton.repaint();
	}

	/**
	 * Starts the associated timer.
	 */
	private void startTimer() {
		if (!this.timer.isRunning()) {
			this.timer.start();
		}
	}

	/**
	 * Stops the associated timer.
	 */
	private void stopTimer() {
		if (this.timer.isRunning()) {
			this.timer.stop();
		}
	}

	/**
	 * Returns the status of the associated timer.
	 * 
	 * @return <code>true</code> is the associated timer is running,
	 *         <code>false</code> otherwise.
	 */
	private boolean isRunning() {
		return this.timer.isRunning();
	}

	/**
	 * Updates the state of the specified button which must be a default button
	 * in some window. The button state is determined based on focus ownership.
	 * 
	 * @param jButton
	 *            Button.
	 */
	public static void update(JButton jButton) {
		boolean hasFocus = PulseTracker.isInFocusedWindow(jButton
				.getTopLevelAncestor());
		PulseTracker tracker = trackers.get(jButton);
		if (!hasFocus) {
			// remove
			if (tracker == null) {
				return;
			}
			if (cycles.get(jButton) == 0) {
				return;
			}
			cycles.put(jButton, (long) 0);
			// System.out.println("r::" + trackers.size());
		} else {
			// add
			if (tracker != null) {
				tracker.startTimer();
				return;
			}
			tracker = new PulseTracker(jButton);
			tracker.startTimer();
			trackers.put(jButton, tracker);
			long initialCycle = isAttentionDrawingCloseButton(jButton) ? -80
					: 0;
			cycles.put(jButton, initialCycle);
			// System.out.println("a::" + trackers.size());
		}
	}

	/**
	 * Retrieves the current cycle count for the specified button.
	 * 
	 * @param jButton
	 *            Button.
	 * @return Current cycle count for the specified button.
	 */
	public static long getCycles(JButton jButton) {
		Long cycleCount = cycles.get(jButton);
		if (cycleCount == null) {
			return 0;
		}
		long result = cycleCount.longValue();
		if (result < 0)
			result = 0;
		return result;
	}

	/**
	 * Retrieves the animation state for the specified button.
	 * 
	 * @param jButton
	 *            Button.
	 * @return <code>true</code> if the specified button is being animated,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isAnimating(JButton jButton) {
		PulseTracker tracker = trackers.get(jButton);
		if (tracker == null) {
			return false;
		}
		return tracker.isRunning();
	}

	/**
	 * Returns memory usage.
	 * 
	 * @return Memory usage string.
	 */
	static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("PulseTracker: \n");
		sb.append("\t" + trackers.size() + " trackers, " + cycles.size()
				+ " cycles");
		return sb.toString();
	}

	/**
	 * Checks whether the specified button is attention-drawing
	 * <code>close</code> button of some internal frame, root pane or desktop
	 * icon.
	 * 
	 * @param jButton
	 *            Button.
	 * @return <code>true</code> if the specified button is <code>close</code>
	 *         button of some internal frame, root pane or desktop icon,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isAttentionDrawingCloseButton(JButton jButton) {
		if (SubstanceCoreUtilities.isTitleCloseButton(jButton)) {
			// check if have windowModified property
			Component comp = jButton;
			boolean isWindowModified = false;
			while (comp != null) {
				if (comp instanceof JInternalFrame) {
					JInternalFrame jif = (JInternalFrame) comp;
					isWindowModified = Boolean.TRUE
							.equals(jif
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				if (comp instanceof JRootPane) {
					JRootPane jrp = (JRootPane) comp;
					isWindowModified = Boolean.TRUE
							.equals(jrp
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				if (comp instanceof JDesktopIcon) {
					JDesktopIcon jdi = (JDesktopIcon) comp;
					JInternalFrame jif = jdi.getInternalFrame();
					isWindowModified = Boolean.TRUE
							.equals(jif
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				comp = comp.getParent();
			}
			if (isWindowModified) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the specified button is pulsating.
	 * 
	 * @param jButton
	 *            Button.
	 * @return <code>true</code> if the specified button is pulsating,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isPulsating(JButton jButton) {
		// default button pulsates.
		boolean isDefault = jButton.isDefaultButton();
		if (isDefault) {
			return true;
		}

		// attention-drawing close button pulsates.
		return isAttentionDrawingCloseButton(jButton);
	}

	/**
	 * Stops all timers.
	 */
	public static void stopAllTimers() {
		for (PulseTracker tracker : trackers.values()) {
			tracker.stopTimer();
		}
	}
}
