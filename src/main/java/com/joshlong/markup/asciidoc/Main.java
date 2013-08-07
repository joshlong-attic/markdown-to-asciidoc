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

import java.io.File;
import java.util.*;

import static com.joshlong.markup.asciidoc.StringUtils.*;

/**
 * Expects one or two arguments on the command line: {@code -IN}, followed by a valid path, and {@code -OUT}, also
 * followed by a valid path. <EM>Valid</EM> paths are paths that may be passed to {@link File#File(String)}.
 *
 * @author Josh Long
 */
public class Main {

	public final static String USER_HOME = System.getProperty("user.home");
	public final static String IN = "in";
	public final static String OUT = "out";

	public static void main(String[] args) throws Throwable {


		AsciidocPegDownProcessor pegDownProcessor = new AsciidocPegDownProcessor();
		Map<String, String> argsMap = options(args);
		String markdown = read(in(argsMap));
		String asciidoc = pegDownProcessor.markdownToAsciidoc(markdown);
		write(out(argsMap), asciidoc);

	}

	private static File in(Map<String, String> options) {
		String inPath = defaultString(options.get(IN), new File(USER_HOME, IN + ".md").getAbsolutePath());
		return new File(inPath);
	}

	private static File out(Map<String, String> args) {
		String outPath = defaultString(args.get(OUT), new File(USER_HOME, OUT + ".asc").getAbsolutePath());
		return new File(outPath);
	}

	private static Map<String, String> options(String[] args) {
		Map<String, String> options = new HashMap<String, String>();
		if (args != null && args.length > 0){
			int index = 0;
			for (String arg : args) {
				if (arg.equals("-" + IN)){
					options.put(IN, args[index + 1]);
				}
				if (arg.equals("-" + OUT)){
					options.put(OUT, args[index + 1]);
				}
				index += 1;
			}
		}
		return options;
	}
}
