# JOpenCTM
Java implementation of the OpenCTM file-format.
This library is licensed under the LGPL 3.0 License.

## Content
* [Getting Started](#getting-started)
* [Compression Formats](#compression-formats)
  * [Raw](#raw)
  * [MG1](#mg1)
  * [MG2](#mg2)
* [Usage](#usage)
  * [Reading a OpenCTM file](#reading-a-openctm-file)
  * [Writing a OpenCTM file](#writing-a-openctm-file)
* [Tools and Integrations](#tools-and-integrations)
* [Similiar Projects](#similiar-projects)

## Getting Started

        <dependency>
            <groupId>com.github.danny02</groupId>
            <artifactId>JOpenCTM</artifactId>
            <version>1.5.2</version>
        </dependency>
## Compression Formats
### Raw
Raw binary dump of the vertex data. The format does not apply any compression, but piping the output stream through e.g. gzip gives quite an improvement
### MG1
"Lossless" mesh compression. Not completly lossless, because the vertex and index odering is lost/changed.
### MG2
Lossy mesh encoding. Precision values are provided for the attributes which represent a maximum agreeable error in the data.
## The Mesh Pojo
A mesh in the OpenCTM format supports the following data per vertex:
* one 4D position vector
* one 3D optional normal vector
* N 2D texture coordinates
* N 4D custom vertex attribute vectors

The mesh also contains an triangle index list (three consecutive indicies form a triangle of the mesh).

        float[] vertices = {...};
        float[] normals = {...} // or 'null' of not existing;
        int[] indices = {...};
        
        //provide an empty array if the mesh has no texture coordinates, same goes for the custom attributes
        AttributeData[] texcoordinates = new AttributeData[0];
        //each custom attribute defines an own precision value for the MG2 encoder
        AttributeData[] attributes = {new AttributeData("attribute name", "material name", STANDARD_PRECISION, new float[]{...})};
        
        Mesh mesh = new Mesh(vertices, normals, indices, texcoordinates, attributes);
        
        //you should always check the integrity of the model before encoding
        //report errors with checked exception
        mesh.checkIntegrity();
        //return a list of errors
        mesh.validate();
Take a look at [all the properties](https://github.com/Danny02/JOpenCTM/blob/develop/src/main/java/darwin/jopenctm/data/Mesh.java#L83) which are checked.
## Usage
### Reading a OpenCTM file
        InputStream in = new FileInputStream(...);
        CtmFileReader reader = new CtmFileReader(in);
        
        //read a CTM file into memory without checking any mesh properties
        Mesh mesh = reader.decodeWithoutValidation();
        //read a CTM file while checking some propeties, i.e. indicies point to valid verticies, count of normals is equal to vertex count
        Mesh validMesh = reader.decode();
### Writing a OpenCTM file
        OutputStream out = new FileOutputStream(...);
        MeshEncoder rawEncoder = new RawEncoder();
        MeshEncoder mg1Encoder = new MG1Encoder();
        //MG2 is an lossy encoding. The precision values define the maximum error of the vertex attributes
        MeshEncoder mg2Encoder = new MG2Encoder(VERTEX_PRECISION, NORMAL_PRECISION);
        
        CtmFileWriter writer = new CtmFileWriter(out, rawEncoder);
        //the compression factor is only used by the two MG encoders and ignored by the raw encoder
        CtmFileWriter compressedWriter = new CtmFileWriter(out, mg2Encoder, lzmaCompressionFactor /*1-9, 5 gives already nearly the best compression*/);
        
        Mesh teapot = ...;
        writer.encode(teapot, "Written by my awesome App!");
        
## Tools and Integrations
* [OpenCTM C++](https://github.com/Danny02/OpenCTM) reference implementation 
provides tools like a model viewer and a commandline converter from and to OpenCTM from common 3D model formats
* [OpenCTM Validator](https://github.com/Danny02/ctmvalidator)
a simple commandline tool to validate and inspect a OpenCTM file

## Similiar Projects
* [Open3DGC](https://github.com/KhronosGroup/glTF/wiki/Open-3D-Graphics-Compression)
better results than OpenCTM and properly supported by Khronos Group

