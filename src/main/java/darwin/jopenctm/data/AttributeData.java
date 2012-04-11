/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.data;

/**
 *
 * @author daniel
 */
public class AttributeData
{
    public static final float STANDART_UV_PRECISION = 1f / 4096f;
    public static final float STANDART_PRECISION = 1f / 256;

    public final String name;         // Unique name
    public final String materialName;     // File name reference (used only for UV maps)
    public final float precision;
    public final float[] values;   // Attribute/UV coordinate values (per vertex)

    public AttributeData(String name, String materialName, float precision, float[] values)
    {
        this.name = name;
        this.materialName = materialName;
        this.precision = precision;
        this.values = values;
    }

    public boolean checkIntegrity()
    {
        return precision > 0;
    }
}
