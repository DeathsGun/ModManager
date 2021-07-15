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
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import xyz.deathsgun.modmanager.util.MarkdownPreprocessor;

import java.util.List;

public class DescriptionWidget extends EntryListWidget<DescriptionListEntry> {

    private final String text;

    public DescriptionWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, String text) {
        super(client, width, height, top, bottom, itemHeight);
        this.text = text;
        init();
    }

    private void init() {
        MutableText[] lines = MarkdownPreprocessor.processText(text);
        TextRenderer textRenderer = client.textRenderer;
        for (MutableText line : lines) {
            if (textRenderer.getWidth(line) - 10 >= width) {
                List<OrderedText> texts = textRenderer.wrapLines(line, width - 10);
                for (OrderedText wrappedLine : texts) {
                    addEntry(new DescriptionListEntry(this, wrappedLine));
                }
                continue;
            }
            addEntry(new DescriptionListEntry(this, line));
        }
        addEntry(new DescriptionListEntry(this, new LiteralText("")));
    }

    @Override
    public DescriptionListEntry getSelectedOrNull() {
        return null;
    }

    @Override
    public int getRowWidth() {
        return this.width - 10;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 6 + left;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        // Better not read this thing
    }

    public int getBottom() {
        return bottom;
    }
}
