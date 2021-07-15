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
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResponse {

    int limit;
    @SerializedName("total_hits")
    int totalHits;
    private List<ModResult> hits;
    private int offset;

    public ArrayList<SummarizedMod> toSummarizedMods() {
        ArrayList<SummarizedMod> result = new ArrayList<>();
        for (ModResult modResult : hits) {
            String slug = modResult.getSlug().toLowerCase(Locale.ROOT).replaceAll(" ", "");
            result.add(new SummarizedMod(modResult.getModId(), slug, modResult.getAuthor(), modResult.getTitle(), modResult.getVersions(), modResult.getDescription(), modResult.getIconUrl()));
        }
        return result;
    }
}
