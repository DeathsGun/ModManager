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
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class DescriptionListEntry extends EntryListWidget.Entry<DescriptionListEntry> {

    private final DescriptionWidget widget;
    private final OrderedText orderedText;
    private final TextRenderer textRenderer;
    private Text text;
    private int x = 0;

    public DescriptionListEntry(DescriptionWidget widget, Text text) {
        this(widget, text.asOrderedText());
        this.text = text;
    }

    public DescriptionListEntry(DescriptionWidget widget, OrderedText orderedText) {
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.widget = widget;
        this.orderedText = orderedText;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (y >= widget.getBottom() - textRenderer.fontHeight + 2) {
            return;
        }
        this.x = x;
        textRenderer.draw(matrices, orderedText, x, y, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (text == null) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (isMouseOver(mouseX, mouseY)) {
            ClickEvent event = text.getStyle().getClickEvent();
            if (event == null || event.getAction() != ClickEvent.Action.OPEN_URL) {
                return super.mouseClicked(mouseX, mouseY, button);
            }
            Util.getOperatingSystem().open(event.getValue());
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) && x + textRenderer.getWidth(text) >= mouseX;
    }
}
