/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 */
package net.sourceforge.plantuml.ugraphic.eps;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.eps.EpsGraphics;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UShape;

public class DriverPolygonEps implements UDriver<EpsGraphics> {

	private final ClipContainer clipContainer;

	public DriverPolygonEps(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, EpsGraphics eps) {
		final UPolygon shape = (UPolygon) ushape;

		final double points[] = new double[shape.getPoints().size() * 2];
		int i = 0;

		for (Point2D pt : shape.getPoints()) {
			points[i++] = pt.getX() + x;
			points[i++] = pt.getY() + y;
		}

		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			for (int j = 0; j < points.length; j += 2) {
				if (!clip.isInside(points[j], points[j + 1])) {
					return;
				}
			}
		}

		if (shape.getDeltaShadow() != 0) {
			eps.epsPolygonShadow(shape.getDeltaShadow(), points);
		}

		final HtmlColor back = param.getBackcolor();
		if (back instanceof HtmlColorGradient) {
			eps.setStrokeColor(mapper.getMappedColor(param.getColor()));
			eps.epsPolygon((HtmlColorGradient) back, mapper, points);
		} else {

			eps.setFillColor(mapper.getMappedColor(back));
			eps.setStrokeColor(mapper.getMappedColor(param.getColor()));
			eps.epsPolygon(points);
		}
	}
}
