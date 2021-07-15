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

package xyz.deathsgun.modmanager.providers.modrinth.model;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.TranslatableText;
import xyz.deathsgun.modmanager.api.mod.Category;
import xyz.deathsgun.modmanager.api.mod.DetailedMod;

import java.util.ArrayList;
import java.util.List;

public class Mod {

    private String id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private License license;
    private int downloads;
    private List<String> categories;
    @SerializedName("issues_url")
    private String issuesUrl;
    @SerializedName("source_url")
    private String sourceUrl;
    @SerializedName("wiki_url")
    private String wikiUrl;
    private List<String> versions;

    public DetailedMod toDetailedMod() {
        ArrayList<Category> categoriesList = new ArrayList<>();
        categories.forEach(s -> categoriesList.add(new Category(s, new TranslatableText("modmanager.category." + s))));
        return new DetailedMod(id.replaceFirst("locale-", ""), title, description, body, license.name(), downloads, categoriesList, issuesUrl, sourceUrl, wikiUrl, versions);
    }
}
