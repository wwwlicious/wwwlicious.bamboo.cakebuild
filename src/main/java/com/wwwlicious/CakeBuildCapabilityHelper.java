package com.wwwlicious;

import com.atlassian.bamboo.v2.build.agent.capability.*;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class CakeBuildCapabilityHelper extends AbstractCapabilityTypeModule implements CapabilityDefaultsHelper {

    public static final String CAKEBUILD_CAPABILITY_LABEL = "CakeBuild";

    public static final String CAKEBUILD_CAPABILITY_KEY = CakeBuildTask.CAKEBUILD_CAPABILITY_PREFIX + "." + CAKEBUILD_CAPABILITY_LABEL;

    @NotNull
    @Override
    public CapabilitySet addDefaultCapabilities(@NotNull CapabilitySet capabilitySet) {
        String frameworkDirectory = "c:\\cake";
        final File cakeFilePath = new File(frameworkDirectory, "Cake.exe");

        capabilitySet.addCapability(new CapabilityImpl(CAKEBUILD_CAPABILITY_KEY, cakeFilePath.getAbsolutePath()));
        return capabilitySet;
    }

    @NotNull
    @Override
    public Map<String, String> validate(@NotNull Map<String, String[]> map) {
        Map<String, String> fieldErrors = new HashMap();
        return fieldErrors;
    }

    @NotNull
    @Override
    public Capability getCapability(@NotNull Map<String, String[]> map) {
        return new CapabilityImpl(CAKEBUILD_CAPABILITY_KEY, "ckae.exe");
    }

    @NotNull
    @Override
    public String getLabel(@NotNull String s) {
        return "cake label";
    }

    @NotNull
    public String getCapabilityTypeKey()
    {
        return "cake type key";
    }

    @NotNull
    public String getCapabilityTypeLabel()
    {
        return CAKEBUILD_CAPABILITY_LABEL;
    }
}
