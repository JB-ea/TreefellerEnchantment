package io.github.lamelemon.treefellerEnchantment;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class EnchantBootstrap implements PluginBootstrap {
    private boolean bootstrapFailed = false;

    @Override
    public void bootstrap(BootstrapContext context) {

        InputStream in = getClass().getResourceAsStream("/config.yml");
        if (in == null) {
            bootstrapFailed = true;
            return;
        }

        ConfigurationSection config = YamlConfiguration.loadConfiguration(new InputStreamReader(in)).getConfigurationSection("enchantment");
        Registry<@NotNull ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

        context.getLifecycleManager().registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            EnchantmentKeys.create(Key.key("treefeller:treefeller")),
                            b -> {
                                b.description(Component.text(config.getString("description", "Treefeller")))
                                        .maxLevel(config.getInt("max-level", 5))
                                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                                                config.getInt("minimum-cost.base-cost", 5),
                                                config.getInt("minimum-cost.additional-cost", 8)
                                        ))
                                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                                                config.getInt("maximum-cost.base-cost", 25),
                                                config.getInt("maximum-cost.additional-cost", 8)
                                        ))
                                        .weight(config.getInt("weight", 4))
                                        .anvilCost(config.getInt("anvil-cost", 4))
                                        .supportedItems(RegistrySet.keySetFromValues(RegistryKey.ITEM,
                                                mapValid(config.getStringList("supported-items"),
                                                        item -> itemRegistry.get(Key.key(item))
                                                )
                                        ))
                                        .primaryItems(RegistrySet.keySetFromValues(RegistryKey.ITEM,
                                                mapValid(config.getStringList("supported-items"),
                                                        item -> itemRegistry.get(Key.key(item))
                                                )
                                        ))
                                        .activeSlots(
                                                mapValid(config.getStringList("active-slots"), EquipmentSlotGroup::getByName)
                                        );
                            }
                    );
                })
        );
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        if (bootstrapFailed) {
            return null;
        }
        return new TreefellerEnchantment();
    }

    // Helper function to avoid repeating code when reading config (stay dry gang)
    private static <T> List<T> mapValid(List<String> keys, Function<String, T> mapper) {
        return keys.stream().map(mapper).filter(Objects::nonNull).toList();
    }
}
