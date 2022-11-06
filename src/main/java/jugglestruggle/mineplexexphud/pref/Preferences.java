/*
 * MineplexExpHud: A mod which tracks the current
 * EXP the user has on the Mineplex server.
 * Copyright (C) 2022  JuggleStruggle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 *  <https://www.gnu.org/licenses/>.
 */

package jugglestruggle.mineplexexphud.pref;

import com.google.gson.JsonObject;
import jugglestruggle.mineplexexphud.AbstractExpHud;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.hud.enums.AccuracyMode;
import jugglestruggle.mineplexexphud.hud.enums.ExpTotalsCacheMethodCheck;
import jugglestruggle.mineplexexphud.hud.enums.UpdateMethod;
import jugglestruggle.mineplexexphud.hud.enums.HudPositioning;
import jugglestruggle.mineplexexphud.hud.info.ExpCacheState;
import jugglestruggle.util.JuggleTimeUnit;
import jugglestruggle.util.TimeUnitInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Houses the user preferences that is stored on memory.
 */
public final class Preferences
{
    //
    // General
    //
    public static boolean expUpdateEnabled = true;
    public static AccuracyMode accuracy = AccuracyMode.ACCOUNT_FOR_ALL_LINES;
    public static UpdateMethod updateMethod = UpdateMethod.ON_WORLD_CHANGE;
    public static ExpTotalsCacheMethodCheck expTotalsCacheMethodCheck = ExpTotalsCacheMethodCheck.LISTEN_TO_CHAT_MESSAGE;
    
    /**
     * The amount of milliseconds it takes to perform a chat listen. This by default
     * is set to 3000 milliseconds, or 3 seconds.
     *
     * <p> Only used if {@link #updateMethod} is {@link UpdateMethod#UNTIL_NEXT_MS_UPDATE}
     * or {@link UpdateMethod#ON_WORLD_CHANGE} if {@link #worldChangeUseDelays} is set
     * to {@code true}.
     */
    public static long millisUntilNextExpUpdate = 3000L;
    /**
     * Note: This field must be in minimum of 1 to keep track of some extra features such as
     * expObtainedInSetTime, totalExp and totalLevelsObtainedInSession
     */
    // Not shown anywhere as there's no way to view previous exp caches yet
    public static int maxPrevExpCaches = 1;
    
    
    //
    // Specifics: World Change Update Type
    //
    /**
     * Whenever the user switches worlds, the server will always end up returning the
     * EXP levels as 0 and is not useful/reflective of what we want, so we propose a
     * delay by using {@link #millisUntilNextExpUpdate}.
     */
    public static boolean worldChangeUseDelays = true;
    
    
    //
    // Display & Formatting
    //
    /**
     * Replace the current formatting used from the language and applies the user-formatted style.
     *
     * <p> The format goes as following:
     *
     * <ul>
     * <li> 01 = {@linkplain ExpCacheState#currentExp Current EXP} </li>
     * <li> 02 = {@linkplain ExpCacheState#expUntilNextLevel EXP Until Next Level} </li>
     * <li> 03 = {@linkplain ExpCacheState#currentLevel Current Level} </li>
     * <li> 04 = {@linkplain ExpCacheState#nextLevel Next Level} </li>
     * <li> 05 = {@linkplain AbstractExpHud#percentageUntilNextLevel EXP Percentage Until Next Level}
     *      ({@linkplain ExpCacheState#currentExp Current EXP} divided by
     *      {@linkplain ExpCacheState#expUntilNextLevel EXP Until Next Level}) </li>
     * <li> 06 = {@linkplain UpdateMethod#getUpdateTypeText(AbstractExpHud) Next Update Text} (like "10 seconds" or "World Change") </li>
     * <li> 07 = {@linkplain AbstractExpHud#millisRemainingUntilNextSetTimeClear Total EXP Obtained in Set Time Remaining} (like 10 seconds until reset) </li>
     * <li> 08 = {@linkplain AbstractExpHud#totalLevelsGainedInSession Total Levels Gained in Session} </li>
     * <li> 09 = {@linkplain AbstractExpHud#totalExpGainedInSession Total EXP Gained in Session} </li>
     * <li> 10 = {@linkplain AbstractExpHud#totalLevelsGainedInSetTime Total Levels Gained in Set Time} </li>
     * <li> 11 = {@linkplain AbstractExpHud#totalExpGainedInSetTime Total EXP Gained in Set Time} </li>
     * <li> 12 = {@linkplain AbstractExpHud#totalExpAccumulatedFromLevelCache Guesstimate Accumulated EXP} </li>
     * <li> 13 = {@linkplain AbstractExpHud#percentageUntilLevelOneHundred Guesstimate EXP Percentage To Reach 100%} </li>
     * <li>  =  </li>
     * </ul>
     *
     * <p> It also accepts new lines {@code \n} to create new lines.
     */
    public static String displayFormatting = "";
    // This and the other format preference is not used anywhere other than decimal formatting, so it's only
    // accessible through editing the configuration file
    public static String secondsFormat = "0.00";
    public static String progressPercentageFormat = "0.00";
    public static String progressTo100PercentageFormat = "0.0000";
    /**
     * If the line provided matches the correct one, it will be used to show the progress
     * bar in the filler-background image. Set it to -1 to use none.
     */
    public static int lineToShowProgress = 0;
    // /**
    //  * If the line provided matches the correct one, it will be used to show the progression to
    //  * level 100 as a progress bar in the filler-background image. Set it to -1 to use none.
    //  */
    // public static int lineToShowProgressToLevel100 = 0;
    // /**
    //  * If the line provided matches the correct one, it will be used to show the time remaining
    //  * as a progress bar in the filler-background image. Set it to -1 to use none.
    //  */
    // public static int lineToShowTimeUntilNextUpdate = 0;

    
    //
    // HUD Positioning
    //
    public static HudPositioning hudTextPositioning = HudPositioning.TopLeft;
    public static HudPositioning hudScreenPositioning = HudPositioning.TopLeft;
    // Both fields, the way that they work, function by doing this:
    // x = displayWidth * offsetScreenPercentage
    public static float xOffsetScreenPercentage = 0.0f;
    public static float yOffsetScreenPercentage = 0.0f;
    
    //
    // HUD Edge
    //
    public static float leftBackgroundEdge = 3.0f;
    public static float rightBackgroundEdge = 3.0f;
    public static float topBackgroundEdge = 2.0f;
    public static float bottomBackgroundEdge = 1.0f;
    
    //
    // Color Customization
    //
    public static int backgroundColor = 0x22000000;
    public static int borderColor = 0x22FFFFFF;
    public static int progressBackgroundColor = 0x22AAFF00;
    public static int progressBorderColor = 0x2200FFAA;
    // public static int progressToLevel100BackgroundColor = 0x22AAFF00;
    // public static int progressToLevel100BorderColor = 0x2200FFAA;
    // public static int timeRemainingBackgroundColor = 0x22AAFF00;
    // public static int timeRemainingBorderColor = 0x2200FFAA;
    public static int textColor = 0xFFFFFFFF;
    public static boolean drawTextWithShadow = true;
    public static boolean showBorders = true;
    
    //
    // Stuff to keep on memory, but also saved to disk
    //
    public static boolean showHud = true;
    
    
    //
    // Miscellaneous
    //
    /**
     * You can say that by default it would be an hour and so... yes.
     */
    public static TimeUnitInfo expLevelGainedInSetTime = new TimeUnitInfo(JuggleTimeUnit.HOURS, 1L);
    /**
     * Renders the HUD after all the other HUD elements have been rendered. Otherwise,
     * it will render it before them. (This doesn't seem useful for Forge without mixins unless if
     * one wants to add mixins, which isn't worth just to get it fixed)
     */
    public static boolean postRender = true;
    /**
     * Shows the HUD while the debug HUD is active. Otherwise, it only shows if the
     * debug HUD is not active and the HUD is visible.
     */
    public static boolean showWhileDebugScreenActive = false;
    /**
     * Ignores empty EXP that the server sends whenever it hasn't loaded the user data
     * yet (like seeing Level 0 when one chats too soon when in reality they are higher).
     */
    public static boolean ignoreEmptyExp = true;
    
    /**
     * Reads the Json Object data and applies it to the memory-cached preferences.
     *
     * @param data the json object to read from
     * @return {@code true} if the entries provided were 9; otherwise {@code false}
     */
    public static boolean read(@Nonnull JsonObject data)
    {
        // Removes this check as it might cause previous versions that had less preferences
        // to be considered invalid
        // if (data.entrySet().size() < 31)
        //     return false;

        return Preferences.readSelf(data);
    }
    
    /**
     * An in-between method used to either read the json data or reset the preference
     * should either the preference not be found or the json object is also empty, which
     * the latter only applies if executed under {@link #reset()}.
     *
     * @param data the json object data, can be nullable
     */
    private static boolean readSelf(@Nullable JsonObject data)
    {
        Preferences.expUpdateEnabled = Configuration.get(data, "expUpdateEnabled", true);
        Preferences.accuracy = Configuration.get(data, "accuracy", AccuracyMode.ACCOUNT_FOR_ALL_LINES);
        Preferences.updateMethod = Configuration.get(data, "updateMethod", UpdateMethod.ON_WORLD_CHANGE);
        Preferences.expTotalsCacheMethodCheck = Configuration.get(data, "expTotalsCacheMethodCheck", ExpTotalsCacheMethodCheck.LISTEN_TO_CHAT_MESSAGE);
        Preferences.maxPrevExpCaches = Configuration.get(data, "maxPrevExpCaches", 1, 1, Integer.MAX_VALUE);
    
        Preferences.millisUntilNextExpUpdate = Configuration.get(data, "millisUntilNextExpUpdate", 3000L, 1000L, Long.MAX_VALUE);
        Preferences.worldChangeUseDelays = Configuration.get(data, "worldChangeUseDelays", true);
    
        Preferences.displayFormatting = Configuration.get(data, "displayFormatting", "");
        Preferences.secondsFormat = Configuration.get(data, "secondsFormat", "0.00");
        Preferences.progressPercentageFormat = Configuration.get(data, "progressPercentageFormat", "0.00");
        Preferences.progressTo100PercentageFormat = Configuration.get(data, "progressTo100PercentageFormat", "0.0000");
        Preferences.lineToShowProgress = Configuration.get(data, "lineToShowProgress", 0, -1, Integer.MAX_VALUE);
    
        Preferences.hudTextPositioning = Configuration.get(data, "hudTextPositioning", HudPositioning.TopLeft);
        Preferences.hudScreenPositioning = Configuration.get(data, "hudScreenPositioning", HudPositioning.TopLeft);
        Preferences.xOffsetScreenPercentage = Configuration.get(data, "xOffsetScreenPercentage", 0.0f, -1.0f, 1.0f);
        Preferences.yOffsetScreenPercentage = Configuration.get(data, "yOffsetScreenPercentage", 0.0f, -1.0f, 1.0f);
        
        Preferences.backgroundColor = Configuration.get(data, "backgroundColor", 0x22000000);
        Preferences.borderColor = Configuration.get(data, "borderColor", 0x22FFFFFF);
        Preferences.progressBackgroundColor = Configuration.get(data, "progressBackgroundColor", 0x22AAFF00);
        Preferences.progressBorderColor = Configuration.get(data, "progressBorderColor", 0x2200FFAA);
        Preferences.textColor = Configuration.get(data, "textColor", 0xFFFFFFFF);
        Preferences.drawTextWithShadow = Configuration.get(data, "drawTextWithShadow", true);
        Preferences.showBorders = Configuration.get(data, "showBorders", true);
    
        Preferences.leftBackgroundEdge = Configuration.get(data, "leftBackgroundEdge", 3.0f);
        Preferences.rightBackgroundEdge = Configuration.get(data, "rightBackgroundEdge", 3.0f);
        Preferences.topBackgroundEdge = Configuration.get(data, "topBackgroundEdge", 2.0f);
        Preferences.bottomBackgroundEdge = Configuration.get(data, "bottomBackgroundEdge", 1.0f);
        
        Preferences.showHud = Configuration.get(data, "showHud", true);
    
        Preferences.expLevelGainedInSetTime = Configuration.get(data, "expLevelGainedInSetTime", new TimeUnitInfo(JuggleTimeUnit.HOURS, 1L));
        Preferences.postRender = Configuration.get(data, "postRender", true);
        Preferences.showWhileDebugScreenActive = Configuration.get(data, "showWhileDebugScreenActive", false);
        Preferences.ignoreEmptyExp = Configuration.get(data, "ignoreEmptyExp", true);
    
        return true;
    }
    
    /**
     * Writes the preferences into the Json Object data provided.
     *
     * @param data the json object to put its preferences in
     * @return whether it was successful in writing it; this will always end up returning {@code true}
     */
    public static boolean write(JsonObject data)
    {
        data.addProperty("expUpdateEnabled", Preferences.expUpdateEnabled);
        data.add("accuracy", Preferences.accuracy.write());
        data.add("updateMethod", Preferences.updateMethod.write());
        data.add("expTotalsCacheMethodCheck", Preferences.expTotalsCacheMethodCheck.write());
        data.addProperty("maxPrevExpCaches", Preferences.maxPrevExpCaches);
    
        data.addProperty("millisUntilNextExpUpdate", Preferences.millisUntilNextExpUpdate);
        data.addProperty("worldChangeUseDelays", Preferences.worldChangeUseDelays);
    
        data.addProperty("displayFormatting", Preferences.displayFormatting);
        data.addProperty("secondsFormat", Preferences.secondsFormat);
        data.addProperty("progressPercentageFormat", Preferences.progressPercentageFormat);
        data.addProperty("progressTo100PercentageFormat", Preferences.progressTo100PercentageFormat);
        data.addProperty("lineToShowProgress", Preferences.lineToShowProgress);
    
        data.add("hudTextPositioning", Preferences.hudTextPositioning.write());
        data.add("hudScreenPositioning", Preferences.hudScreenPositioning.write());
        data.addProperty("xOffsetScreenPercentage", Preferences.xOffsetScreenPercentage);
        data.addProperty("yOffsetScreenPercentage", Preferences.yOffsetScreenPercentage);
        
        data.addProperty("backgroundColor", Preferences.backgroundColor);
        data.addProperty("borderColor", Preferences.borderColor);
        data.addProperty("progressBackgroundColor", Preferences.progressBackgroundColor);
        data.addProperty("progressBorderColor", Preferences.progressBorderColor);
        data.addProperty("textColor", Preferences.textColor);
        data.addProperty("drawTextWithShadow", Preferences.drawTextWithShadow);
        data.addProperty("showBorders", Preferences.showBorders);
    
        data.addProperty("leftBackgroundEdge", Preferences.leftBackgroundEdge);
        data.addProperty("rightBackgroundEdge", Preferences.rightBackgroundEdge);
        data.addProperty("topBackgroundEdge", Preferences.topBackgroundEdge);
        data.addProperty("bottomBackgroundEdge", Preferences.bottomBackgroundEdge);
        
        data.addProperty("showHud", Preferences.showHud);
    
        data.add("expLevelGainedInSetTime", Preferences.expLevelGainedInSetTime.write());
        data.addProperty("postRender", Preferences.postRender);
        data.addProperty("showWhileDebugScreenActive", Preferences.showWhileDebugScreenActive);
        data.addProperty("ignoreEmptyExp", Preferences.ignoreEmptyExp);
        
        return true;
    }
    
    /**
     * Resets the user preferences to how it was.
     *
     * <p> This does not write to the file, so if this is your goal, write it in
     * the Forge mod's {@link MineplexExpHudClientForge#writeToFile()} after resetting it from here. </p>
     */
    public static void reset() {
        Preferences.readSelf(null);
    }
    
    private Preferences() {}
}
