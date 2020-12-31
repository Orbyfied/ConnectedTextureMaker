# ConnectedTextureMaker
An automatic Minecraft CTM (Connected Textures Mod) / Optifine connected texture generator.
(You  are allowed to distribute and change the prorgram/code as long as you credit me)

## Installation
First make sure you have JDK 13 or higher, as the program is written in it. 
Now download the `built` folder, this has the pre-built JAR
file, two types of shell scripts (`.bat` for Windows and `.sh` for Mac and Linux). If a JNI error occurs or if it tells you that `java` was not found, check your JDK installation
or change the path in `javapath.txt` to the absolute path of your JDK 13+'s `java.exe`. Then open a terminal or some shell, go to the downloaded `built` folder 
and type `gentextures` in it, there you will see how the arguments work.

## Usage
 - GUI Syntax
   `gentextures` (Without any parameters)
   `gentextures -gui` (With one parameter)
 - Command Syntax
   (<> means required and [] means optional)
   
   `gentextures <borderlessImageFile> <borderImageFile> <borderSize> <outputFolder> <textureName> [-coverlay <overlayImage>] [-blockid <id>] [-pixsize <pix>] [-testsize] [-mirrorborder]`
    
    `<borderlessImageFile>` = The path to the image file without borders. (Source Image)  
    `<borderImageFile>` = The path to the border image file that will overlay the source image on the border/corner parts.  
    `<borderSize>` = The multiplier for the default size, one element in the equation for the border size. (See the next part)  
    `<outputFolder>` = The path to the output folder; Actual output folder: (`<outputF>/<textureN>/`) (`/` = current folder)  
    `<textureName>` = The name of the texture. Used for the actual output folder and the `.properties` file.  
    `[-coverlay <image>]` = Enables the corners of the texture to have a different overlay than the borders.    
    `[-blockid <id>]` = Automatically puts the block ID in the `.properties` file.  
    `[-pixsize <pix>]` = Set the border size on pixels. Original `<borderSize>` will be added to the result   
    `[-testsize]` = Will automatically test for the border size in pixel. Border image has to be completely transparent on the inside of the border. Original `borderSize` will            be added to the result  
    `[-mirrorborder]` = Will mirror the border on all sides to make it look good.  
  - Basic Internal Workings  
    `Border size in pixels` : Element Size * (Resolution / 16) * Border Size  
    `Actual output directory` : OutputFolder/TextureName  
    `Dependencies required` : JDK 13+  

## Me
***YouTube***: https://www.youtube.com/channel/UC341yIu-j9Fs9iS5F3-OeYg?view_as=subscriber
Thats kind of it...

## Changelog  
### 1.0 - Initial Release  
   - Source Image, Border Image & Border Size  

### 1.1 - Extra Customization & Bug fixes  
   - Added the `<texture>.properties` generator.  
   - Added the corner overlay feature.  
   - Added the automatic block ID feature.  
   - Fixed some bugs regarding the math that goes into the elements.  

### 1.2 - The big lazyness update!  
   - Added the `pixsize` feature, to make sure that you don't have to do the math.  
   - Added the `testsize` feature, to make sure that you don't have to look the border size up yourself.  
   - Added the `mirrorborder` feature, to make sure that you don't have to manually make the borders look nice and symmetric.

### 2.0 - GUI!
   - Added a very basic, not so stylish, but working GUI!
