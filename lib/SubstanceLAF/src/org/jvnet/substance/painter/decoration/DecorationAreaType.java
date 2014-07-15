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
package org.jvnet.substance.painter.decoration;

/**
 * Enumeration of available decoration area types. This class is part of
 * officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 4.3
 */
public enum DecorationAreaType {
	/**
	 * Title pane of top-level windows (frames, dialogs).
	 */
	PRIMARY_TITLE_PANE,

	/**
	 * Title pane of non top-level windows (internal frames, desktop icons).
	 */
	SECONDARY_TITLE_PANE,

	/**
	 * Tool bar.
	 */
	TOOLBAR,

	/**
	 * Any area that can be placed in the top portion of its window. Menu bar is
	 * an example of a core Swing component. <code>JXHeader</code> and
	 * <code>JXTitledPanel</code> titled area (components from <a
	 * href="https://swingx.dev.java.net">SwingX</a> suite) are examples of
	 * third-party components.
	 */
	HEADER,

	/**
	 * Any area that can be placed in the bottom portion of its window.
	 * <code>JXStatusBar</code> component from <a
	 * href="https://swingx.dev.java.net">SwingX</a> suite is an example of a
	 * third-party component.
	 */
	FOOTER,

	/**
	 * Any general area that does not fit for the other types.
	 * <code>JXTaskPaneContainer</code> component from <a
	 * href="https://swingx.dev.java.net">SwingX</a> suite is an example of a
	 * third-party component.
	 */
	GENERAL,

	/**
	 * The default decoration area type. Components placed in areas with this
	 * type do not get any special background decoration painting.
	 */
	NONE
}
