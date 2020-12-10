# ConnectedTextureMaker
An automatic Minecraft CTM (Connected Textures Mod) / Optifine connected texture generator.
(You  are allowed to distribute and change the prorgram/code as long as you credit me)

## How to install
First make sure you have JDK 13 or higher, as the program is written in it. 
Now download the `built` folder, this has the pre-built JAR
file, two types of shell scripts (`.bat` for Windows and `.sh` for Mac and Linux). If a JNI error occurs or if it tells you that `java` was not found, check your JDK installation
or change the path in `javapath.txt` to the absolute path of your JDK 13+'s `java.exe`. Then open a terminal or some shell, go to the downloaded `built` folder 
and type `gentextures` in it, there you will see how the arguments work.

## How to use
 - Command Syntax
   (<> means required and [] means optional)
   
   `gentextures <borderlessImageFile> <borderImageFile> <borderSize> <outputFolder> <textureName> [-coverlay <overlayImage>] [-blockid <id>]`
    
    `<borderlessImageFile>` = The path to the image file without borders. (Source Image)  
    `<borderImageFile>` = The path to the border image file that will overlay the source image on the border/corner parts.  
    `<borderSize>` = The multiplier for the default size, one element in the equation for the border size. (See the next part)  
    `<outputFolder>` = The path to the output folder; Actual output folder: (`<outputF>/<textureN>/`) (`/` = current folder)  
    `<textureName>` = The name of the texture. Used for the actual output folder and the `.properties` file.
    `[-coverlay <image>]` = Enables the corners of the texture to have a different overlay than the borders.  
    `[-blockid <id>]` = Automatically puts the block ID in the `.properties` file.
