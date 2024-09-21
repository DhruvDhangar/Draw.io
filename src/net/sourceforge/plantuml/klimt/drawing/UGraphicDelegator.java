/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
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
 *
 */
package net.sourceforge.plantuml.klimt.drawing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.UShape;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.url.Url;

public abstract class UGraphicDelegator implements UGraphic {
    // ::remove file when __HAXE__

	final private UGraphic ug;

	final private XDimension2D dimension;

	@Override
	public String toString() {
		return super.toString() + " " + getUg().toString();
	}

	public final boolean matchesProperty(String propertyName) {
		return ug.matchesProperty(propertyName);
	}

	public UGraphicDelegator(UGraphic ug) {
		this.ug = ug;
		this.dimension = ug instanceof UGraphicDelegator ? ((UGraphicDelegator) ug).getDimension()
						: (ug instanceof UGraphicNo ? ((UGraphicNo) ug).getTranslate().getDimension() : null);
	}

	public UGraphicDelegator(UGraphic ug, XDimension2D dimension) {
		this.ug = ug;
		this.dimension = dimension;
	}

	public StringBounder getStringBounder() {
		return ug.getStringBounder();
	}

	public UParam getParam() {
		return ug.getParam();
	}

	public void draw(UShape shape) {
		ug.draw(shape);
	}

	public ColorMapper getColorMapper() {
		return ug.getColorMapper();
	}

	@Override
	public void startUrl(Url url) {
		ug.startUrl(url);
	}

	@Override
	public void closeUrl() {
		ug.closeUrl();
	}

	public void startGroup(Map<UGroupType, String> typeIdents) {
		ug.startGroup(typeIdents);
	}

	public void closeGroup() {
		ug.closeGroup();
	}

	protected UGraphic getUg() {
		return ug;
	}

	public void flushUg() {
		ug.flushUg();
	}

	@Override
	public HColor getDefaultBackground() {
		return ug.getDefaultBackground();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		ug.writeToStream(os, metadata, dpi);
	}

	public XDimension2D getDimension() {
		return dimension;
	}
}
