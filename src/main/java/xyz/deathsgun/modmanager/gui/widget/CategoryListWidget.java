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

package xyz.deathsgun.modmanager.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import xyz.deathsgun.modmanager.ModManager;
import xyz.deathsgun.modmanager.api.mod.Category;
import xyz.deathsgun.modmanager.gui.ModManagerErrorScreen;
import xyz.deathsgun.modmanager.gui.ModsOverviewScreen;
import xyz.deathsgun.modmanager.gui.widget.better.BetterListWidget;

import java.util.ArrayList;

public class CategoryListWidget extends BetterListWidget<CategoryListEntry> {

    private ArrayList<Category> categories;

    public CategoryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, ModsOverviewScreen parentScreen) {
        super(client, width, height, top, bottom, itemHeight, parentScreen);
        setScrollAmount(0.0 * Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)));
    }

    @Override
    public void init() {
        try {
            this.categories = new ArrayList<>();
            ModManager.getModProvider().getCategories().forEach(category -> this.addEntry(new CategoryListEntry(this, category)));
            if (parent.getEntry(this) != null) {
                setSelected(parent.getEntry(this));
                return;
            }
            setSelected(this.getEntry(0));
        } catch (Exception e) {
            client.openScreen(new ModManagerErrorScreen((Screen) parent, e));
            e.printStackTrace();
        }
    }

    @Override
    public void setSelected(CategoryListEntry entry) {
        super.setSelected(entry);
        selectedId = entry.getCategory().id();
        parent.updateSelectedEntry(this, getSelectedOrNull());
    }

    @Override
    public int addEntry(CategoryListEntry entry) {
        if (this.categories.contains(entry.getCategory())) {
            return 0;
        }
        this.categories.add(entry.getCategory());
        return super.addEntry(entry);
    }

    @Override
    protected boolean removeEntry(CategoryListEntry entry) {
        this.categories.remove(entry.getCategory());
        return super.removeEntry(entry);
    }

    @Override
    protected CategoryListEntry remove(int index) {
        this.categories.remove(getEntry(index).getCategory());
        return super.remove(index);
    }
}
