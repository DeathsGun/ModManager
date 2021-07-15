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

package xyz.deathsgun.modmanager.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.deathsgun.modmanager.gui.widget.CategoryListEntry;
import xyz.deathsgun.modmanager.gui.widget.CategoryListWidget;
import xyz.deathsgun.modmanager.gui.widget.ModListEntry;
import xyz.deathsgun.modmanager.gui.widget.ModListWidget;
import xyz.deathsgun.modmanager.gui.widget.better.IListScreen;

import java.util.Objects;

public class ModsOverviewScreen extends Screen implements IListScreen {

    private final Screen previousScreen;
    private ModListWidget modListWidget;
    private ModListEntry selectedMod;
    private CategoryListWidget categoryListWidget;
    private CategoryListEntry selectedCategory;
    private int paneWidth;
    private int rightPaneX;

    public ModsOverviewScreen(Screen previousScreen) {
        super(new TranslatableText("modmanager.title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(true);
        paneWidth = this.width / 4;
        rightPaneX = this.paneWidth + 10;
        this.categoryListWidget = this.addSelectableChild(new CategoryListWidget(this.client, paneWidth, this.height, 30, this.height - 10, 14, this));
        this.categoryListWidget.setLeftPos(0);
        int modListWidth = this.width - paneWidth - 20;
        this.modListWidget = this.addSelectableChild(new ModListWidget(this.client, modListWidth, this.height, 30, this.height - 40, 36, this));
        this.modListWidget.setLeftPos(rightPaneX);
        this.categoryListWidget.init();
        this.modListWidget.init();
        this.addDrawableChild(new ButtonWidget(rightPaneX, this.height - 30, modListWidth / 2 - 10, 20,
                new TranslatableText("modmanager.page.previous"), button -> this.modListWidget.showPreviousPage()));
        this.addDrawableChild(new ButtonWidget(rightPaneX + modListWidth / 2, this.height - 30, modListWidth / 2, 20,
                new TranslatableText("modmanager.page.next"), button -> this.modListWidget.showNextPage()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        TextRenderer font = Objects.requireNonNull(client).textRenderer;
        font.draw(matrices, new TranslatableText("modmanager.categories"), 5, 29 - font.fontHeight, 0xFFFFFF);
        this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
        this.modListWidget.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
    }

    @Override
    public void onClose() {
        super.onClose();
        this.modListWidget.close();
        Objects.requireNonNull(this.client).openScreen(this.previousScreen);
    }

    @Override
    public <E> void updateSelectedEntry(Object widget, E entry) {
        if (widget == this.categoryListWidget) {
            if (entry != null) {
                this.selectedCategory = (CategoryListEntry) entry;
                this.modListWidget.setCategory(selectedCategory.getCategory(), false);
            }
        }
        if (widget == this.modListWidget) {
            if (entry != null) {
                if (this.selectedMod == entry) {
                    Objects.requireNonNull(this.client).openScreen(new ModDetailScreen(this, ((ModListEntry) entry).getMod()));
                    return;
                }
                this.selectedMod = (ModListEntry) entry;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getEntry(Object widget) {
        if (widget == this.categoryListWidget) {
            return (E) this.selectedCategory;
        }
        return null;
    }
}
