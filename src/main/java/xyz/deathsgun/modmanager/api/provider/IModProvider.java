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

package xyz.deathsgun.modmanager.api.provider;

import xyz.deathsgun.modmanager.api.mod.Category;
import xyz.deathsgun.modmanager.api.mod.DetailedMod;
import xyz.deathsgun.modmanager.api.mod.ModVersion;
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;

import java.util.List;

/**
 * Provides mods to the ModMenu graphical interface
 *
 * @since 1.0.0
 */
public interface IModProvider {

    /**
     * Name of the provider. This will be shown
     * in the GUI
     *
     * @return returns a user friendly name of the mod provider implementation
     */
    String getName();

    /**
     * Returns a list of all possible mod categories also with an translatable text
     *
     * @return a list of all mod categories
     */
    List<Category> getCategories() throws Exception;

    /**
     * Returns a limited number of {@link SummarizedMod}'s sorted
     * in a given way.
     *
     * @param sorting the requested sorting of the mods
     * @param page    the requested from the UI starting at 1
     * @param limit   to not overfill the ui and for shorter loading times the amount of returned mods needs to limited
     * @return a list of sorted mods
     */
    List<SummarizedMod> getMods(Sorting sorting, int page, int limit) throws Exception;

    /**
     * Returns a limited number of {@link SummarizedMod}'s from the specified category
     *
     * @param category the category of all the mods
     * @param page     the requested from the UI starting at 1
     * @param limit    to not overfill the ui and for shorter loading times the amount of returned mods needs to limited
     * @return a list of sorted mods
     */
    List<SummarizedMod> getMods(Category category, int page, int limit) throws Exception;

    /**
     * Returns a limited number of {@link SummarizedMod}'s from a given search.
     *
     * @param query the search string
     * @param page  the current requested page starts at 0
     * @param limit the amount of mods to return
     * @return a list of mods matching the search term
     */
    List<SummarizedMod> getMods(String query, int page, int limit) throws Exception;

    /**
     * Returns a more detailed representation of the mod de
     *
     * @param id the id from {@link SummarizedMod#id()} which is used to
     * @return a detailed representation of {@link SummarizedMod}
     */
    DetailedMod getMod(String id) throws Exception;

    List<ModVersion> getVersionsForMod(String id) throws Exception;


}
