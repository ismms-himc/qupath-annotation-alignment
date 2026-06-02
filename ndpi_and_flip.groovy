//    File : addFlippedImage.groovy
//    The script creates a affine transformed version of an image in a new project entry and displays it in the viewer.
//    It can be used to test flipping an image.
//    see: https://forum.image.sc/t/flipping-an-image-in-qupaths-gui/85110
//
//    Copyright (C) 2023 Peter Haub
//
//    This is a minimal-version, prototype  experimental QuPath script. The script is not intended to be optimized and/or finalized code.
//    The primary use of the script is to study the feasibility and processing performance and to visually inspect the processing results.
//
//    This script is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty 
//    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//    See the GNU General Public License for more details.
// **************************************************************************

import java.awt.geom.AffineTransform
import qupath.lib.images.servers.TransformedServerBuilder
import qupath.lib.gui.scripting.QPEx
import qupath.lib.projects.Project
import qupath.lib.objects.PathObject
import qupath.lib.objects.PathObjectTools

import qupath.lib.regions.*
import qupath.imagej.tools.IJTools
import qupath.opencv.tools.OpenCVTools
import org.bytedeco.opencv.opencv_core.Size
import static org.bytedeco.opencv.global.opencv_core.*
import static org.bytedeco.opencv.global.opencv_imgproc.*
import ij.*
import qupath.lib.ndpaAnnotations.*
import qupath.ext.ndpa.NdpaTools


// ******  Parameters  *******

boolean applyFlipX = true  // set to true to flipX
boolean applyFlipY = false  // set to true to flipY

double rotation = 0.0 // set to e.g. 0.7 to rotate

         
// ******  Script  *******

Project project = QP.getProject()

def entry = QP.getProjectEntry()

if (entry == null){
    print 'No image is loaded!' + '\n'
    return
}

def name = entry.getImageName()
print name + '\n'

def imageData = entry.readImageData()
def server = imageData.getServer()

print NdpaTools.readNDPA(imageData, null)
def annotations = imageData.getHierarchy().getAnnotationObjects()

int w = server.getWidth()
int h = server.getHeight()

int sx = 1
int sy = 1
int tx = 0
int ty = 0

if (applyFlipX){
    sx = -1
    tx = -w
}
if (applyFlipY){
    sy = -1
    ty = -h
}

AffineTransform transform = new AffineTransform()
transform.scale(sx, sy)
if (rotation != 0)
    transform.rotate(rotation)
transform.translate(tx, ty)

// or with Affine matrix m
//transform.setTransform(m[0], m[3], m[1], m[4], 0, 0)

// Create the new server, the new image & add to the project
TransformedServerBuilder builder = new TransformedServerBuilder(server)
builder.transform(transform)
ImageServer<BufferedImage> serverNew = builder.build()
def imageDataCreated = new ImageData<BufferedImage>(serverNew)
imageDataCreated.setImageType(ImageData.ImageType.FLUORESCENCE)
def hierarchyCreated = imageDataCreated.getHierarchy()

double[] matrix = new double[6]
transform.getMatrix(matrix)

def transformedAnnotations = []
for(annotation in annotations) {
    transformedAnnotations << PathObjectTools.transformObject(annotation, transform, false)
}
hierarchyCreated.addObjects(transformedAnnotations)

Platform.runLater {
    QPEx.getCurrentViewer().setImageData(imageDataCreated)
    QPEx.getQuPath().refreshProject()
}
println 'Done!' + '\n'