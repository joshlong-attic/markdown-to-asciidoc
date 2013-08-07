/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joshlong.markup.asciidoc;

import org.pegdown.Printer;
import org.pegdown.ast.*;

import static com.joshlong.markup.asciidoc.StringUtils.defaultString;
import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * {@link Visitor visitor} implementation that emits Asciidoc compliant markup as it <EM>visits</EM> the Markdown
 * document. At least, on theory.
 *
 * @author Josh Long
 */

public class ToAsciidocSerializer extends SimpleVisitorAdapter {

	private Printer asciidoc = new Printer();

	public String toAsciidoc(RootNode rootNode) {
		checkArgNotNull(rootNode, "rootNode");
		rootNode.accept(this);
		return asciidoc.getString();
	}

	@Override
	public void visit(CodeNode node) {
		asciidoc.print("`" + node.getText() + "`");
	}

	@Override
	public void visit(ExpLinkNode node) {
		String url = node.url;
		String asciidocLink = String.format("%s[%s]", url, this.printChildrenToString(node));
		asciidoc.print(asciidocLink);
	}

	protected String printChildrenToString(Node node) {
		Printer priorPrinter = asciidoc;
		asciidoc = new Printer();
		visitChildren(node);
		String result = asciidoc.getString();
		asciidoc = priorPrinter;
		return result;
	}

	protected void visitChildren(Node node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(HeaderNode node) {
		String equals = org.parboiled.common.StringUtils.repeat('=', node.getLevel());
		String headline = equals + ' ' + printChildrenToString(node) + ' ' + equals;
		asciidoc.print(headline).println().println();
	}

	@Override
	public void visit(ParaNode node) {
		asciidoc.print(printChildrenToString(node)).println().println();
	}

	@Override
	public void visit(RootNode node) {
		visitChildren(node);
	}

	@Override
	public void visit(SpecialTextNode node) {
		String specialText = node.getText();
		asciidoc.print(specialText);
	}

	@Override
	public void visit(StrongEmphSuperNode node) {
		String text = node.getChars();
		String body = printChildrenToString(node);
		if (text.equals("*") || text.equals("_")){
			asciidoc.print(italics(body));
		}
		else {
			asciidoc.print(bold(body));
		}
	}

	@Override
	public void visit(VerbatimNode node) {
		String body = printChildrenToString(node);
		asciidoc.print(body);
	}

	@Override
	public void visit(HtmlBlockNode node) {
		asciidoc.print(node.getText());
	}

	@Override
	public void visit(InlineHtmlNode node) {
		String html = node.getText();
		if (html.toLowerCase().startsWith("<img")){
			String src = html.split("src")[1].split("\"")[1].split("\"")[0];
			String alt = "";
			if (html.toLowerCase().contains("alt")){
				alt = html.split("alt")[1].split("\"")[1].split("\"")[0];
			}
			asciidoc.print("." + defaultString(alt, "Default Caption")).println();
			asciidoc.print(String.format("image::%s[%s]", src, alt)).println();
		}
		else {
			asciidoc.print(node.getText());
		}
	}

	@Override
	public void visit(TextNode node) {
		asciidoc.print(node.getText());
	}

	@Override
	public void visit(SuperNode node) {
		visitChildren(node);
	}

	protected String bold(String bold) {
		return "*" + bold + "*";
	}

	protected String italics(String italics) {
		return "_" + italics + "_";
	}
}