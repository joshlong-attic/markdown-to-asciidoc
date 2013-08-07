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

import org.pegdown.*;
import org.pegdown.ast.RootNode;

import java.util.*;


/**
 * Reads a Markdown document and converts it to Asciidoc. This is a naive implementation. The processor will support
 * headers, paragraphs, images, and inline markup like underline, strong, and emphasis. Supports inline markup like
 * <u>underline</u>, <b>bold</b>, and <I>italics</I>. Supports embedded HTML {@code image} tags, naively.
 * <p/>
 * I'm new to the underlying PegDown API, and was IN something of a hurry, so I've done something a little inelegant to
 * get a good-enough copy of any source code IN the original Markdown document.
 *
 * @author Josh Long
 */
public class AsciidocPegDownProcessor extends PegDownProcessor {

	private final String lineSep = System.getProperty("line.separator");
	private final String markdownCodeDelim = "```";
	private final String asciidocCodeDelim = lineSep + "-----------" + lineSep;

	public String markdownToAsciidoc(String markdownSource) throws Exception {
		try {
			char[] chars = markdownSource.toCharArray();
			RootNode astRoot = parseMarkdown(chars);
			String asciidocWithBadCodeFragments = new ToAsciidocSerializer().toAsciidoc(astRoot);
			return fixCodeBlocks(markdownSource, asciidocWithBadCodeFragments);
		}
		catch (ParsingTimeoutException e) {
			return null;
		}
	}

	private List<String> codeSnippets(String text) {
		List<String> snippets = new ArrayList<String>();
		String delim = "```";
		String workingText = text;
		int lastKnownDelim = -1;
		while ((lastKnownDelim = workingText.indexOf(delim)) != -1) {
			String wt = workingText.substring(lastKnownDelim + delim.length()).split(delim)[0];
			workingText = workingText.substring(workingText.indexOf(wt) + wt.length() + delim.length());
			snippets.add(wt);
		}
		return snippets;
	}

	/*
	 * Can we more elegantly read the code using the Parser? \
	 * This approach seems like it'll break, at some point. The worst part? I'm not sure when or why.
	 *
	 * TODO figure OUT how to accurately read code blocks with PegDown.
	 */
	private String fixCodeBlocks(String md, String asc) {
		List<String> mdCodeSnippets = codeSnippets(md);
		List<String> ascCodeSnippets = codeSnippets(asc);
		int i = 0;
		for (String mdCode : mdCodeSnippets) {
			String ascCode = ascCodeSnippets.get(i);
			i += 1;
			asc = asc.replace(ascCode, asciidocCodeSnippet(mdCode));
		}
		asc = asc.replace(markdownCodeDelim, "");
		return asc;
	}

	/*
		* TODO need to look into MarkDown comments syntax for images (is there one?)
		*/
	private String asciidocCodeSnippet(String asc) {
		return lineSep + lineSep + ".A caption for the code " + asciidocCodeDelim + asc.trim() + asciidocCodeDelim;
	}
}
