/*
 * Copyright (C) 2012 Daniel Heinrich
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
 */
package darwin.jopenctm.data;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author daniel
 */
public class AttributeData {

    public static final float STANDARD_UV_PRECISION = 1f / 4096f;
    public static final float STANDARD_PRECISION = 1f / 256;
    public final String name;         // Unique name
    public final String materialName;     // File name reference (used only for UV maps)
    public final float precision;
    public final float[] values;   // Attribute/UV coordinate values (per vertex)

    public AttributeData(String name, String materialName, float precision, float[] values) {
        this.name = name;
        this.materialName = materialName;
        this.precision = precision;
        this.values = values;
    }

    public boolean checkIntegrity() {
        return precision > 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.materialName);
        hash = 67 * hash + Float.floatToIntBits(this.precision);
        hash = 67 * hash + Arrays.hashCode(this.values);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AttributeData other = (AttributeData) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.materialName, other.materialName)) {
            return false;
        }
        if (Float.floatToIntBits(this.precision) != Float.floatToIntBits(other.precision)) {
            return false;
        }
        if (!Arrays.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }
}
