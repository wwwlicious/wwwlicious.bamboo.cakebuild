package com.wwwlicious;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.logger.interceptors.ErrorMemorisingInterceptor;
import com.atlassian.bamboo.build.logger.interceptors.LogMemorisingInterceptor;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.CommandlineStringUtils;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.CurrentResult;
import com.atlassian.bamboo.v2.build.agent.capability.Capability;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityDefaultsHelper;
import com.atlassian.utils.process.ExternalProcess;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CakeBuildTask implements TaskType
{
    public static final String CAKEBUILD_CAPABILITY_PREFIX = CapabilityDefaultsHelper.CAPABILITY_BUILDER_PREFIX + ".cake";
    private static final int LINES_TO_PARSE_FOR_ERRORS = 200;

    private final ProcessService processService;
    private final CapabilityContext capabilityContext;

    public CakeBuildTask(final ProcessService processService, final CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        final CurrentResult currentBuildResult = taskContext.getCommonContext().getCurrentResult();
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        LogMemorisingInterceptor recentLogLines = new LogMemorisingInterceptor(LINES_TO_PARSE_FOR_ERRORS);
        final ErrorMemorisingInterceptor errorLines = ErrorMemorisingInterceptor.newInterceptor();

        buildLogger.getInterceptorStack().add(recentLogLines);
        buildLogger.getInterceptorStack().add(errorLines);

        try{
        final CakeBuildSettings settings = new CakeBuildSettings(taskContext.getConfigurationMap());

        final String cakeExePath = "cake.exe";
        final String msBuildExecutable = getCakeBuildExecutablePath(cakeExePath);

        Preconditions.checkNotNull(msBuildExecutable, "Could not find MSBuild.exe");

        ExternalProcess process = processService.createExternalProcess(taskContext,
                new ExternalProcessBuilder()
                        .command(Arrays.asList(cakeExePath))
                        .workingDirectory(taskContext.getWorkingDirectory()));


            final List<String> command = Lists.newArrayList(msBuildExecutable);
            command.addAll(CommandlineStringUtils.tokeniseCommandline(settings.tasks));
            command.add(settings.tasks);

            return TaskResultBuilder.newBuilder(taskContext)
                    .checkReturnCode(processService.executeExternalProcess(taskContext, new ExternalProcessBuilder()
                            .command(command)
                            //.env(environment)
                            .workingDirectory(taskContext.getWorkingDirectory())))
                    .build();
        }
        finally
        {
            currentBuildResult.addBuildErrors(errorLines.getErrorStringList());
            //currentBuildResult.addBuildErrors(DotNetLogHelper.parseErrorOutput(recentLogLines.getLogEntries()));
        }
    }

    public class CakeBuildSettings
    {
        String script, tasks, logLevel;

        public CakeBuildSettings(ConfigurationMap map){
            script = map.get("script");
            tasks = map.get("tasks");
            logLevel = map.get("logLevel");
        }
    }

    private String getCakeBuildExecutablePath(final String label)
    {
        final Capability capability = capabilityContext.getCapabilitySet().getCapability(CAKEBUILD_CAPABILITY_PREFIX + "." + label);
        Preconditions.checkNotNull(capability, "Capability");
        return new File(capability.getValue()).getAbsolutePath();
    }
}

