// @formatter:off
/*
 * qqwing - Sudoku solver and generator
 * Copyright (C) 2014 Stephen Ostermiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
// @formatter:on
package org.secuso.privacyfriendlysudoku.controller.qqwing;

import java.util.Locale;

public enum Symmetry {
	NONE,
	ROTATE90,
	ROTATE180,
	MIRROR,
	FLIP,
	RANDOM;

	public static Symmetry get(String s) {
		if (s == null) return null;
		try {
			s = s.toUpperCase(Locale.ENGLISH);
			return valueOf(s);
		} catch (IllegalArgumentException aix) {
			return null;
		}
	}

	public String getName() {
		String name = toString();
		return name.substring(0, 1) + name.substring(1).toLowerCase(Locale.ENGLISH);
	}
}
