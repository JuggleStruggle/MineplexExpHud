# Mineplex EXP Hud
A HUD mod requested by a cat which features ways for you to track your EXP and leveling.

Some notable features:
 * Updates depending on the interval you set or when the world changes
 * EXP and levels gained over a session *(note: it does not account if you leave the server! Might be an option in the 
   future!)*
 * Send the HUD anywhere you'd want it to be by using a percentage-based offset so that whenever you resize the screen,
   it doesn't stay stuck in a particular location
 * Position the HUD text
 * A fully customizable text hud by either using your formatting or language-dependent should your display text fail to 
   parse (do keep in mind that in order for your format to be seen and used, it must follow 
   [Java Formatting Standards](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html) and will let you
   know if there are any known errors by highlighting the line red.) 
 * Customize the formattings of [decimals](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html), 
   individually (which means you can customize both percentage or seconds) but there is no way at the moment to edit 
   those preferences other than the configuration file
 * Customize colors, including borders
 
## Display Formatting
Suppose you want to know your actual EXP, received from the server, without having to input a fake number. This is 
where the mod will allow you to do this along with many other supported formats in the most rough way (this mod was 
rather "rushed"). 

Let's say you want format it something along like this: `You have <current exp> EXP` and have it show as the current 
EXP that you have, which would be `11111`, and results as `You have 11111 EXP`. You'd have to use, in the actual display
formatting line, `You have %1$s EXP`.

As this uses Java's String formatting, you will have to learn how to apply formatting in a way the mod does not error
out from making the editing line's highlight be red.

### Color Formatting (introduced in v0.1.0)
- To format the text you're dealing with, use `&<color>`.
  - This behaves the same way as it would by using `§<color>` from the vanilla game.

### Some small tips: 
- To show a simple percent `%` while avoiding an error, use `%%`.
- Rather than use `%<number>$s` (which specifically targets which format index to use and is the preferred method),
  you could instead use `%s` to do an incremental formatting (just keep in mind that all of the lines you're dealing
  with are all parsed at once as the mod connects your multi-line texts into a single line which is why the numeric 
  route is better-suited to avoid confusion. You'll see this as you experiment around with your multi-line strings).
- To show `&<color>` in the end result, just perform `&&<color>`.

### Examples:
`EXP: %1$s/%2$s | Progress: %5$s` results in `EXP: 11111/50000 | Progress: 22.22%` <br>
`Session: %9$s EXP | Set Time: %11$s EXP` results in `Session: 100 EXP | Set Time: 5 EXP` <br>
`Until Next Update: %6$s` results in `Until Next Update: 3 seconds` 

## Downloads
At the time of writing (2022-10-26), it is not available in CurseForge or Modrinth yet, so head to
[GitHub Releases](https://github.com/JuggleStruggle/MineplexExpHud/releases) instead.
