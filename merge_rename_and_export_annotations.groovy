import org.locationtech.jts.geom.Geometry
import qupath.lib.roi.GeometryTools
import static qupath.lib.gui.scripting.QPEx.*

def class_names = ["#000000", "#00FF00", "#00FFFF", "#FFFF00"]
def class_types = ["Tumor", "Non-Tumor", "Fibrosis", "Necrosis"]
def out_path = "/sc/arion/projects/HIMC/himc-project-data/BATA02/data/ROIs/"

def imageData = getCurrentImageData()
def server = imageData.getServer()
int w = server.getWidth()
int h = server.getHeight()
def sample_path = server.getPath().split('/')
def sample = sample_path[sample_path.length-1].split('[.]')[0]

annotations = getAnnotationObjects()
for(int i = 0; i < class_names.size(); i++) {
    try {
        def class_annotations = annotations.findAll { it.getPathClass() == getPathClass(class_names[i]) }
        mergeAnnotations(class_annotations)
        annotation = getAnnotationObjects().findAll { it.getPathClass() == getPathClass(class_names[i]) }[0]
        annotation.setName(class_types[i])
        
        // Create a new Rectangle ROI
        //def roi = new RectangleROI(0, 0, w, h)
        //def r = new PathAnnotationObject(roi, PathClassFactory.getPathClass("Region"))
        //imageData.getHierarchy().addPathObject(r, false)
    }
    catch(Exception e) {
       print "Can't find " + class_types[i]
    }
    
}
println(getAnnotationObjects())
exportObjectsToGeoJson(getAnnotationObjects(), out_path+sample+'.geojson', "FEATURE_COLLECTION");
println("Exported objects to "+out_path+sample+".geojson")
println("Done!")