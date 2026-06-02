# qupath-annotation-alignment
QuPath scripts for alignment and transfer of slide annotations from one image to another

Requirements:
- Qupath Align extension for getting alignment transformation (https://github.com/qupath/qupath-extension-align)

Optional:
- Qupath NDPA extension with color preservation for opening npdi images and annotations (https://github.com/ismms-himc/qupath-extension-ndpa-keep-colors)

Example HIMC workflow for transferring annotations from .ndpi H&E image and to a mirrored IF image:
1. Make new qupath project or open existing qupath project
1. Open .ndpi file in qupath project
2. Run ndpi_and flip.groovy using qupath script editor
3. Open IF image in qupath project
4. Open alignment tool
5. Open H&E image as overlay in alignment tool
6. Manually align H&E to IF (alter affine transform matrix directly to change overlay scale, shift+right click+drag overlay to translate, rotate overlay using rotation buttons.
7. Copy affine transform matrix at bottom of alignment tool and paste over the values of the variable *matrix* in intransferAnnotations.groovy
8. Copy the the name of the image you want to transfer annotations from - in this case it should end with (AffineTransform[[something], [something]])
9. With IF image open, run transferAnnotations.groovy
10. In IF image, double click to select and move/edit any annotations that require it.
11. (Optional) Run merge_rename_and_export_annotations.groovy to consolidate and rename annotations of each annotation class. Exports these annotations as a .geojson file as well. Before running, edit *class_names* and *class_types* so that *class_names[i]* is the current annotation class/color hexcode corresponding to *class_types[i]* (Example - *class_names[0] ="#000000"* and *class_types[0]="Tumor"*). Edit outpath to be the folder you want to export the annotation .geojson file to. This .geojson file will have the same name as its corresponding image file.
