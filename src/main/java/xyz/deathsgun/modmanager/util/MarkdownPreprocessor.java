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

package xyz.deathsgun.modmanager.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownPreprocessor {

    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\*");
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[(.*?)]\\((.*?)\\)");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(.*?)]\\((.*?)\\)");

    public static MutableText[] processText(String text) {
        text = text.replaceAll("<br>", "\n");
        String[] lines = text.split("\n");
        ArrayList<MutableText> texts = new ArrayList<>();
        for (String line : lines) {
            if (IMAGE_PATTERN.matcher(line).find()) {
                continue;
            }
            texts.add(processLine(line));
        }
        return texts.toArray(new MutableText[]{});
    }

    private static MutableText processLine(String text) {
        if (BOLD_PATTERN.matcher(text).find()) {
            return extractBoldText(text);
        }
        if (LINK_PATTERN.matcher(text).find()) {
            return extractLinkText(text);
        }
        return new LiteralText(text);
    }

    private static MutableText extractLinkText(String text) {
        Matcher matcher = LINK_PATTERN.matcher(text);
        if (!matcher.find()) {
            return new LiteralText(text);
        }
        MutableText linkText = new LiteralText(matcher.group(1)).formatted(Formatting.UNDERLINE, Formatting.BLUE);
        return linkText.setStyle(linkText.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, matcher.group(2))));
    }

    private static MutableText extractBoldText(String text) {
        Matcher matcher = BOLD_PATTERN.matcher(text);
        if (!matcher.find()) {
            return new LiteralText(text);
        }
        String boldText = matcher.group(1);
        int begin = text.indexOf(boldText);
        LiteralText preText = new LiteralText(text.substring(0, begin).replaceAll("\\*\\*", ""));
        MutableText matchedText = new LiteralText(boldText).formatted(Formatting.BOLD);
        return preText.append(matchedText).append(extractBoldText(text.substring(begin + 2 + boldText.length())));
    }

}
