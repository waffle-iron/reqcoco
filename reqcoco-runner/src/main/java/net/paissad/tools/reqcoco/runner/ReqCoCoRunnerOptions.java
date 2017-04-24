package net.paissad.tools.reqcoco.runner;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.OptionHandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCoCoRunnerOptions {

	@Option(name = "-h", aliases = { "--help" }, help = true, usage = "Shows the help.")
	private boolean			help;

	@Option(name = "--out", required = true, metaVar = "<dir>", usage = "Directory where to store the coverage reports.")
	private String			outputFolder;

	@Option(name = "--in", required = true, metaVar = "<file>", usage = "Source (file or url) containing the requirements to parse.")
	private String			requirementSource;

	@Option(name = "--console-report", required = false, usage = "Whether or not to generate reports onto the standard console output.")
	private boolean			buildConsoleReport;

	@Option(name = "--html-report", required = false, usage = "Whether or not to generate HTML reports into the specified output directory.")
	private boolean			buildHtmlReport	= true;

	@Option(name = "--log-level", required = false, metaVar = "[level]", usage = "Sets the log level. Possible values are ERROR|WARN|INFO|DEBUG|TRACE. Default value is 'INFO'.")
	private String			logLevel;

	@Argument
	private List<String>	arguments		= new ArrayList<>();

	/**
	 * @param args : arguments / options.
	 * @return The command line parser.
	 * @throws CmdLineException If an error occurs while parsing the options.
	 */
	public CmdLineParser parseOptions(final String... args) throws CmdLineException {

		final CmdLineParser parser = new CmdLineParser(this);

		ParserProperties.defaults().withUsageWidth(150);

		try {
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			System.err.println();
			System.err.println("======> " + e.getMessage());
			System.err.println();
			printUsage(parser);
			throw e;
		}

		return parser;
	}

	/**
	 * Prints the usage.
	 * 
	 * @param parser : the options & arguments parser
	 */
	public static void printUsage(final CmdLineParser parser) {
		System.out.println("reqcoco-runner [options...] arguments...");
		parser.printUsage(System.out);
		System.out.println();

		System.out.println("  Example: reqcoco-runner " + parser.printExample(filter -> !filter.option.help()));
	}
}