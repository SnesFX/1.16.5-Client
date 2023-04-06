/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.monitoring.jmx;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MinecraftServerStatistics
implements DynamicMBean {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer server;
    private final MBeanInfo mBeanInfo;
    private final Map<String, AttributeDescription> attributeDescriptionByName = Stream.of(new AttributeDescription("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class), new AttributeDescription("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap(attributeDescription -> AttributeDescription.access$200(attributeDescription), Function.identity()));

    private MinecraftServerStatistics(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        MBeanAttributeInfo[] arrmBeanAttributeInfo = (MBeanAttributeInfo[])this.attributeDescriptionByName.values().stream().map(object -> ((AttributeDescription)object).asMBeanAttributeInfo()).toArray(n -> new MBeanAttributeInfo[n]);
        this.mBeanInfo = new MBeanInfo(MinecraftServerStatistics.class.getSimpleName(), "metrics for dedicated server", arrmBeanAttributeInfo, null, null, new MBeanNotificationInfo[0]);
    }

    public static void registerJmxMonitoring(MinecraftServer minecraftServer) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new MinecraftServerStatistics(minecraftServer), new ObjectName("net.minecraft.server:type=Server"));
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException jMException) {
            LOGGER.warn("Failed to initialise server as JMX bean", (Throwable)jMException);
        }
    }

    private float getAverageTickTime() {
        return this.server.getAverageTickTime();
    }

    private long[] getTickTimes() {
        return this.server.tickTimes;
    }

    @Nullable
    @Override
    public Object getAttribute(String string) {
        AttributeDescription attributeDescription = this.attributeDescriptionByName.get(string);
        return attributeDescription == null ? null : attributeDescription.getter.get();
    }

    @Override
    public void setAttribute(Attribute attribute) {
    }

    @Override
    public AttributeList getAttributes(String[] arrstring) {
        List<Attribute> list = Arrays.stream(arrstring).map(this.attributeDescriptionByName::get).filter(Objects::nonNull).map(attributeDescription -> new Attribute(attributeDescription.name, attributeDescription.getter.get())).collect(Collectors.toList());
        return new AttributeList(list);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributeList) {
        return new AttributeList();
    }

    @Nullable
    @Override
    public Object invoke(String string, Object[] arrobject, String[] arrstring) {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }

    static final class AttributeDescription {
        private final String name;
        private final Supplier<Object> getter;
        private final String description;
        private final Class<?> type;

        private AttributeDescription(String string, Supplier<Object> supplier, String string2, Class<?> class_) {
            this.name = string;
            this.getter = supplier;
            this.description = string2;
            this.type = class_;
        }

        private MBeanAttributeInfo asMBeanAttributeInfo() {
            return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
        }
    }

}

