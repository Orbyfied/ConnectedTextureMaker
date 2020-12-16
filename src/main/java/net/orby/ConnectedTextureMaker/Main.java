package net.orby.ConnectedTextureMaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static JFrame frame;
    public static JPanel panel;

    public static BufferedImage borderOverlay;
    public static BufferedImage sourceImage;
    public static BufferedImage cornerOverlay;
    public static float borderSize = 1;
    public static int blockId = 0;
    public static boolean debug = false;

    public static void main(String[] args) throws IOException {

        List<String> args1 = Arrays.asList(args);

        System.out.println("Connected Texture Maker - By Orbyfied");
        System.out.println("- GUI coming soon (maybe)");

        if (args.length < 3){
            System.out.println("[error] please provide parameters: gentextures <source> <borderoverlay> " +
                    "<bordersize> <outputpath> <texturename> [-coverlay <img>] [-blockid <id>]");
            System.out.println("[help] example: \"gentextures diamond_block.png blue_square.png " +
                    "2 out diamond_block");
            return;
        }

        if (args1.contains("-coverlay"))
            loadCornerOverlay(args1.get(args1.indexOf("-coverlay")+1));

        if (args1.contains("-blockid"))
            blockId = Integer.parseInt(args1.get(args1.indexOf("-blockid")+1));

        if (args1.contains("-debug"))
            debug = true;

        String psep = "\\";

        if (args1.contains("-psep"))
            psep = args1.get(args1.indexOf("-psep")+1);

        if (args1.contains("-coverlay"))
            loadCornerOverlay(args1.get(args1.indexOf("-coverlay")+1));

        loadImages(args[0], args[1]);
        borderSize = Float.parseFloat(args[2]);
        export(args[3], args[4], psep);

//        frame = new JFrame("Connected texture maker");
//        frame.setPreferredSize(new Dimension(500, 500));
//        panel = new JPanel();
    }

    public static void loadImages(String srcPath, String ovrPath){
        File si = new File(srcPath);
        File oi = new File(ovrPath);

        try {
              sourceImage = ImageIO.read(si);
              borderOverlay = ImageIO.read(oi);
        } catch (IOException e){
            e.printStackTrace();
            throw new IllegalStateException("failed to load files: IOException", e);
        }
    }

    public static void loadCornerOverlay(String path){
        File si = new File(path);

        try {
            cornerOverlay = ImageIO.read(si);
        } catch (IOException e){
            e.printStackTrace();
            throw new IllegalStateException("failed to load corner overlay: IOException", e);
        }
    }

    public static void export(String fpath, String textureName, String psep){

        if (sourceImage.getWidth() != sourceImage.getHeight())
            throw new IllegalArgumentException("source image must have a valid square resolution.");
        if (borderOverlay.getWidth() != borderOverlay.getHeight())
            throw new IllegalArgumentException("border overlay image must have a valid square resolution.");
        if (cornerOverlay != null)
            if (cornerOverlay.getWidth() != cornerOverlay.getHeight())
                throw new IllegalArgumentException("corner overlay image must have a valid square resolution.");

        if (fpath.equals("/"))
            fpath = "";

        int sr = sourceImage.getWidth();
        int br = borderOverlay.getWidth();

        if (sr != br)
            throw new IllegalArgumentException("border overlay image must have " +
                    "the same resolution as the source image.");

        if (cornerOverlay != null)
            if (cornerOverlay.getWidth() != sr)
                throw new IllegalArgumentException("corner overlay image must have the same resolution as " +
                        "the source image.");

        // replace <id> with the connection design id
        String filePathFormat = fpath + "/" + textureName + "/<id>.png";
        if (filePathFormat.startsWith("/"))
            filePathFormat = filePathFormat.replaceFirst("/", "");
        filePathFormat = filePathFormat.replace("/", psep);
        String filePath;

        if (debug){
            try {
                filePath = filePathFormat.replace("<id>", "source__");
                ImageIO.write(sourceImage, "PNG", new File(filePath));
                filePath = filePathFormat.replace("<id>", "overlay__");
                ImageIO.write(borderOverlay, "PNG", new File(filePath));
                System.out.println("[debug] successfully bundled source and overlay images for debugging " +
                        "(<outputDir>/\"source__\" & \"overlay__\")");
            } catch (Exception e){
                System.out.println("failed to bundle source and overlay images for debugging");
            }
        }

        File outFolder = new File(filePathFormat.replace("/<id>.png", ""));
        if (outFolder.isDirectory() && outFolder.exists()) {
            System.out.print("the output directory ("+outFolder.getAbsolutePath()+") already exists, do you want" +
                    " to delete it and overwrite it with new content? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String o = scanner.next();
            if (o.equals("y")){
                if (!outFolder.delete())
                    System.out.println("failed to delete output directory, continuing");
            }
        }

        for (int i = 0; i < Template.template.length; i++){
            filePath = filePathFormat.replace("<id>", Integer.toString(i));
            File file = new File(filePath);

            System.out.println("[info] processing id "+i+" | texture: "+textureName+" | out: "+filePath);

            // mask parsing
            List<String> elementsStr = Template.parse(i);
            List<Integer[]> elements = Template.createMask(Template.parseCoords(elementsStr), sr,
                    borderSize, borderSize);

            BufferedImage concat = new BufferedImage(sr, sr, BufferedImage.TYPE_INT_RGB);
            concat.createGraphics();

            Graphics2D g = (Graphics2D) concat.getGraphics();

            g.drawImage(sourceImage, 0, 0, null);

            int typeI = 0;
            int typeV = 0;
            for (Integer[] ints : elements){
                typeV = Template.elementTypes.get(typeI);
                Rectangle rect = new Rectangle(ints[0], ints[1], ints[2], ints[3]);
                if (cornerOverlay != null && typeV == Template.ELEMENTTYPE_CORNER){
                    // Fill with corner overlay
                    g.setPaint(new TexturePaint(cornerOverlay.getSubimage(rect.x, rect.y,
                            rect.width, rect.height), rect));
                } else {
                    // Just fill with border overlay like normal
                    g.setPaint(new TexturePaint(borderOverlay.getSubimage(rect.x, rect.y,
                            rect.width, rect.height), rect));
                }
                g.fill(rect);
//                g.setPaint(new TexturePaint(borderOverlay, new Rectangle2D.Double(ints[0], ints[1]
//                        , ints[2], ints[3])));
                typeI++;
            }

//            g.dispose();

            File out = new File(filePath);
            outFolder = new File(out.getParent());
            if (!outFolder.exists()){
                try {
                    if (!out.mkdirs())
                        throw new IOException("File.mkdirs() returned false");
                } catch (IOException e){
                    e.printStackTrace();
                    throw new IllegalStateException("failed to create output file folder (texture: "+textureName+"" +
                            ", id: "+i+", filepath: "+outFolder.getAbsolutePath()+") : IOException");
                }
            }
            if (!out.exists()){
                try {
                    if (!out.createNewFile())
                        throw new IOException("File.createFile() returned false");
                } catch (IOException e){
                    e.printStackTrace();
                     throw new IllegalStateException("failed to create output file (texture: "+textureName+"" +
                             ", id: "+i+", filepath: "+filePath+") : IOException");
                }
            }

            try {
                ImageIO.write(concat, "png", out);
            } catch (IOException e){
                e.printStackTrace();
                throw new IllegalStateException("failed to write to output file (texture: "+textureName+"" +
                        ", id: "+i+", filepath: "+filePath+") : IOException");
            }
        }

        try {
            File file = new File(filePathFormat.replace("<id>"
                    , textureName+".properties").replace(".png", ""));
            if (!file.exists()){
                if (!file.createNewFile()){
                    throw new IllegalStateException("failed to create <texture>.properties file. " +
                            "File.createNewFile() returned false.");
                }
            }
            if (!file.canRead()||!file.canWrite()){
                file.setReadable(true);
                file.setWritable(true);
            }

            FileOutputStream fos = new FileOutputStream(file);
            String blockId0 = "<id>";
            if (blockId != null){
                blockId0 = Integer.toString(blockId);
            }
            fos.write(("matchBlocks=<id>\nmethod=ctm\ntiles=0-46").replace("<id>", blockId0)
                    .getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            System.out.println("successfully created <texture>.properties file (in output directory)");
        } catch (Exception e){
            e.printStackTrace();
            throw new IllegalStateException("failed to write to/create <texture>.properties file.");
        }
        System.out.println("");
        System.out.println("");
    }
}
