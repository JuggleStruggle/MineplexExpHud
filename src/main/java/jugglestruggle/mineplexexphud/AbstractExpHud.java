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

package jugglestruggle.mineplexexphud;

import jugglestruggle.mineplexexphud.hud.RenderContext;
import jugglestruggle.mineplexexphud.hud.enums.AccuracyMode;
import jugglestruggle.mineplexexphud.hud.enums.ExpTotalsCacheMethodCheck;
import jugglestruggle.mineplexexphud.hud.enums.UpdateMethod;
import jugglestruggle.mineplexexphud.hud.info.ExpCacheState;
import jugglestruggle.mineplexexphud.hud.info.LineInfoCache;
import jugglestruggle.mineplexexphud.pref.Preferences;
import jugglestruggle.util.ColorUtility;
import jugglestruggle.util.JuggleTimeUnit;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;
import static jugglestruggle.mineplexexphud.MineplexExpHudClient.getLang;

public abstract class AbstractExpHud
{
    //
    // HUD Fields
    // Needed for instances where it has to be cached and retrieved
    //
    /**
     * Only has an effect if {@link Preferences#updateMethod} is {@link UpdateMethod#UNTIL_NEXT_MS_UPDATE}
     * or {@link UpdateMethod#ON_WORLD_CHANGE} if {@link Preferences#worldChangeUseDelays} is set
     * to {@code true}.
     */
    public long activeMillisUntilNextExpUpdate = 0L;
    /**
     * Only has an effect if {@link Preferences#expLevelGainedInSetTime} is in use.
     */
    long millisRemainingUntilNextSetTimeClear = 0L;
    public boolean worldChangeInitiatedDelay = false;
    
    //
    // EXP: What the /xp Command Gives
    //
    // These fields are used to know the current state of the /xp command
    public ExpCacheState expStatus;
    public ExpCacheState expStatusOnWatch;
    public Deque<ExpCacheState> prevExpStatuses;
    
    /**
     * This field is only a percentage by dividing currentExp over expUntilNextLevel;
     * only used in displaying percentage text or the experience bar.
     */
    public float percentageUntilNextLevel;
    
    //
    // These fields just make a guess and/or help with the user knowing how
    // much is this or that, which is useful for extra info
    //
    
    /**
     * Treat this as if it was like, the amount of EXP the player has obtained in said hour
     * but instead of exactly being in "said hour", it's instead by what it was defined in
     * {@link Preferences#expLevelGainedInSetTime}.
     */
    long totalExpGainedInSetTime;
    // long[] totalExpGainedInSetTimeHistory;
    /**
     * Same with {@link #totalExpGainedInSetTime} with regards to accuracy but keeps track
     * of the amount of levels the user had. It is unknown how this will be exactly handled yet.
     */
    int totalLevelsGainedInSetTime;
    // int[] totalLevelsGainedInSetTimeHistory;
    /**
     * This is not exactly accurate, but it's the EXP that the user has accumulated over
     * the playing session and might require extra regexes should this require reading
     * the XP chat line when finishing a game instance on the server (or XP/EXP rewards
     * for that matter).
     */
    long totalExpGainedInSession;
    /**
     * Same with {@link #totalExpGainedInSession} with regards to accuracy but keeps track
     * of the amount of levels the user had. It is unknown how this will be exactly handled yet.
     */
    int totalLevelsGainedInSession;
    
    /**
     * This field is not in the /xp command output; it was only added to avoid having to
     * repeatedly call a {@link ExpStatics#accumulateExpFromCurrentLevel(int)} a bunch of times.
     */
    long totalExpAccumulatedFromLevelCache;
    /**
     * A version that adds {@link #totalExpAccumulatedFromLevelCache} with the
     * {@linkplain ExpCacheState#currentExp current exp} to help determine the total count.
     */
    long totalExpAccumulated;
    /**
     * This field is only a percentage by dividing {@linkplain #totalExpAccumulatedFromLevelCache
     * the total user's exp} + {@linkplain ExpCacheState#currentExp the current EXP} (see
     * {@link #totalExpAccumulated} as it is used instead) over
     * {@linkplain ExpStatics#LEVEL_100_TOTAL_EXP all levels' EXP combined} (which is a constant,
     * to avoid future problems).
     */
    protected float percentageUntilLevelOneHundred;
    
    
    
    //
    // Text caching for render
    //
    protected LineInfoCache[] lines;
    protected String cachedFormatToUse;
    
    
    
    //
    // EXP Swallow Fields
    // Stuff to keep track during the EXP chat listener
    //
    // System.getMillis() + 1000 (1 second) to wait until it's not "swallowing" the chat.
    protected long millisUntilExpMessageSwallowDone = 0L;
    /**
     * If true, this swallows the EXP message lines shown to the player and
     * attempts to parse it based on the current given information and updates
     * the current HUD's information.
     */
    protected boolean waitingForExpMessage = false;
    
    /**
     * Only used if {@link Preferences#accuracy} is set to
     * {@link AccuracyMode#ACCOUNT_FOR_ALL_LINES}.
     *
     * <p> Used to cache messages while waiting for the EXP message to show up.
     * There are so many false positives where if this field wasn't a thing,
     * it would have never even shown you anything non-related to the EXP
     * you have.
     *
     * <p> So this is why it caches them if it assumes that either is the
     * Mineplex EXP formatting or a false-positive version. This is then
     * either sent to the responsible chat history to show it to the user
     * or deletes it if it was successful in parsing the EXP format.
     *
     * <p><b>Note: </b>The Object here <b>has</b> to be treated like the base
     * text component (or specifically the Literal Text) on mod loader version
     * like Forge or Fabric.</p>
     */
    protected List<Object> cachedMessagesToPrint;
    
    /**
     * We need to get at least 9 lines to satisfy our EXP chat miniscreen, but
     * the maximum that we can get, in Java fashion, is 8.
     *
     * <p> For the most part, it is only used if {@link Preferences#accuracy} is set to
     * {@link AccuracyMode#ACCOUNT_FOR_ALL_LINES}.
     *
     * <p> {@link AccuracyMode#ONLY_ON_WHAT_MATTERS} is used, but only to keep track of
     * "necessary progress".
     */
    protected byte currentLine;

    public DecimalFormat secondsFormat;
    public DecimalFormat progressPercentageFormat;
    public DecimalFormat progressTo100PercentageFormat;
    
    
    protected float xPosition;
    protected float yPosition;
    protected float wSize;
    protected float hSize;
    
    
    
    
    protected AbstractExpHud()
    {
        this.cachedMessagesToPrint = new ArrayList<>();
        this.expStatus = new ExpCacheState();
        
        this.wSize = 20.0f; this.hSize = 20.0f;
    }
    
    public void updateFromPreferences()
    {
        this.secondsFormat = new DecimalFormat(Preferences.secondsFormat);
        this.progressPercentageFormat = new DecimalFormat(Preferences.progressPercentageFormat);
        this.progressTo100PercentageFormat = new DecimalFormat(Preferences.progressTo100PercentageFormat);
    
        // TODO: Account for maxPrevExpCaches difference and keep existing prevExpStatuses that can fit in this new entry
        // TODO: None of this matters at the moment as that preference is not exposed yet
//        if (this.prevExpStatuses == null || this.prevExpStatuses.size() !=)
        this.prevExpStatuses = new ArrayDeque<>(Preferences.maxPrevExpCaches);
    
        if (MineplexExpHudClient.getInstance().tempExpCacheReadFromConfig != null)
        {
            this.expStatus = new ExpCacheState(MineplexExpHudClient.getInstance().tempExpCacheReadFromConfig);
            MineplexExpHudClient.getInstance().tempExpCacheReadFromConfig = null;
        }
        
        this.setPositionByScreenAreaAndOffset();
    }
    
    
    
    
    
    //
    // Abstract Methods
    //
    protected abstract boolean avoidExecuting();
    protected abstract int getTextRendererHeight();
    protected abstract int getTextRendererStringWidth(String text);
    protected abstract void sendChatMessage(String text);
    protected abstract void printMessage(Object text);
    
    
    
    
    
    
    //
    // The Eventual Release! (Events-like methods)
    //
    public void render(RenderContext ctx, float delta)
    {
        if (this.lines == null)
            this.updateDisplayCacheInfo();
        
        this.render(this.lines, ctx, delta);
    }
    // Separated method to allow DisplayFormatPreferencesScreen and HudEdgesEditorScreen
    // to render its previews
    public void render(LineInfoCache[] lines, RenderContext ctx, float delta)
    {
        boolean higherThan0 = this.percentageUntilNextLevel > 0.0f;
        boolean lowerThan1  = this.percentageUntilNextLevel < 1.0f;
        
        ctx.glColor(1.0F, 1.0F, 1.0F, 1.0F);
    
        for (int i = 0; i < lines.length; ++i)
        {
            LineInfoCache line = lines[i];
    
            if (i == Preferences.lineToShowProgress)
            {
                float w = line.right - line.left + 2.0f;
    
                final int top = (int)line.top - 1;
                final int height = (int)(line.bottom - line.top) + 2;
                final int wInt = (int)(w * this.percentageUntilNextLevel);
    
                if (higherThan0)
                {
                    if (lowerThan1)
                        ctx.createScissor((int)line.left - 1, top, wInt, height);
    
                    this.renderOnlyTextBackgroundS
                    (
                        ctx, delta, 1.0f,
                        
                        line.left, line.top,
                        line.right, line.bottom,
                        
                        Preferences.progressBackgroundColor,
                        Preferences.showBorders ? Preferences.progressBorderColor : 0
                    );
    
                    if (lowerThan1)
                        ctx.removeScissor();
                }
                if (lowerThan1)
                {
                    if (higherThan0)
                        ctx.createScissor((int)line.left - 1 + wInt, top,(int)w - wInt, height);
    
                    this.renderOnlyTextBackgroundS
                    (
                        ctx, delta, 1.0f,
                        
                        line.left, line.top,
                        line.right, line.bottom,
                        
                        Preferences.backgroundColor,
                        Preferences.showBorders ? Preferences.borderColor : 0
                    );
    
                    if (higherThan0)
                        ctx.removeScissor();
                }
            }
            else
            {
                this.renderOnlyTextBackgroundS
                (
                    ctx, delta, 1.0f,
                    
                    line.left, line.top,
                    line.right, line.bottom,
        
                    Preferences.backgroundColor,
                    Preferences.showBorders ? Preferences.borderColor : 0
                );
            }
    
            this.renderOnlyTextS(ctx, delta, line.text, 1.0f, line.textX, line.textY, Preferences.textColor);
        }
    
        ctx.glColor(1.0f, 1.0f, 1.0f, 1.0f);
        ctx.enableTexture();
    }
    
    public void tick()
    {
        final long sysTime = System.currentTimeMillis();
    
        if (this.waitingForExpMessage && this.millisUntilExpMessageSwallowDone <= sysTime)
        {
            this.waitingForExpMessage = false;
            this.millisUntilExpMessageSwallowDone = 0L;
        
            this.endPerformingExpUpdate(false);
        }
    
        if (Preferences.expUpdateEnabled)
        {
            switch (Preferences.updateMethod)
            {
                case ON_WORLD_CHANGE:
                {
                    if (Preferences.worldChangeUseDelays && this.worldChangeInitiatedDelay && this.activeMillisUntilNextExpUpdate <= sysTime) {
                        this.performExpUpdate();
                    }
            
                    break;
                }
                case UNTIL_NEXT_MS_UPDATE:
                {
                    if (this.activeMillisUntilNextExpUpdate <= sysTime)
                    {
                        this.delayActiveMillisUntilNextExpUpdate();
                        this.performExpUpdate();
                    }
            
                    break;
                }
                default:
                    break;
            }
    
            if (this.millisRemainingUntilNextSetTimeClear <= sysTime)
            {
                this.millisRemainingUntilNextSetTimeClear = sysTime + Preferences.expLevelGainedInSetTime.getResult();
    
                this.totalExpGainedInSetTime = 0L;
                this.totalLevelsGainedInSetTime = 0;
            }
        }
    
        if (Preferences.updateMethod.requiresConstantTextCacheUpdate(this))
            this.updateDisplayCacheInfo();
    }
    
    // Returns: Whether the chat received event should be cancelled
    public boolean onChatReceived(Object msg, String formatted, String unformatted)
    {
        if (this.waitingForExpMessage)
        {
            // TODO FUTURE: Have regexes function in Formatted Mode and replace & remove unformatted with Formatted Text as the sole reason to avoid unformatted bypasses.
            
            switch (Preferences.accuracy)
            {
                case ACCOUNT_FOR_ALL_LINES:
                {
                    if (this.findAndMatch(this.currentLine, unformatted))
                    {
                        if (this.currentLine < 8)
                        {
                            ++this.currentLine;
                            
                            // Note: There will be false-positives during the readout of the chat,
                            // so try keeping them until we either end performing the EXP update check
                            // or have it "spew" out
                            this.cachedMessagesToPrint.add(msg);
                        }
                        else if (this.currentLine == 8)
                        {
                            this.endPerformingExpUpdate(true);
                        }
                        
                        return true;
                    }
                    else
                    {
                        // todo maybe? account for tries left whenever it fails to get
                        // (provided that the currentLine is more than 0)
                        
                        // if the current line is 0, ignore this as a problem
                        // If the current line is more than 0, retry again and spew out the cached messages for printing
                        if (this.currentLine > 0)
                        {
                            this.currentLine = 0;
                            this.spewOutCachedMessages();
                        }
                    }
                    
                    break;
                }
                case ONLY_ON_WHAT_MATTERS: // Focus on what we want
                {
                    boolean matchFound;
    
                    if (matchFound = this.findAndMatch(2, unformatted))
                        this.currentLine = 1;
                    else if (matchFound = this.findAndMatch(4, unformatted))
                    {
                        if (this.currentLine == 1)
                            this.endPerformingExpUpdate(true);
                    }
    
                    if (matchFound)
                        return false;
                    else
                        break;
                }
            }
        }
        
        
        if (!Preferences.expUpdateEnabled || Preferences.expTotalsCacheMethodCheck != ExpTotalsCacheMethodCheck.LISTEN_TO_CHAT_MESSAGE)
            return false;
        
        // Find any sign of game results (or EXP pattern matching for that matter)
        for (Pattern p : ExpStatics.EXP_ADDITION_PATTERN)
        {
            Matcher m = p.matcher(formatted);
            
            if (m.find() && m.groupCount() == 1)
            {
                try
                {
                    long exp = Long.parseLong(m.group(1));
                    
                    this.totalExpGainedInSetTime += exp;
                    this.totalExpGainedInSession += exp;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                if (!Preferences.updateMethod.requiresConstantTextCacheUpdate(this))
                    this.updateDisplayCacheInfo();
                
                break;
            }
        }
        
        return false;
    }
    public void onWorldLoad()
    {
        if (!Preferences.expUpdateEnabled || Preferences.updateMethod != UpdateMethod.ON_WORLD_CHANGE)
            return;
        
        if (Preferences.worldChangeUseDelays)
        {
            this.delayActiveMillisUntilNextExpUpdate();
            this.worldChangeInitiatedDelay = true;
        }
        else
            this.performExpUpdate();
    }
    
    public void delayActiveMillisUntilNextExpUpdate() {
        this.activeMillisUntilNextExpUpdate = System.currentTimeMillis() + Preferences.millisUntilNextExpUpdate;
    }
    
    
    
    
    
    
    //
    // Getting & Setting Positions and Sizes
    //
    public float getX() {
        return this.xPosition;
    }
    public float getY() {
        return this.yPosition;
    }
    public float getWidth() {
        return this.wSize;
    }
    public float getHeight() {
        return this.hSize;
    }
    public AbstractExpHud setPos(float x, float y, boolean update)
    {
        this.xPosition = x;
        this.yPosition = y;
    
        if (update)
            this.updateLineRenderPosBox();
    
        return this;
    }
    public AbstractExpHud setSize(float w, float h, boolean update)
    {
        this.wSize = w;
        this.hSize = h;
        
        if (update)
            this.updateLineRenderPosBox();
        
        return this;
    }
    public void setPositionByScreenArea()
    {
        float[] xy = this.getPositionByScreenArea();
        this.setPos(xy[0], xy[1], true);
    }
    public void setPositionByScreenAreaAndOffset()
    {
        int[] scr = MineplexExpHudClient.getRenderContext().getSCR();
        float[] xy = this.getPositionByScreenArea();
    
        xy[0] += (float)scr[0] * Preferences.xOffsetScreenPercentage;
        xy[1] += (float)scr[1] * Preferences.yOffsetScreenPercentage;
    
        if (xy[0] < 0)
            xy[0] = 0;
        else if (xy[0] > scr[0])
            xy[0] = scr[0] - this.wSize;
    
        if (xy[1] < 0)
            xy[1] = 0;
        else if (xy[1] > scr[1])
            xy[1] = scr[1] - this.hSize;
        
        this.setPos(xy[0], xy[1], true);
    }
    
    /**
     * Returns X and Y of the HUD's screen area.
     * @return a float array of 2 representing X and Y respectively
     */
    public float[] getPositionByScreenArea()
    {
        int[] scr = MineplexExpHudClient.getRenderContext().getSCR();
        float[] xy = new float[2];
        
        switch (Preferences.hudScreenPositioning)
        {
            case None:
                return new float[] {0.0f, 0.0f};
            case Left:
            case TopLeft:
            case BottomLeft:
            {
                xy[0] = 4;
                break;
            }
            case Top:
            case Center:
            case Bottom:
            {
                xy[0] = ((float)scr[0] / 2.0f) - (this.wSize / 2.0f);
                break;
            }
            case TopRight:
            case Right:
            case BottomRight:
            {
                xy[0] = (float)scr[0] - (4.0f + this.wSize);
                break;
            }
        }
        
        switch (Preferences.hudScreenPositioning)
        {
            case Top:
            case TopLeft:
            case TopRight:
            {
                xy[1] = 4;
                break;
            }
            case Left:
            case Center:
            case Right:
            {
                xy[1] = ((float)scr[1] / 2.0f) - (this.hSize / 2.0f);
                break;
            }
            case Bottom:
            case BottomLeft:
            case BottomRight:
            {
                xy[1] = (float)scr[1] - (4.0f + this.hSize);
                break;
            }
        }
        
        return xy;
    }
    
    
    
    
    
    
    
    //
    // /xp Pattern Matching
    //
    boolean findAndMatch(int line, String text)
    {
        final Matcher m = ExpStatics.EXP_COMMAND_OUTPUT_PATTERN[line].matcher(text);
        
        if (m.find())
        {
            try
            {
                switch (line)
                {
                    case 2: // [Line 3] Your Current Level
                    {
                        if (m.groupCount() == 1)
                        {
                            this.expStatusOnWatch.currentLevel = Integer.parseInt(m.group(1));
                            
                            this.updatePrevExp_OnlyOnWhatMatters(false);
                        }
                        
                        break;
                    }
                    case 4: // [Line 5] The Goodies(tm) (Current EXP, Max EXP and Next Level)
                    {
                        if (m.groupCount() == 3)
                        {
                            this.expStatusOnWatch.currentExp = Long.parseLong(m.group(1));
                            this.expStatusOnWatch.expUntilNextLevel = Long.parseLong(m.group(2));
                            this.expStatusOnWatch.nextLevel = Integer.parseInt(m.group(3));
                            
                            this.updatePrevExp_OnlyOnWhatMatters(true);
                        }
                        
                        break;
                    }
                }
                
                return true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return false;
    }
    
    
    
    
    
    
    //
    // EXP Updating Checks and Executions
    //
    void performExpUpdate()
    {
        if (this.avoidExecuting())
            return;
        
        final long millis = System.currentTimeMillis() ;
        boolean doNotAvoidChatSending = true;
        
        if (this.waitingForExpMessage && this.millisUntilExpMessageSwallowDone > millis)
            doNotAvoidChatSending = false;
        
        if (doNotAvoidChatSending)
            this.sendChatMessage("/xp");
        
        this.worldChangeInitiatedDelay = false;
        this.waitingForExpMessage = true;
        this.millisUntilExpMessageSwallowDone = millis + 2000L;
        
        // Reset EXP Status on Watch when attempting to perform an update
        this.expStatusOnWatch = new ExpCacheState();
    }
    void endPerformingExpUpdate(boolean successful)
    {
        this.waitingForExpMessage = false;
        this.worldChangeInitiatedDelay = false;
        this.currentLine = 0;
        this.millisUntilExpMessageSwallowDone = 0L;
        
        if (successful)
            this.clearOutCachedMessages();
        else
            this.spewOutCachedMessages();

//        this.millisUntilNextExpUpdate = 10000L;
        
        this.finishWatchingExpStatus();
        
        this.updatePrevExp_AccountAllLines(successful);
        this.updateDisplayCacheInfo();
    }
    void finishWatchingExpStatus()
    {
        if (!Preferences.ignoreEmptyExp || !(this.expStatusOnWatch.isEmpty() || this.expStatusOnWatch.isInitialExp()))
        {
            this.expStatus = this.expStatusOnWatch;
            this.expStatusOnWatch = null;
        }
    }
    
    
    
    
    
    
    //
    // Updating Display Cache Text
    //
    public void rebuildDisplayCacheInfo()
    {
        this.cachedFormatToUse = null;
        this.updateDisplayCacheInfo();
    }
    protected void updateDisplayCacheInfo()
    {
        if (this.expStatus.expUntilNextLevel > 0)
            this.percentageUntilNextLevel = (float)((double)this.expStatus.currentExp / (double)this.expStatus.expUntilNextLevel);
        else
            this.percentageUntilNextLevel = 0.0f;
        
        // While at it, also update Road to Level 100's status as well
        this.updateTotalExpAccumulated();
        
        if (this.cachedFormatToUse == null)
        {
            if (this.attemptDisplayFormatting(Preferences.displayFormatting)) {
                this.cachedFormatToUse = Preferences.displayFormatting;
            }
            else
            {
                String langText = getLang().translate(LANG_FORMAT + "format");
                
                if (this.attemptDisplayFormatting(langText)) {
                    this.cachedFormatToUse = langText;
                }
                else
                {
                    // We are in hot waters if this method doesn't work (last resort)
                    if (this.attemptDisplayFormatting(ExpStatics.EXP_DISPLAY_FORMATTING_FALLBACK)) {
                        this.cachedFormatToUse = ExpStatics.EXP_DISPLAY_FORMATTING_FALLBACK;
                    }
                    else
                    {
                        String hc = "Failed to format";
    
                        this.attemptDisplayFormatting(hc);
                        this.cachedFormatToUse = hc;
                    }
                }
                
            }
        }
        else if (!this.attemptDisplayFormatting(this.cachedFormatToUse))
        {
            this.cachedFormatToUse = null;
            this.updateDisplayCacheInfo();
        }
    }
    private boolean attemptDisplayFormatting(String format)
    {
        LineInfoCache[] lines = this.attemptDisplayFormattingS(format, true);
        
        if (lines == null)
            return false;
        
        this.lines = lines;
        this.updateLineRenderPosBox();
        
        return true;
    }
    public LineInfoCache[] attemptDisplayFormattingS(String format, boolean printErrors)
    {
        if (format == null || format.isEmpty())
            return null;
        
        try
        {
            String formatFormatted = String.format
            (
                Locale.ROOT, format,
                
                this.expStatus.currentExp,   this.expStatus.expUntilNextLevel,
                this.expStatus.currentLevel, this.expStatus.nextLevel,
                
                this.progressPercentageFormat.format(this.percentageUntilNextLevel * 100.0f),
                Preferences.updateMethod.getUpdateTypeText(this),
                
                this.getExpLevelGainedSetTimeRemainingTimer(),
                this.totalLevelsGainedInSession, this.totalExpGainedInSession,
                this.totalLevelsGainedInSetTime, this.totalExpGainedInSetTime,
                
                this.totalExpAccumulated,
                this.progressTo100PercentageFormat.format(this.percentageUntilLevelOneHundred)
            );
            
            final String[] lines;
            final int lineSize;
            
            if (formatFormatted.contains("\\n"))
            {
                lines = formatFormatted.split("\\\\n");
                lineSize = lines.length;
            }
            else
            {
                lines = new String[] {formatFormatted};
                lineSize = 1;
            }
            
            List<LineInfoCache> lineInfos = new ArrayList<>();
            
            for (int i = 0; i < lineSize; ++i)
            {
                String line = lines[i];
                
                if (line == null || line.trim().isEmpty())
                    continue;
                
                LineInfoCache lineInfo = new LineInfoCache();
                
                lineInfo.text = line;
                lineInfo.textWidth = this.getTextRendererStringWidth(line);
                
                lineInfos.add(lineInfo);
            }
            
            return lineInfos.toArray(new LineInfoCache[0]);
        }
        catch (Exception e)
        {
            if (printErrors)
                e.printStackTrace();
            
            return null;
        }
    }
    
    public final String getCachedFormatToUse() {
        return this.cachedFormatToUse;
    }
    public String getExpLevelGainedSetTimeRemainingTimer()
    {
        long r = this.millisRemainingUntilNextSetTimeClear - System.currentTimeMillis();
        
        if (r >= 0)
            return JuggleTimeUnit.formatFromMillis(r);
        else
            return JuggleTimeUnit.formatUnknown();
    }
    
    //
    public void updateLineRenderPosBox()
    {
        if (this.lines != null)
            this.updateLineRenderPosBox(this.lines, this.xPosition, this.yPosition, this.wSize, this.hSize);
    }
    public void updateLineRenderPosBox(LineInfoCache[] lines, float xPosition, float yPosition, float wSize, float hSize)
    {
        final int size = lines.length;
        
        for (int i = 0; i < size; ++i)
        {
            LineInfoCache line = lines[i];
            
            line.setPosAndBoxPos(getTextAndBgBoxPos
            (
                xPosition, yPosition, wSize, hSize,
                0.0f, 0.0f,
                
                line.textWidth,
                this.getTextRendererHeight(),
                
                Preferences.leftBackgroundEdge, Preferences.topBackgroundEdge,
                Preferences.rightBackgroundEdge, Preferences.bottomBackgroundEdge,
                
                i, size, Preferences.showBorders
            ));
        }
    }
    
    
    
    
    
    protected void renderOnlyTextS(RenderContext ctx, float tickDelta, String s, float alpha, float x, float y, int textColor)
    {
        if (s == null || alpha <= 0.0f)
            return;
        
        int txAlpha = ColorUtility.blendAndGetByteAlpha(alpha, (textColor >> 24) & 255);
    
        if (txAlpha > 3)
        {
            ctx.enableBlend();
            ctx.defaultBlendFunc();
            ctx.enableTexture();
            
            ctx.drawString(s, x, y, (txAlpha << 24) | (textColor & 0xFFFFFF), Preferences.drawTextWithShadow);
            
            ctx.disableBlend();
            ctx.disableTexture();
        }
    }
    protected void renderOnlyTextBackgroundS(RenderContext ctx, float tickDelta, float alpha, float left, float top, float right, float bottom, int bgColor, int boColor)
    {
        if (alpha <= 0.0f)
            return;
        
        int bgAlpha = ColorUtility.blendAndGetByteAlpha(alpha, (bgColor >> 24) & 255);
        int boAlpha = ColorUtility.blendAndGetByteAlpha(alpha, (boColor >> 24) & 255);
        
        if (bgAlpha > 0)
        {
            ctx.fill
            (
                left, top, right, bottom,
                (bgAlpha << 24) | (bgColor & 0xFFFFFF)
            );
        }
        if (boAlpha > 0)
        {
            ctx.outerWireframe
            (
                left, top, right, bottom,
                (boAlpha << 24) | (boColor & 0xFFFFFF)
            );
        }
    }
    
    /**
     * Returns 6 floats array representing the following:
     * <ul>
     * <li> 0 = Text's X Position </li>
     * <li> 1 = Text's Y Position </li>
     * <li> 2 = Background's Box Left   Position </li>
     * <li> 3 = Background's Box Top    Position </li>
     * <li> 4 = Background's Box Right  Position </li>
     * <li> 5 = Background's Box Bottom Position </li>
     * </ul>
     *
     * @param xOffset the x offset in which this will be moved to
     * @param xOffset the y offset in which this will be moved to
     * @param sWidth the rendering string's width
     * @param sHeight the rendering string's height
     * @param lineAlike creates an assumption that this is like a line and has to render
     *        at different Y positions
     * @param assumingLineCount also creates an assumption as to how many lines exists
     *        and adjusts it to account for such thing: only used on Y center and bottom
     *        in the meantime
     * @param accountForBorders accounts for borders, which add +1 pixels out on each
     *        text border side. note: this does not set the background's box borders to
     *        increase by 1 or otherwise this would cause more problems; it only modified
     *        the position of each elements
     *
     * @return a six-entry float representing what was said
     */
    protected static float[] getTextAndBgBoxPos(float xPosition, float yPosition, float wSize, float hSize,
                                                float xOffset, float yOffset, float sWidth, float sHeight,
                                                float leftEdgeAdd, float topEdgeAdd, float rightEdgeAdd, float bottomEdgeAdd,
                                         int lineAlike, int assumingLineCount, boolean accountForBorders)
    {
        float x = xPosition + xOffset;
        float y = yPosition + yOffset;
        
        // final float xAdd = leftEdgeAdd + rightEdgeAdd;
        // final float yAdd = topEdgeAdd + bottomEdgeAdd;
        // final float xAdd = 6.0f;
        // final float yAdd = 3.0f;
        final float bAdd = accountForBorders ? 1.0f : 0.0f;
        
        if (Preferences.hudTextPositioning == null)
        {
            x += (wSize / 2) - (sWidth / 2);
            y += (hSize / 2) - (sHeight / 2);
        }
        else
        {
            // X Positioning
            switch (Preferences.hudTextPositioning)
            {
                // Center
                case Center:
                case Top:
                case Bottom:
                {
                    x += (wSize / 2) - (sWidth / 2);
                    
                    break;
                }
                // Left
                case Left:
                case TopLeft:
                case BottomLeft:
                {
                    // x += xAdd + bAdd;
                    x += leftEdgeAdd + bAdd;
                    
                    break;
                }
                // Right
                case Right:
                case TopRight:
                case BottomRight:
                {
                    // x += wSize - (sWidth + xAdd + bAdd);
                    x += wSize - (sWidth + rightEdgeAdd + bAdd);
                    
                    break;
                }
                
                default:
                    break;
            }
            
            // final float lhb = sHeight + ((yAdd * 2.0f) - 1.0f) + (bAdd * 2.0f); // also known as LineHeightBox
            final float lhb = sHeight + (topEdgeAdd + bottomEdgeAdd) + (bAdd * 2.0f); // also known as LineHeightBox
            final float lay = lineAlike * lhb; // also known as LineAlikeY
            
            // Y Positioning
            switch (Preferences.hudTextPositioning)
            {
                // Top
                case Top:
                case TopLeft:
                case TopRight:
                {
                    // y += yAdd + bAdd + lay;
                     y += topEdgeAdd + bAdd + lay;
                    break;
                }
                // Center
                case Center:
                case Left:
                case Right:
                {
                    y += (hSize / 1.5f) + lay - ((assumingLineCount * lhb) / 2.0f);
                    break;
                }
                // Bottom
                case Bottom:
                case BottomLeft:
                case BottomRight:
                {
                    // y += hSize + yAdd + bAdd + lay - (assumingLineCount * lhb);
                    y += hSize + bottomEdgeAdd + bAdd + lay - (assumingLineCount * lhb);
                    break;
                }
                
                default:
                    break;
            }
        }
        
        return new float[]
        {
            x, y,
            
            x - leftEdgeAdd,
            y - topEdgeAdd,
            x + sWidth  + rightEdgeAdd,
            y + sHeight + bottomEdgeAdd
            
            // x - xAdd, y - yAdd,
            // x + sWidth  + xAdd,
            // y + sHeight + (yAdd - 1.0f)
        };
    }
    
    
    
    
    
    
    /**
     * Send all of the cached messages this HUD has stored during or after the EXP wait. It
     * then removes all of the cached entries one by one and all if there were leftovers.
     *
     * <p> Ensure you first set {@link #waitingForExpMessage} to {@code false} (not needed to
     * do so anymore) as this will end up calling {@code onChatPrintEvent} (if that's applicable
     * to your mod loader) x amount of times depending on how many cached entries it has to deal
     * with.
     */
    void spewOutCachedMessages()
    {
        final boolean wasWaitingForExpMsg = this.waitingForExpMessage;
        this.waitingForExpMessage = false;
        
        try
        {
            Iterator<?> cmtp = this.cachedMessagesToPrint.iterator();
            
            while (cmtp.hasNext())
            {
                this.printMessage(cmtp.next());
                cmtp.remove();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        this.clearOutCachedMessages();
        
        this.waitingForExpMessage = wasWaitingForExpMsg;
    }
    void clearOutCachedMessages()
    {
        if (!this.cachedMessagesToPrint.isEmpty())
            this.cachedMessagesToPrint.clear();
    }
    
    
    
    
    
    
    void updatePrevExp_AccountAllLines(boolean successful)
    {
        if (Preferences.accuracy == AccuracyMode.ACCOUNT_FOR_ALL_LINES && successful)
            this.updatePrevExpsAndExpLevelCheck();
    }
    
    void updatePrevExp_OnlyOnWhatMatters(boolean expInfoLine)
    {
        if (Preferences.accuracy == AccuracyMode.ONLY_ON_WHAT_MATTERS && expInfoLine)
        {
            this.finishWatchingExpStatus();
            this.updatePrevExpsAndExpLevelCheck();
        }
    }
    
    void updatePrevExpsAndExpLevelCheck()
    {
        final ExpCacheState expStatus = this.expStatus.copy();
        
        if (this.prevExpStatuses.isEmpty())
        {
            this.prevExpStatuses.add(expStatus);
            this.updateTotalExpAccumulatedAlongWithLevels();
        }
        else
        {
            // Compare against the first item in the entry and make a guess
            final ExpCacheState expStatusPrev = this.prevExpStatuses.peekFirst();
            
            final boolean shouldUpdateExps = Preferences.expTotalsCacheMethodCheck ==
                    ExpTotalsCacheMethodCheck.ONLY_ON_EXP_UPDATE_END;
            
            // First check for level differences between the previous and current
            // Note: checking for Next Level has no point as that also gets updated
            // if the user had their current level changed
            if (expStatus.currentLevel != expStatusPrev.currentLevel)
            {
                int levelDiff = expStatus.currentLevel - expStatusPrev.currentLevel;
                
                if (levelDiff > 0)
                {
                    // Add +1 (should be) to the total levels in both set time and session
                    this.totalLevelsGainedInSetTime += levelDiff;
                    this.totalLevelsGainedInSession += levelDiff;
                    
                    if (shouldUpdateExps)
                    {
                        // Use the previous EXP info to know the total EXP obtained before
                        // the level update
                        long expDiff = expStatusPrev.expUntilNextLevel - expStatusPrev.currentExp;
                        // Then use the current EXP info and add it as part of the total
                        expDiff += expStatus.currentExp;
                        
                        // Check if the total of levels is more than 1.
                        if (levelDiff > 1)
                        {
                            // Skip the user's previous level as that was already done when
                            // initializing expDiff. The current level is also skipped since it
                            // doesn't count; only the current exp which was already done after
                            // expDiff.
                            
                            // Add on the exp differentials; not much to worry as we've already
                            // checked that the levels are more than 1. If it's a level skip,
                            // it would already be targeting to the skipped level and return only
                            // its EXP Until Next Level.
                            expDiff += ExpStatics.accumulateExpFromLevelRanges
                                    (expStatusPrev.currentLevel + 1, expStatus.currentLevel - 1);
                        }
                        // Add the total differentials to the total EXPs in session and in set time.
                        this.totalExpGainedInSetTime += expDiff;
                        this.totalExpGainedInSession += expDiff;
                    }
                }
                
                this.updateTotalExpAccumulatedAlongWithLevels();
            }
            // Assuming that the current EXP was changed to a higher one and ensure only to update
            // if shouldUpdateExps is set to true!
            // Note: The user can only get lower EXP if the server didn't load their user
            // data yet, or if they have had a level change; which the latter should have
            // already executed under currentLevel being different. This is more of a fail-safe
            // unless Mineplex pulls a confusion.
            else if (shouldUpdateExps && expStatus.currentExp > expStatusPrev.currentExp)
            {
                long expObtained = expStatus.currentExp - expStatusPrev.currentExp;
                this.totalExpGainedInSetTime += expObtained;
                this.totalExpGainedInSession += expObtained;
            }
            
            if (this.prevExpStatuses.size() + 1 > Preferences.maxPrevExpCaches)
                this.prevExpStatuses.removeLast();
            
            this.prevExpStatuses.addFirst(expStatus);
        }
    }
    
    protected void updateTotalExpAccumulatedAlongWithLevels()
    {
        this.totalExpAccumulatedFromLevelCache =
                ExpStatics.accumulateExpFromCurrentLevel(this.expStatus.currentLevel);
        this.updateTotalExpAccumulated();
    }
    
    protected void updateTotalExpAccumulated()
    {
        this.totalExpAccumulated = this.totalExpAccumulatedFromLevelCache + this.expStatus.currentExp;
        this.percentageUntilLevelOneHundred = (float)((double)this.totalExpAccumulated /
                (double)ExpStatics.LEVEL_100_TOTAL_EXP);
    }
}
