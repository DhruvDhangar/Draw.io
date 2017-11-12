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
 *
 */
package net.sourceforge.plantuml.classdiagram;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.svek.image.EntityImageClass;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;

public class ClassDiagram extends AbstractClassOrObjectDiagram {

	@Override
	public ILeaf getOrCreateLeaf(Code code, LeafType type, USymbol symbol) {
		if (getNamespaceSeparator() != null) {
			code = code.withSeparator(getNamespaceSeparator());
		}
		if (type == null) {
			code = code.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
			if (getNamespaceSeparator() == null) {
				return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
			}
			code = code.getFullyQualifiedCode(getCurrentGroup());
			if (super.leafExist(code)) {
				return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
			}
			return createEntityWithNamespace(code, Display.getWithNewlines(code.getShortName(getLeafs())),
					LeafType.CLASS, symbol);
		}
		if (getNamespaceSeparator() == null) {
			return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
		}
		code = code.getFullyQualifiedCode(getCurrentGroup());
		if (super.leafExist(code)) {
			return getOrCreateLeafDefault(code, type, symbol);
		}
		return createEntityWithNamespace(code, Display.getWithNewlines(code.getShortName(getLeafs())), type, symbol);
	}


	@Override
	public ILeaf createLeaf(Code code, Display display, LeafType type, USymbol symbol) {
		if (getNamespaceSeparator() != null) {
			code = code.withSeparator(getNamespaceSeparator());
		}
		if (type != LeafType.ABSTRACT_CLASS && type != LeafType.ANNOTATION && type != LeafType.CLASS
				&& type != LeafType.INTERFACE && type != LeafType.ENUM && type != LeafType.LOLLIPOP
				&& type != LeafType.NOTE) {
			return super.createLeaf(code, display, type, symbol);
		}
		if (getNamespaceSeparator() == null) {
			return super.createLeaf(code, display, type, symbol);
		}
		code = code.getFullyQualifiedCode(getCurrentGroup());
		if (super.leafExist(code)) {
			throw new IllegalArgumentException("Already known: " + code);
		}
		return createEntityWithNamespace(code, display, type, symbol);
	}

	private ILeaf createEntityWithNamespace(Code fullyCode, Display display, LeafType type, USymbol symbol) {
		IGroup group = getCurrentGroup();
		final String namespace = getNamespace(fullyCode);
		if (namespace != null
				&& (EntityUtils.groupRoot(group) || !group.getCode().getFullName().equals(namespace))) {
			final Code namespace2 = Code.of(namespace);
			group = getOrCreateNamespaceInternal(namespace2, Display.getWithNewlines(namespace), GroupType.PACKAGE,
					getRootGroup());
		}
		return createLeafInternal(
				fullyCode,
				Display.isNull(display) ? Display.getWithNewlines(fullyCode.getShortName(getLeafs())).withCreoleMode(
						CreoleMode.SIMPLE_LINE) : display, type, group, symbol);
	}

	private final String getNamespace(Code fullyCode) {
		String name = fullyCode.getFullName();
		do {
			final int x = name.lastIndexOf(getNamespaceSeparator());
			if (x == -1) {
				return null;
			}
			name = name.substring(0, x);
		} while (getLeafs().containsKey(Code.of(name, getNamespaceSeparator())));
		return name;
	}

	@Override
	public final boolean leafExist(Code code) {
		if (getNamespaceSeparator() == null) {
			return super.leafExist(code);
		}
		code = code.withSeparator(getNamespaceSeparator());
		return super.leafExist(code.getFullyQualifiedCode(getCurrentGroup()));
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.CLASS;
	}

	private boolean allowMixing;

	public void setAllowMixing(boolean allowMixing) {
		this.allowMixing = allowMixing;
	}

	public boolean isAllowMixing() {
		return allowMixing;
	}

	private int useLayoutExplicit = 0;

	public void layoutNewLine() {
		useLayoutExplicit++;
		incRawLayout();
	}

	@Override
    protected final ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		if (useLayoutExplicit != 0) {
			return exportLayoutExplicit(os, index, fileFormatOption);
		}
		return super.exportDiagramInternal(os, index, fileFormatOption);
	}

	protected final ImageData exportLayoutExplicit(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final FullLayout fullLayout = new FullLayout();
		for (int i = 0; i <= useLayoutExplicit; i++) {
			final RowLayout rawLayout = getRawLayout(i);
			fullLayout.addRowLayout(rawLayout);
		}
		final ImageBuilder imageBuilder = new ImageBuilder(getSkinParam(), 1, null, null, 0, 10, null);
		imageBuilder.setUDrawable(fullLayout);
		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, seed(), os);
	}

	private RowLayout getRawLayout(int raw) {
		final RowLayout rawLayout = new RowLayout();
		for (ILeaf leaf : getLeafs().values()) {
			if (leaf.getRawLayout() == raw) {
				rawLayout.addLeaf(getEntityImageClass(leaf));
			}
		}
		return rawLayout;
	}

	private TextBlock getEntityImageClass(ILeaf entity) {
		return new EntityImageClass(null, entity, getSkinParam(), this);
	}

	@Override
	public String checkFinalError() {
		for (Link link : this.getLinks()) {
			final int len = link.getLength();
			if (len == 1) {
				for (Link link2 : this.getLinks()) {
					if (link2.sameConnections(link) && link2.getLength() != 1) {
						link2.setLength(1);
					}
				}
			}
		}
		this.applySingleStrategy();
		return super.checkFinalError();
	}

}
