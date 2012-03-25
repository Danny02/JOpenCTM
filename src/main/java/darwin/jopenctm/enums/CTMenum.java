/*
 * To change this template; choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.enums;

/**
 *
 * @author daniel
 */
public class CTMenum
{

    // Context queries
    public static final int CTM_VERTEX_COUNT = 0x0301; ///< Number of vertices in the mesh (integer).
    public static final int CTM_TRIANGLE_COUNT = 0x0302; ///< Number of triangles in the mesh (integer).
    public static final int CTM_HAS_NORMALS = 0x0303; ///< CTM_TRUE if the mesh has normals (integer).
    public static final int CTM_UV_MAP_COUNT = 0x0304; ///< Number of UV coordinate sets (integer).
    public static final int CTM_ATTRIB_MAP_COUNT = 0x0305; ///< Number of custom attribute sets (integer).
    public static final int CTM_VERTEX_PRECISION = 0x0306; ///< Vertex precision - for MG2 (float).
    public static final int CTM_NORMAL_PRECISION = 0x0307; ///< Normal precision - for MG2 (float).
    public static final int CTM_COMPRESSION_METHOD = 0x0308; ///< Compression method (integer).
    public static final int CTM_FILE_COMMENT = 0x0309; ///< File comment (string).
    // UV/attribute map queries
    public static final int CTM_NAME = 0x0501; ///< Unique name (UV/attrib map string).
    public static final int CTM_FILE_NAME = 0x0502; ///< File name reference (UV map string).
    public static final int CTM_PRECISION = 0x0503; ///< Value precision (UV/attrib map float).
    // Array queries
    public static final int CTM_INDICES = 0x0601; ///< Triangle indices (integer array).
    public static final int CTM_VERTICES = 0x0602; ///< Vertex point coordinates (float array).
    public static final int CTM_NORMALS = 0x0603; ///< Per vertex normals (float array).
    public static final int CTM_UV_MAP_1 = 0x0700; ///< Per vertex UV map 1 (float array).
    public static final int CTM_UV_MAP_2 = 0x0701; ///< Per vertex UV map 2 (float array).
    public static final int CTM_UV_MAP_3 = 0x0702; ///< Per vertex UV map 3 (float array).
    public static final int CTM_UV_MAP_4 = 0x0703; ///< Per vertex UV map 4 (float array).
    public static final int CTM_UV_MAP_5 = 0x0704; ///< Per vertex UV map 5 (float array).
    public static final int CTM_UV_MAP_6 = 0x0705; ///< Per vertex UV map 6 (float array).
    public static final int CTM_UV_MAP_7 = 0x0706; ///< Per vertex UV map 7 (float array).
    public static final int CTM_UV_MAP_8 = 0x0707; ///< Per vertex UV map 8 (float array).
    public static final int CTM_ATTRIB_MAP_1 = 0x0800; ///< Per vertex attribute map 1 (float array).
    public static final int CTM_ATTRIB_MAP_2 = 0x0801; ///< Per vertex attribute map 2 (float array).
    public static final int CTM_ATTRIB_MAP_3 = 0x0802; ///< Per vertex attribute map 3 (float array).
    public static final int CTM_ATTRIB_MAP_4 = 0x0803; ///< Per vertex attribute map 4 (float array).
    public static final int CTM_ATTRIB_MAP_5 = 0x0804; ///< Per vertex attribute map 5 (float array).
    public static final int CTM_ATTRIB_MAP_6 = 0x0805; ///< Per vertex attribute map 6 (float array).
    public static final int CTM_ATTRIB_MAP_7 = 0x0806; ///< Per vertex attribute map 7 (float array).
    public static final int CTM_ATTRIB_MAP_8 = 0x0807; ///< Per vertex attribute map 8 (float array).
}
