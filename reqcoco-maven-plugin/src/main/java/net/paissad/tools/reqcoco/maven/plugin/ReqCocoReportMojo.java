package net.paissad.tools.reqcoco.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import net.paissad.tools.reqcoco.runner.ExitStatus;
import net.paissad.tools.reqcoco.runner.ReqRunner;
import net.paissad.tools.reqcoco.runner.ReqRunnerOptions;
import net.paissad.tools.reqcoco.runner.ReqSourceType;

@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE, requiresProject = true, threadSafe = true, aggregator = true)
public class ReqCocoReportMojo extends AbstractReqCoCoMojo {

    /**
     * The configuration file to use for building the report.
     */
    @Parameter(property = "reqcoco.report.config", required = true, defaultValue = "${basedir}${file.separator}reqcoco.properties")
    private String        config;

    /**
     * The type of the source which contains the declarations of the requirements.
     */
    @Parameter(property = "reqcoco.report.source.type", required = true, defaultValue = "FILE")
    private ReqSourceType sourcetype;

    /**
     * The location where all requirements are declared.
     */
    @Parameter(property = "reqcoco.report.source.location", required = true)
    private String        sourcelocation;

    /**
     * The path to the source code.
     */
    @Parameter(property = "reqcoco.report.code.source", required = true, defaultValue = "${project.build.sourceDirectory}")
    private String        sourceCodePath;

    /**
     * The path to the test code.
     */
    @Parameter(property = "reqcoco.report.code.test", required = true, defaultValue = "${project.build.testSourceDirectory}")
    private String        testCodePath;

    /**
     * The output directory where to store the reports.
     */
    @Parameter(property = "reqcoco.report.outputdir", required = true, defaultValue = "${project.build.directory}${file.separator}reqcoco-reports")
    private File          outputdir;

    /**
     * Whether or not to generate the HTML report.
     */
    @Parameter(property = "reqcoco.report.htmlreport", required = false, defaultValue = "true")
    private boolean       htmlreport;

    /**
     * Whether or not to generate the EXCEL report.
     */
    @Parameter(property = "reqcoco.report.excelreport", required = false, defaultValue = "true")
    private boolean       excelreport;

    /**
     * The report name. If not specified, the default value will be used. The default value is hold by the ReqCoCo Core project, not by this Maven
     * plugin.
     */
    @Parameter(property = "reqcoco.report.name", required = false)
    private String        reportname;

    @Parameter(readonly = true, required = false, defaultValue = "${project}")
    private MavenProject  project;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException {

        String format = "############# %-15s : %s";
        getLog().debug("======================= ReqCoCo parameters =====================================");
        getLog().debug(String.format(format, "config", this.config));
        getLog().debug(String.format(format, "sourcetype", this.sourcetype));
        getLog().debug(String.format(format, "sourcelocation", this.sourcelocation));
        getLog().debug(String.format(format, "outputdir", this.outputdir));
        getLog().debug(String.format(format, "sourceCodePath", this.sourceCodePath));
        getLog().debug(String.format(format, "testCodePath", this.testCodePath));
        getLog().debug(String.format(format, "htmlreport", this.htmlreport));
        getLog().debug(String.format(format, "excelreport", this.excelreport));
        getLog().debug(String.format(format, "reportname", this.reportname));
        getLog().debug("================================================================================");

        try {

            final List<String> arguments = new ArrayList<>(Arrays.asList(
                    new String[] { "--config", this.config, "--input-type", this.sourcetype.name(), "--input", this.sourcelocation, "--output", this.outputdir.toString() }));

            if (!StringUtils.isBlank(this.reportname)) {
                arguments.add("--report-name");
                arguments.add(this.reportname);
            }

            final ReqRunner reqRunner = new ReqRunner();
            final int parseArgsStatus = reqRunner.parseArguments(arguments.toArray(new String[arguments.size()]));

            if (ExitStatus.OK.getCode() == parseArgsStatus) {

                getLog().info("Running the requirements code coverage report generator");
                final ReqRunnerOptions options = reqRunner.getOptions();

                if (!StringUtils.isBlank(options.getSourceCodePath())) {
                    getLog().warn("The value of '" + ReqRunnerOptions.CONFIG_SOURCE_CODE_PATH + "' will not be used. It must be defined into the pom.xml");
                }
                if (!StringUtils.isBlank(options.getTestCodePath())) {
                    getLog().warn("The value of '" + ReqRunnerOptions.CONFIG_TEST_CODE_PATH + "' will not be used. It must be defined into the pom.xml");
                }

                getLog().debug("Setting the source code path to -> " + this.sourceCodePath);
                options.setSourceCodePath(this.sourceCodePath);

                getLog().debug("Setting the test code path to ->" + this.testCodePath);
                options.setTestCodePath(this.testCodePath);

                options.setReportConsole(false);
                options.setReportHtml(this.htmlreport);
                options.setReportExcel(this.excelreport);

                final int generateReportsStatus = reqRunner.generateReports();

                if (ExitStatus.OK.getCode() != generateReportsStatus) {
                    String errMsg = "The return code status while generating the reports is : " + generateReportsStatus;
                    getLog().error(errMsg);
                    throw new MojoExecutionException(errMsg);
                }

            } else {
                String errMsg = "The ReqRunner program returned an exit code of " + parseArgsStatus + ". The arguments are : " + arguments;
                getLog().error(errMsg);
                throw new MojoExecutionException(errMsg);
            }

            getLog().info("Finished generating requirements coverage report");

        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Error while generating the requirements code coverage", e);
        }
    }
}
