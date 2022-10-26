package jugglestruggle.mineplexexphud.hud.info;

public class LineInfoCache
{
    public String text;
    
    public float textX;
    public float textY;
    public int textWidth;
    
    public float left;
    public float top;
    public float right;
    public float bottom;
    
    public LineInfoCache() {
        // nothing to worry!
    }
    
    public LineInfoCache(LineInfoCache c)
    {
        this.text = c.text;
        this.textWidth = c.textWidth;
        
        this.left = c.left;
        this.top = c.top;
        this.right = c.right;
        this.bottom = c.bottom;
    }
    
    public LineInfoCache copy() {
        return new LineInfoCache(this);
    }
        
        /*
        public void setBox(RectangleF posBox)
        {
            this.left = posBox.getX();
            this.top = posBox.getY();
            this.right = posBox.getW();
            this.bottom = posBox.getH();
        }
         */
    /**
     * @param f a six-array float representing... what?
     */
    public void setPosAndBoxPos(float[] f)
    {
        this.textX = f[0];
        this.textY = f[1];
        
        this.left   = f[2];
        this.top    = f[3];
        this.right  = f[4];
        this.bottom = f[5];
    }
}