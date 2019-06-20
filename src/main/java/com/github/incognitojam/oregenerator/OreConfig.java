package com.github.incognitojam.oregenerator;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.bukkit.Material;

import java.io.*;
import java.util.*;

class OreConfig {
    private final Gson gson;
    private final Random random;

    private boolean initialised;
    private OreEntry[] oreEntries;
    private Integer totalOreEntryWeight;

    private double flintChance;

    OreConfig(long seed) {
        gson = new Gson();
        random = new Random();
        random.setSeed(seed);
    }

    void loadConfig(Reader reader) throws IllegalArgumentException {
        // Read Ore Config from the given file
        JsonReader jsonReader = new JsonReader(reader);
        Model model = gson.fromJson(jsonReader, Model.class);

        /*
         * Iterate over chances map and add the entries to a list, sorting them
         * by their weight.
         *
         * This means the set will be sorted with the "highest weight" (most
         * common) ores at the head of the list, so the search time for these
         * ores will be reduced.
         */
        List<OreEntry> oreEntries = new ArrayList<>();
        int sum = 0;
        for (Map.Entry<String, Integer> entry : model.ores.entrySet()) {
            // Convert the given material name to a Material object
            Material material = Material.valueOf(entry.getKey());

            // Ensure the chance variable is not null
            Integer chanceI = entry.getValue();
            int chance = chanceI == null ? 0 : chanceI;

            // Ensure the chance variable is positive
            if (chance <= 0)
                // If it negative or zero, skip it
                continue;

            // Add this entry to the map
            oreEntries.add(new OreEntry(material, chance));
            sum += chance;
        }
        oreEntries.sort(Comparator.comparingInt(a -> -a.weight));

        this.oreEntries = oreEntries.toArray(new OreEntry[0]);
        this.totalOreEntryWeight = sum;
        this.initialised = true;

        // Get flint chance from config, defaulting to "1 in 100" if not specified
        this.flintChance = model.flintChance == null ? 0.01 : model.flintChance;
    }

    Material getNextOreMaterial() {
        if (!initialised) return null;
        return getNextOreEntry().material;
    }

    double getFlintChance() {
        return flintChance;
    }

    private OreEntry getNextOreEntry() {
        final int target = random.nextInt(totalOreEntryWeight);

        StringBuilder builder = new StringBuilder();
        builder.append("target: " + target + "\n");
        builder.append("total weight: " + totalOreEntryWeight + "\n");

        int sum = 0, index = -1;
        OreEntry entry;
        do {
            builder.append("(A) sum: " + sum + " index: " + index + "\n");

            index++;
            if (index >= oreEntries.length) {
                System.out.println(builder);
            }

            entry = oreEntries[index];
            builder.append("    entry: " + entry + "\n");

            sum += entry.weight;

            builder.append("(B) sum: " + sum + " index: " + index + "\n");
        } while (sum < target);

        return entry;
    }

    static class OreEntry {
        final Material material;
        final int weight;

        OreEntry(Material material, int weight) {
            this.material = material;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "OreEntry{" +
                    "material=" + material +
                    ", weight=" + weight +
                    '}';
        }
    }

    static class Model {
        Map<String, Integer> ores;
        Double flintChance;
    }
}
