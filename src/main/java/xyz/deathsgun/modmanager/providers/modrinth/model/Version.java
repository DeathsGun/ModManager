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
import xyz.deathsgun.modmanager.api.mod.Asset;
import xyz.deathsgun.modmanager.api.mod.ModVersion;
import xyz.deathsgun.modmanager.api.mod.VersionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Version {

    @SerializedName("version_number")
    private String version;
    private String changelog;
    @SerializedName("date_published")
    private Date releaseDate;
    @SerializedName("version_type")
    private String type;
    @SerializedName("game_versions")
    private List<String> gameVersions;
    private List<File> files;

    public static ArrayList<ModVersion> toModVersion(ArrayList<Version> versions) {
        ArrayList<ModVersion> result = new ArrayList<>();
        for (Version version : versions) {
            result.add(new ModVersion(version.version, version.changelog, VersionType.fromString(version.version), version.releaseDate, version.gameVersions, File.toFiles(version.files)));
        }
        return result;
    }

    private static class File {
        private String url;
        private String filename;

        public static List<Asset> toFiles(List<File> files) {
            ArrayList<Asset> result = new ArrayList<>();
            for (File file : files) {
                result.add(new Asset(file.url, file.filename));
            }
            return result;
        }
    }

}
