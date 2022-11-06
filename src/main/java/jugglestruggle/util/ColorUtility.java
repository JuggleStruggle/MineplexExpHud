package jugglestruggle.util;

public final class ColorUtility
{
    public static int blendAndGetByteAlpha(float controllingAlpha, int alpha)
    {
        if (alpha > 0)
        {
            float fAlpha = (float)alpha / 255.0f;
            
            fAlpha *= controllingAlpha;
            
            if (fAlpha > 0.0f)
            {
                if (fAlpha > 1.0f)
                    fAlpha = 1.0f;
                
                return (int)(fAlpha * 255.0f);
            }
        }
        
        return 0;
    }
    
    private ColorUtility() {}
}
