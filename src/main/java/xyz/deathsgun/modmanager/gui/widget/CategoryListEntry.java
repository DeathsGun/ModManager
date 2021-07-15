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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import xyz.deathsgun.modmanager.api.mod.Category;
import xyz.deathsgun.modmanager.gui.widget.better.BetterListWidget;

public class CategoryListEntry extends BetterListWidget.BetterListEntry<CategoryListEntry> {

    private final Category category;
    private final MinecraftClient client = MinecraftClient.getInstance();

    public CategoryListEntry(CategoryListWidget list, Category category) {
        super(list, category.text());
        this.category = category;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        TextRenderer font = this.client.textRenderer;
        Text text = category.text();
        if (this.list.isSelectedEntry(this)) {
            text = text.getWithStyle(text.getStyle().withBold(true)).get(0);
        }
        OrderedText trimmedText = Language.getInstance().reorder(font.trimToWidth(text, rowWidth - 10));
        font.draw(matrices, trimmedText, x + 3, y + 1, 0xFFFFFF);
    }

    @Override
    public String id() {
        return category.id();
    }

    @Override
    public Text method_37006() {
        return category.text();
    }

    public Category getCategory() {
        return category;
    }
}
