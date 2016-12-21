package com.wwwlicious;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.util.concurrent.Nullable;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CakeBuildTaskConfigurator extends AbstractTaskConfigurator
{

    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put("say", params.getString("say"));
        config.put("script", params.getString("script"));
        config.put("tasks", params.getString("tasks"));
        config.put("logLevel", params.getString("logLevel"));

        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("script", "build.cake");
        context.put("tasks", "default");
        context.put("logLevel", "Information");
        context.put("say", "Hello, World!");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("script", taskDefinition.getConfiguration().get("script"));
        context.put("tasks", taskDefinition.getConfiguration().get("tasks"));
        context.put("logLevel", taskDefinition.getConfiguration().get("logLevel"));
        context.put("say", taskDefinition.getConfiguration().get("say"));
    }

    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String sayValue = params.getString("say");
        if (StringUtils.isEmpty(sayValue))
        {
            errorCollection.addError("say", getI18nBean().getText("helloworld.say.error"));
        }

        // validate other params
    }
}
