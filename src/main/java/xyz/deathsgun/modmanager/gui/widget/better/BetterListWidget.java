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

package xyz.deathsgun.modmanager.gui.widget.better;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Using ModMenu's implementation because the implementation of
 * Mojang's is broken. {@link com.terraformersmc.modmenu.gui.ModsScreen}.
 * All credits for this code go to the Terraformers
 */
public abstract class BetterListWidget<E extends BetterListWidget.BetterListEntry<E>> extends AlwaysSelectedEntryListWidget<E> {

    protected final IListScreen parent;
    protected String selectedId = null;
    private boolean scrolling;

    public BetterListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, IListScreen parent) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
    }

    public abstract void init();

    @Override
    protected boolean isFocused() {
        return parent.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable E entry) {
        super.setSelected(entry);
        if (entry != null) {
            selectedId = entry.id();
        } else {
            selectedId = null;
        }
        parent.updateSelectedEntry(this, getSelectedOrNull());
    }

    @Override
    protected boolean isSelectedEntry(int index) {
        return Objects.equals(getEntry(index).id(), selectedId);
    }

    @Override
    protected int addEntry(E entry) {
        int i = super.addEntry(entry);
        if (entry.id().equals(selectedId)) {
            setSelected(entry);
        }
        return i;
    }

    @Override
    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int itemCount = this.getEntryCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (int index = 0; index < itemCount; ++index) {
            int entryTop = this.getRowTop(index) + 2;
            int entryBottom = this.getRowTop(index) + this.itemHeight;
            if (entryBottom >= this.top && entryTop <= this.bottom) {
                int entryHeight = this.itemHeight - 4;
                BetterListEntry<E> entry = this.getEntry(index);
                int rowWidth = this.getRowWidth();
                int entryLeft;
                if (this.isSelectedEntry(index)) {
                    entryLeft = getRowLeft() - 2;
                    int selectionRight = x + rowWidth + 2;
                    RenderSystem.disableTexture();
                    float float_2 = this.isFocused() ? 1.0F : 0.5F;
                    RenderSystem.setShader(GameRenderer::getPositionShader);
                    RenderSystem.setShaderColor(float_2, float_2, float_2, 1.0F);
                    Matrix4f matrix = matrices.peek().getModel();
                    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                    buffer.vertex(matrix, entryLeft, entryTop + entryHeight + 2, 0.0F).next();
                    buffer.vertex(matrix, selectionRight, entryTop + entryHeight + 2, 0.0F).next();
                    buffer.vertex(matrix, selectionRight, entryTop - 2, 0.0F).next();
                    buffer.vertex(matrix, entryLeft, entryTop - 2, 0.0F).next();
                    tessellator.draw();
                    RenderSystem.setShader(GameRenderer::getPositionShader);
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                    buffer.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, 0.0F).next();
                    buffer.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, 0.0F).next();
                    buffer.vertex(matrix, selectionRight - 1, entryTop - 1, 0.0F).next();
                    buffer.vertex(matrix, entryLeft + 1, entryTop - 1, 0.0F).next();
                    tessellator.draw();
                    RenderSystem.enableTexture();
                }

                entryLeft = this.getRowLeft();
                entry.render(matrices, index, entryTop, entryLeft, rowWidth, entryHeight, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPos(mouseX, mouseY), entry), delta);
            }
        }
    }

    @Override
    protected void updateScrollingState(double double_1, double double_2, int int_1) {
        super.updateScrollingState(double_1, double_2, int_1);
        this.scrolling = int_1 == 0 && double_1 >= (double) this.getScrollbarPositionX() && double_1 < (double) (this.getScrollbarPositionX() + 6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.updateScrollingState(mouseX, mouseY, mouseButton);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            BetterListEntry<E> entry = this.getEntryAtPos(mouseX, mouseY);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, mouseButton)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            } else if (mouseButton == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return this.scrolling;
        }
    }

    public final BetterListEntry<E> getEntryAtPos(double x, double y) {
        int int_5 = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int index = int_5 / this.itemHeight;
        return x < (double) this.getScrollbarPositionX() && x >= (double) getRowLeft() && x <= (double) (getRowLeft() + getRowWidth()) && index >= 0 && int_5 >= 0 && index < this.getEntryCount() ? this.children().get(index) : null;
    }

    @Override
    public int getRowWidth() {
        return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
    }

    @Override
    public int getRowLeft() {
        return left + 6;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.left + this.width - 6;
    }

    @Override
    protected int getMaxPosition() {
        return super.getMaxPosition() + 4;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    public boolean isSelectedEntry(BetterListEntry<E> entry) {
        return Objects.equals(selectedId, entry.id());
    }


    public static abstract class BetterListEntry<E extends BetterListWidget.BetterListEntry<E>> extends AlwaysSelectedEntryListWidget.Entry<E> {

        protected final BetterListWidget<E> list;
        private final Text title;

        public BetterListEntry(BetterListWidget<E> list, Text title) {
            this.list = list;
            this.title = title;
        }

        public abstract String id();

        @Override
        public Text method_37006() {
            return title;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean mouseClicked(double v, double v1, int i) {
            list.setSelected((E) this);
            return true;
        }
    }
}
