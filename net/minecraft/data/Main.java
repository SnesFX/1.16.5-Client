/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.worldgen.biome.BiomeReport;

public class Main {
    public static void main(String[] arrstring) throws IOException {
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec abstractOptionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpecBuilder = optionParser.accepts("server", "Include server generators");
        OptionSpecBuilder optionSpecBuilder2 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpecBuilder3 = optionParser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionSpecBuilder4 = optionParser.accepts("reports", "Include data reports");
        OptionSpecBuilder optionSpecBuilder5 = optionParser.accepts("validate", "Validate inputs");
        OptionSpecBuilder optionSpecBuilder6 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(arrstring);
        if (optionSet.has((OptionSpec)abstractOptionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn((OutputStream)System.out);
            return;
        }
        Path path = Paths.get((String)argumentAcceptingOptionSpec.value(optionSet), new String[0]);
        boolean bl = optionSet.has((OptionSpec)optionSpecBuilder6);
        boolean bl2 = bl || optionSet.has((OptionSpec)optionSpecBuilder2);
        boolean bl3 = bl || optionSet.has((OptionSpec)optionSpecBuilder);
        boolean bl4 = bl || optionSet.has((OptionSpec)optionSpecBuilder3);
        boolean bl5 = bl || optionSet.has((OptionSpec)optionSpecBuilder4);
        boolean bl6 = bl || optionSet.has((OptionSpec)optionSpecBuilder5);
        DataGenerator dataGenerator = Main.createStandardGenerator(path, optionSet.valuesOf((OptionSpec)argumentAcceptingOptionSpec2).stream().map(string -> Paths.get(string, new String[0])).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6);
        dataGenerator.run();
    }

    public static DataGenerator createStandardGenerator(Path path, Collection<Path> collection, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
        DataGenerator dataGenerator = new DataGenerator(path, collection);
        if (bl || bl2) {
            dataGenerator.addProvider(new SnbtToNbt(dataGenerator).addFilter(new StructureUpdater()));
        }
        if (bl) {
            dataGenerator.addProvider(new ModelProvider(dataGenerator));
        }
        if (bl2) {
            dataGenerator.addProvider(new FluidTagsProvider(dataGenerator));
            BlockTagsProvider blockTagsProvider = new BlockTagsProvider(dataGenerator);
            dataGenerator.addProvider(blockTagsProvider);
            dataGenerator.addProvider(new ItemTagsProvider(dataGenerator, blockTagsProvider));
            dataGenerator.addProvider(new EntityTypeTagsProvider(dataGenerator));
            dataGenerator.addProvider(new RecipeProvider(dataGenerator));
            dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
            dataGenerator.addProvider(new LootTableProvider(dataGenerator));
        }
        if (bl3) {
            dataGenerator.addProvider(new NbtToSnbt(dataGenerator));
        }
        if (bl4) {
            dataGenerator.addProvider(new BlockListReport(dataGenerator));
            dataGenerator.addProvider(new RegistryDumpReport(dataGenerator));
            dataGenerator.addProvider(new CommandsReport(dataGenerator));
            dataGenerator.addProvider(new BiomeReport(dataGenerator));
        }
        return dataGenerator;
    }
}

