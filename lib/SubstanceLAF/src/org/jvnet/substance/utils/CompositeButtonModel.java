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

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

/**
 * Composite button model that tracks changes to one primary and any number of
 * secondary button models for composite rollover effects. This model can be
 * used to "simulate" rollover effects on the primary component when the actual
 * rollover happens on one of the secondary components. An example is a scroll
 * bar. When the mouse enters one of the scroll buttons, the scroll track is
 * highlighted as well.
 * 
 * @author Kirill Grouchnikov
 */
public class CompositeButtonModel extends DefaultButtonModel {
	/**
	 * The primary model.
	 */
	protected ButtonModel primaryModel;

	/**
	 * The secondary models.
	 */
	protected ButtonModel[] secondaryModels;

	/**
	 * Creates a new composite button model.
	 * 
	 * @param primaryModel
	 *            The primary model.
	 * @param secondaryModels
	 *            The secondary models.
	 */
	public CompositeButtonModel(ButtonModel primaryModel,
			ButtonModel... secondaryModels) {
		this.primaryModel = primaryModel;
		this.secondaryModels = secondaryModels;
	}

	/**
	 * Creates a new composite button model.
	 * 
	 * @param primaryModel
	 *            The primary model.
	 * @param secondaryButtons
	 *            The secondary buttons.
	 */
	public CompositeButtonModel(ButtonModel primaryModel,
			AbstractButton... secondaryButtons) {
		this.primaryModel = primaryModel;
		List<ButtonModel> bmList = new LinkedList<ButtonModel>();
		for (AbstractButton secondary : secondaryButtons) {
			if (secondary != null) {
				bmList.add(secondary.getModel());
			}
		}
		this.secondaryModels = bmList.toArray(new ButtonModel[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.DefaultButtonModel#isRollover()
	 */
	@Override
	public boolean isRollover() {
		if ((primaryModel != null) && primaryModel.isRollover())
			return true;
		for (ButtonModel secondary : this.secondaryModels) {
			if ((secondary != null) && secondary.isRollover())
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.DefaultButtonModel#isArmed()
	 */
	public boolean isArmed() {
		return (primaryModel != null) && primaryModel.isArmed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.DefaultButtonModel#isEnabled()
	 */
	public boolean isEnabled() {
		return (primaryModel != null) && primaryModel.isEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.DefaultButtonModel#isPressed()
	 */
	public boolean isPressed() {
		return (primaryModel != null) && primaryModel.isPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.DefaultButtonModel#isSelected()
	 */
	public boolean isSelected() {
		return (primaryModel != null) && primaryModel.isSelected();
	}
}
