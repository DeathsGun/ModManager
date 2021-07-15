/*
 * Copyright 2021 DeathsGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.deathsgun.modmanager;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import xyz.deathsgun.modmanager.api.mod.ModState;
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;
import xyz.deathsgun.modmanager.api.provider.IModProvider;
import xyz.deathsgun.modmanager.providers.modrinth.Modrinth;
import xyz.deathsgun.modmanager.services.IconDownloadService;
import xyz.deathsgun.modmanager.services.ModDownloadService;
import xyz.deathsgun.modmanager.services.UpdateCheckService;

import java.util.ArrayList;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ModManager implements ClientModInitializer, ModMenuApi {

    private static final String currentProvider = "Modrinth";
    private static final ArrayList<IModProvider> modProviders = new ArrayList<>();
    private static final UpdateCheckService updateCheckService = new UpdateCheckService();
    private static final ModDownloadService modDownloadService = new ModDownloadService();
    private static final IconDownloadService iconService = new IconDownloadService();

    public static void registerModProvider(IModProvider provider) {
        ModManager.modProviders.removeIf(value -> value.getName().equals(provider.getName()));
        ModManager.modProviders.add(provider);
    }

    public static IModProvider getModProvider() {
        return modProviders.stream().filter(iModProvider -> iModProvider.getName().equals(currentProvider)).findFirst().orElse(null);
    }

    public static IconDownloadService getIconDownloader() {
        return iconService;
    }

    public static ModDownloadService getModDownloader() {
        return modDownloadService;
    }

    public static UpdateCheckService getUpdateChecker() {
        return updateCheckService;
    }

    @Override
    public void onInitializeClient() {
        registerModProvider(new Modrinth());
    }

    public static ModState getState(SummarizedMod mod) {
        Optional<ModContainer> installedMod = getInstalledMod(mod);
        return installedMod.map(modContainer -> updateCheckService.isUpdateAvailable(mod, modContainer) ? ModState.OUTDATED : ModState.INSTALLED).orElse(ModState.DOWNLOADABLE);
    }

    private static Optional<ModContainer> getInstalledMod(SummarizedMod mod) {
        return FabricLoader.getInstance().getAllMods().stream().filter(container -> container.getMetadata().getId().equalsIgnoreCase(mod.slug()) ||
                container.getMetadata().getId().equalsIgnoreCase(mod.slug().replaceAll("-", "")))
                .filter(container -> container.getMetadata().getAuthors().stream().anyMatch(person -> person.getName().equalsIgnoreCase(mod.author()))).findFirst();
    }

}
