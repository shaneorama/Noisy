/*
 * aTunes 1.13.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package test.issues;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * The Class SubstanceATunesLookAndFeel.
 */
public class Issue437LookAndFeel extends SubstanceLookAndFeel {

	public Issue437LookAndFeel() {
		super(new Issue437Skin());
	}

	private static final long serialVersionUID = -3907225219153995877L;

	@Override
	public String getID() {
		return "Substance aTunes Blue";
	}

	@Override
	public String getName() {
		return "Substance aTunes Blue";
	}

}
