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

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.regex.Pattern;

public final class ExpStatics
{
    /**
     * Only used if both display methods, {@link jugglestruggle.mineplexexphud.pref.Preferences#displayFormatting} and
     * the Language's method, failed to format anything or properly.
     */
    public static final String EXP_DISPLAY_FORMATTING_FALLBACK;
    
    public static final Long LEVEL_100_TOTAL_EXP;
    public static final Map<Integer, Long> EXP_UNTIL_NEXT_LEVEL;
    
    public static final Pattern[] EXP_COMMAND_OUTPUT_PATTERN;
    
    /**
     * Mineplex is at least known to use 3 EXP display formats:
     * - When the game ends: provided it's public (community, player or staff hosted don't give out any)
     * - When collecting rewards from Carl the Creeper
     * - When collecting rewards from a mission
     *
     * And they all require to be a whole line; they cannot be partial as anyone can easily
     * pull off this format. Looking at you, Immortals.
     */
    public static final Pattern[] EXP_ADDITION_PATTERN;
    
    static
    {
        final Pattern GREEN_LINE = Pattern.compile("[-]{30}");
        final Pattern EMPTY_LINE = Pattern.compile(" ");
//        final Pattern GREEN_LINE = Pattern.compile("§r§a§m[-]{30}§r");
//        final Pattern EMPTY_LINE = Pattern.compile("§r §r");
        
        EXP_COMMAND_OUTPUT_PATTERN = new Pattern[]
        {
            GREEN_LINE,
            EMPTY_LINE,
            Pattern.compile("  You are level (\\d+)"),
            // Pattern.compile("§r  §r§fYou are level §r§e(\\d+)§r"), // Formatted
            EMPTY_LINE,
            Pattern.compile("  (\\d+) / (\\d+) EXP until level (\\d+)"),
            // Pattern.compile("§r §r§e(\\d+)§r§7 / (\\d+)§r§f EXP until level §r§e(\\d+)§r"), // Formatted
            EMPTY_LINE,
            Pattern.compile("  [▌]{30} "), // (the ▌totals to 30 of them)
            // Pattern.compile(""), // Formatted
            EMPTY_LINE,
            GREEN_LINE,
        };
        
        // There must be more "addition" patterns... but all of this is done by an unranked
        // player and some more might have been missed, so it'd be nice to get formatted patterns
        // that are actually sent from the server and not just from a screenshot
        EXP_ADDITION_PATTERN = new Pattern[]
        {
            Pattern.compile("§r§7  \\+§r§e(\\d+) §r§7Experience§r"),
            Pattern.compile("§r§9Carl> §r§7Rewarded §r§e(\\d+) Experience§r"),
            Pattern.compile("§r - §r§e(\\d+) XP§r"),
        };
        
        // Source: https://www.mineplex.com/threads/xp-until-next-level.3076/
        ImmutableMap.Builder<Integer, Long> EXPS = ImmutableMap.builder();
        
        EXPS.put( 0,     500L); EXPS.put( 1,   1_000L);
        EXPS.put( 2,   1_500L); EXPS.put( 3,   2_000L);
        EXPS.put( 4,   2_500L); EXPS.put( 5,   3_000L);
        EXPS.put( 6,   3_500L); EXPS.put( 7,   4_000L);
        EXPS.put( 8,   4_500L); EXPS.put( 9,   5_000L);
        
        EXPS.put(10,   6_000L); EXPS.put(11,   7_000L);
        EXPS.put(12,   8_000L); EXPS.put(13,   9_000L);
        EXPS.put(14,  10_000L); EXPS.put(15,  11_000L);
        EXPS.put(16,  12_000L); EXPS.put(17,  13_000L);
        EXPS.put(18,  14_000L); EXPS.put(19,  15_000L);
        
        EXPS.put(20,  17_000L); EXPS.put(21,  19_000L);
        EXPS.put(22,  21_000L); EXPS.put(23,  23_000L);
        EXPS.put(24,  25_000L); EXPS.put(25,  27_000L);
        EXPS.put(26,  29_000L); EXPS.put(27,  31_000L);
        EXPS.put(28,  33_000L); EXPS.put(29,  35_000L);
        
        EXPS.put(30,  37_000L); EXPS.put(31,  39_000L);
        EXPS.put(32,  41_000L); EXPS.put(33,  43_000L);
        EXPS.put(34,  45_000L); EXPS.put(35,  47_000L);
        EXPS.put(36,  49_000L); EXPS.put(37,  51_000L);
        EXPS.put(38,  53_000L); EXPS.put(39,  55_000L);
        
        EXPS.put(40,  58_000L); EXPS.put(41,  61_000L);
        EXPS.put(42,  64_000L); EXPS.put(43,  67_000L);
        EXPS.put(44,  70_000L); EXPS.put(45,  73_000L);
        EXPS.put(46,  76_000L); EXPS.put(47,  79_000L);
        EXPS.put(48,  82_000L); EXPS.put(49,  85_000L);
        
        EXPS.put(50,  88_000L); EXPS.put(51,  91_000L);
        EXPS.put(52,  94_000L); EXPS.put(53,  97_000L);
        EXPS.put(54, 100_000L); EXPS.put(55, 103_000L);
        EXPS.put(56, 106_000L); EXPS.put(57, 109_000L);
        EXPS.put(58, 112_000L); EXPS.put(59, 115_000L);
        
        EXPS.put(60, 119_000L); EXPS.put(61, 123_000L);
        EXPS.put(62, 127_000L); EXPS.put(63, 131_000L);
        EXPS.put(64, 135_000L); EXPS.put(65, 139_000L);
        EXPS.put(66, 143_000L); EXPS.put(67, 147_000L);
        EXPS.put(68, 151_000L); EXPS.put(69, 155_000L);
        
        EXPS.put(70, 159_000L); EXPS.put(71, 163_000L);
        EXPS.put(72, 167_000L); EXPS.put(73, 171_000L);
        EXPS.put(74, 175_000L); EXPS.put(75, 179_000L);
        EXPS.put(76, 183_000L); EXPS.put(77, 187_000L);
        EXPS.put(78, 191_000L); EXPS.put(79, 195_000L);
        
        EXPS.put(80, 200_000L); EXPS.put(81, 205_000L);
        EXPS.put(82, 210_000L); EXPS.put(83, 215_000L);
        EXPS.put(84, 220_000L); EXPS.put(85, 225_000L);
        EXPS.put(86, 230_000L); EXPS.put(87, 235_000L);
        EXPS.put(88, 240_000L); EXPS.put(89, 245_000L);
        
        EXPS.put(90, 250_000L); EXPS.put(91, 255_000L);
        EXPS.put(92, 260_000L); EXPS.put(93, 265_000L);
        EXPS.put(94, 270_000L); EXPS.put(95, 275_000L);
        EXPS.put(96, 280_000L); EXPS.put(97, 285_000L);
        EXPS.put(98, 290_000L); EXPS.put(99, 295_000L);
        
        EXPS.put(100, -1L);
        
        EXP_UNTIL_NEXT_LEVEL = EXPS.build();
        
        LEVEL_100_TOTAL_EXP = 10_672_500L;
        EXP_DISPLAY_FORMATTING_FALLBACK = "Level: %3$s | EXP: %1$s/%2$s\\nProgress: %5$s%% | Next update: %6$s";
        
    }
    
    public static long accumulateExpFromCurrentLevel(int level)
    {
        if (level < 0)
            return 0;
        else if (level > 100)
            level = 100;
    
        long accumulatedExp = 0;
        
        for (int i = 0; i < level; ++i)
            accumulatedExp += EXP_UNTIL_NEXT_LEVEL.get(i);
        
        return accumulatedExp;
    }
    /**
     *
     * @param min inclusive at bound-only
     * @param max inclusive
     * @return the total exp accumulated starting from {@code min} to {@code max}
     */
    public static long accumulateExpFromLevelRanges(int min, int max)
    {
        if (min > max)
        {
            int temp = max;
            max = min;
            min = temp;
        }
        
        if (min >= 100)
            return EXP_UNTIL_NEXT_LEVEL.get(100);
        else if (max < 0)
            return 0;
        else if (max > 100)
            max = 100;
        
        if (min == max)
            return EXP_UNTIL_NEXT_LEVEL.get(min);
        else
        {
            if (min < 0)
                min = 0;
    
            long accumulatedExp = 0;
    
            for (int i = min; i <= max; ++i)
                accumulatedExp += EXP_UNTIL_NEXT_LEVEL.get(i);
    
            return accumulatedExp;
        }
    }
    
    
    
    
    
    
    
    
    private ExpStatics() {}
}
