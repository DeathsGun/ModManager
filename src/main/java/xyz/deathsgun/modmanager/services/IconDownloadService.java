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

package xyz.deathsgun.modmanager.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class IconDownloadService extends Thread {

    private final Logger logger = LogManager.getLogger("Icon Downloader");
    private final HashMap<String, SummarizedMod> mods = new HashMap<>();
    private final HashMap<String, Identifier> images = new HashMap<>();
    private final ArrayList<String> loading = new ArrayList<>();
    private final ArrayList<String> errored = new ArrayList<>();
    private final HttpClient httpClient;

    public IconDownloadService() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        setName("Icon downloader");
        start();
    }

    @Override
    public void run() {
        while (isAlive()) {
            if (mods.isEmpty()) {
                continue;
            }
            for (SummarizedMod mod : new ArrayList<>(mods.values())) {
                try {
                    this.mods.remove(mod.id());
                    downloadIcon(mod);
                } catch (Exception e) {
                    e.printStackTrace();
                    errored.add(mod.id());
                }
            }
        }
    }

    public void addMod(SummarizedMod mod) {
        if (this.mods.containsKey(mod.id())) {
            return;
        }
        this.mods.put(mod.id(), mod);
    }

    public boolean isErrored(String id) {
        return this.errored.contains(id);
    }

    public Identifier getIcon(String id) {
        return this.images.get(id);
    }

    private void downloadIcon(SummarizedMod mod) throws Exception {
        logger.debug("Downloading icon for {}", mod.slug());
        this.loading.add(mod.id());
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(mod.icon())).build();
        HttpResponse<InputStream> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            logger.debug("Failed to download icon for {} ! received status code: {}", mod.slug(), response.statusCode());
            errored.add(mod.id());
            return;
        }
        logger.debug("Reading icon for {}", mod.slug());
        NativeImage image = NativeImage.read(response.body());
        Identifier iconLocation = new Identifier("modmanager", mod.slug() + "_icon");
        MinecraftClient.getInstance().getTextureManager().registerTexture(iconLocation, new NativeImageBackedTexture(image));
        this.images.put(mod.id(), iconLocation);
        this.loading.remove(mod.id());
        this.errored.remove(mod.id());
        logger.debug("Finished downloading icon for {}", mod.slug());
    }

    public boolean isLoading(String id) {
        return loading.contains(id);
    }

    public void destroyIcon(SummarizedMod mod) {
        logger.debug("Removing {} icon", mod.slug());
        this.loading.remove(mod.id());
        this.errored.remove(mod.id());
        this.mods.remove(mod.id());
        Identifier identifier = this.images.remove(mod.id());
        MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
    }

    public void destroyIcons() {
        if (mods.isEmpty()) {
            return;
        }
        for (SummarizedMod mod : new ArrayList<>(mods.values())) {
            this.destroyIcon(mod);
        }
    }
}
