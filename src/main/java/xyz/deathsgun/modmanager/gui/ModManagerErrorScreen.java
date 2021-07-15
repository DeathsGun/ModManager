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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.exception.ExceptionUtils;
import xyz.deathsgun.modmanager.gui.widget.DescriptionWidget;

import java.util.Objects;

public class ModManagerErrorScreen extends Screen {

    private final Exception exception;
    private final Screen parentScreen;
    private DescriptionWidget descriptionWidget;

    public ModManagerErrorScreen(Screen screen, Exception exception) {
        super(new TranslatableText("modmanager.error.title"));
        this.exception = exception;
        this.parentScreen = screen;
    }

    @Override
    protected void init() {
        super.init();
        String error = ExceptionUtils.getStackTrace(exception);
        error = error.replaceAll("\t", "");
        descriptionWidget = this.addSelectableChild(new DescriptionWidget(client, (int) (width * 0.8), height, (int) (height * 0.2), this.height - 36, 9, error));
        descriptionWidget.setLeftPos((int) (this.width * 0.1));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.onClose()));
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(client).openScreen(parentScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.descriptionWidget.render(matrices, mouseX, mouseY, delta);
    }
}
