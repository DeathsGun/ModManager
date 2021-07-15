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

package xyz.deathsgun.modmanager.providers.modrinth;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import xyz.deathsgun.modmanager.api.mod.Category;
import xyz.deathsgun.modmanager.api.mod.DetailedMod;
import xyz.deathsgun.modmanager.api.mod.ModVersion;
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;
import xyz.deathsgun.modmanager.api.provider.IModProvider;
import xyz.deathsgun.modmanager.api.provider.Sorting;
import xyz.deathsgun.modmanager.providers.modrinth.model.Mod;
import xyz.deathsgun.modmanager.providers.modrinth.model.SearchResponse;
import xyz.deathsgun.modmanager.providers.modrinth.model.Version;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Modrinth implements IModProvider {

    private final Logger logger = LogManager.getLogger("Modrinth");
    private final ArrayList<Category> categories = new ArrayList<>();
    private final HashMap<String, List<SummarizedMod>> cachedCategoryRequests = new HashMap<>();
    private final Gson gson = new Gson();
    private final String baseUrl = "https://api.modrinth.com";
    private final HttpClient http = HttpClient.newHttpClient();

    @Override
    public String getName() {
        return "Modrinth";
    }

    @Override
    public List<Category> getCategories() throws Exception {
        if (!this.categories.isEmpty()) {
            return this.categories;
        }
        logger.debug("Getting categories");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(this.baseUrl + "/api/v1/tag/category")).build();
        HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.error("Received an invalid status code while getting the categories {}: {}", response.statusCode(), response.body());
            throw new Exception(response.body());
        }
        ArrayList<String> categoryStrings = gson.fromJson(response.body(), new TypeToken<ArrayList<String>>() {
        }.getType());
        categories.clear();
        for (String category : categoryStrings) {
            this.categories.add(new Category(category, new TranslatableText(String.format("modmanager.category.%s", category))));
        }
        return categories;
    }

    @Override
    public List<SummarizedMod> getMods(Sorting sorting, int page, int limit) throws Exception {
        logger.debug("Getting a general list of mods");
        URIBuilder uriBuilder = new URIBuilder(this.baseUrl + "/api/v1/mod");
        uriBuilder.addParameter("index", sorting.name());
        uriBuilder.addParameter("filters", "categories=\"fabric\"");
        return getSummarizedMods(page, limit, uriBuilder);
    }

    @Override
    public List<SummarizedMod> getMods(Category category, int page, int limit) throws Exception {
        logger.debug("Getting category '{}' from Modrinth", category.id());
        String key = String.format("%s|%d|%d", category.id(), page, limit);
        if (this.cachedCategoryRequests.containsKey(key)) {
            return this.cachedCategoryRequests.get(key);
        }
        URIBuilder uriBuilder = new URIBuilder(this.baseUrl + "/api/v1/mod");
        uriBuilder.addParameter("filters", String.format("categories=\"fabric\" AND NOT client_side=\"unsupported\" AND categories=\"%s\"", category.id()));
        List<SummarizedMod> mods = getSummarizedMods(page, limit, uriBuilder);
        this.cachedCategoryRequests.put(key, mods);
        return mods;
    }

    @Override
    public List<SummarizedMod> getMods(@NotNull String query, int page, int limit) throws Exception {
        logger.debug("Searching for '{}' in Modrinth", query);
        URIBuilder uriBuilder = new URIBuilder(this.baseUrl + "/api/v1/mod");
        uriBuilder.addParameter("query", query);
        uriBuilder.addParameter("filters", "categories=\"fabric\" AND NOT client_side=\"unsupported\"");
        return getSummarizedMods(page, limit, uriBuilder);
    }

    @NotNull
    private ArrayList<SummarizedMod> getSummarizedMods(int page, int limit, URIBuilder uriBuilder) throws Exception {
        uriBuilder.addParameter("version", String.format("versions=%s", MinecraftClient.getInstance().getGame().getVersion().getReleaseTarget()));
        uriBuilder.addParameter("offset", String.valueOf(page * limit));
        uriBuilder.addParameter("limit", String.valueOf(limit));
        logger.debug("Using {} for query", uriBuilder.toString());
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uriBuilder.build()).build();
        HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception(response.body());
        }
        SearchResponse searchResponse = gson.fromJson(response.body(), SearchResponse.class);
        return searchResponse.toSummarizedMods();
    }

    @Override
    public DetailedMod getMod(@NotNull String id) throws Exception {
        id = id.replaceFirst("local-", "");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(this.baseUrl + "/api/v1/mod/" + id)).build();
        HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception(response.body());
        }
        return gson.fromJson(response.body(), Mod.class).toDetailedMod();
    }

    @Override
    public List<ModVersion> getVersionsForMod(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(this.baseUrl + "/api/v1/mod/" + id + "/version")).build();
        HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception(response.body());
        }
        return Version.toModVersion(gson.fromJson(response.body(), new TypeToken<ArrayList<Version>>() {
        }.getType()));
    }
}
