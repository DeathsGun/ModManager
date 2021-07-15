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

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.util.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;
import xyz.deathsgun.modmanager.ModManager;
import xyz.deathsgun.modmanager.api.mod.ModState;
import xyz.deathsgun.modmanager.api.mod.SummarizedMod;
import xyz.deathsgun.modmanager.gui.widget.better.BetterListWidget;

import java.util.concurrent.CompletableFuture;

public class ModListEntry extends BetterListWidget.BetterListEntry<ModListEntry> {

    public static final Identifier UNKNOWN_ICON = new Identifier("textures/misc/unknown_pack.png");
    public static final Identifier LOADING_ICON = new Identifier("modmanager", "textures/gui/loading.png");

    private final SummarizedMod mod;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private ModState modState = ModState.CHECKING;

    public ModListEntry(ModListWidget list, @NotNull SummarizedMod mod) {
        super(list, new LiteralText(mod.name()));
        this.mod = mod;
        CompletableFuture.runAsync(() -> this.modState = ModManager.getState(this.mod));
    }

    @Override
    public String id() {
        return mod.id();
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int iconSize = 32;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindIconTexture();
        RenderSystem.enableBlend();
        DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
        RenderSystem.disableBlend();
        Text name = new LiteralText(mod.name());
        StringVisitable trimmedName = name;
        int maxNameWidth = entryWidth - iconSize - 3;
        TextRenderer font = this.client.textRenderer;

        int primaryColor = 0xFFFFFF;
        int secondaryColor = 0xFFFFFF;
        OrderedText badgeText = null;
        if (modState == ModState.INSTALLED) {
            primaryColor = 0xff0e2a55;
            secondaryColor = 0xff2b4b7c;
            badgeText = new TranslatableText("modmanager.badge.installed").asOrderedText();
            maxNameWidth -= font.getWidth(badgeText) + 6;
        } else if (modState == ModState.OUTDATED) {
            primaryColor = 0xff530C17;
            secondaryColor = 0xff841426;
            badgeText = new TranslatableText("modmanager.badge.outdated").asOrderedText();
            maxNameWidth -= font.getWidth(badgeText) + 6;
        }

        int textWidth = font.getWidth(name);
        if (textWidth > maxNameWidth) {
            StringVisitable ellipsis = StringVisitable.plain("...");
            trimmedName = StringVisitable.concat(font.trimToWidth(name, maxNameWidth - font.getWidth(ellipsis)), ellipsis);
        }
        font.draw(matrices, Language.getInstance().reorder(trimmedName), x + iconSize + 3, y + 1, 0xFFFFFF);
        if (badgeText != null) {
            DrawingUtil.drawBadge(matrices, x + iconSize + 3 + textWidth + 3, y + 1, font.getWidth(badgeText) + 6, badgeText, secondaryColor, primaryColor, 0xFFFFFF);
        }

        DrawingUtil.drawWrappedString(matrices, mod.description(), (x + iconSize + 3 + 4), (y + client.textRenderer.fontHeight + 4), entryWidth - iconSize - 7, 2, 0x808080);
    }


    private void bindIconTexture() {
        if (ModManager.getIconDownloader().isErrored(mod.id())) {
            RenderSystem.setShaderTexture(0, UNKNOWN_ICON);
            return;
        }
        Identifier icon = ModManager.getIconDownloader().getIcon(mod.id());
        if (icon == null) {
            if (ModManager.getIconDownloader().isLoading(mod.id())) {
                icon = LOADING_ICON;
            } else {
                ModManager.getIconDownloader().addMod(mod);
                return;
            }
        }
        RenderSystem.setShaderTexture(0, icon);
    }

    public SummarizedMod getMod() {
        return mod;
    }
}
