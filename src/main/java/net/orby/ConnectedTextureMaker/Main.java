package net.orby.ConnectedTextureMaker;

import javax.imageio.ImageIO;
import javax.management.timer.TimerMBean;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.plaf.SplitPaneUI;
import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static BufferedImage borderOverlay;
    public static BufferedImage sourceImage;
    public static BufferedImage cornerOverlay;
    public static Graphics2D borderOverlayGraphics;
    public static Graphics2D sourceImageGraphics;
    public static Graphics2D cornerOverlayGraphics;
    public static int userBorderSizeInPixels = -1;
    public static float borderSize = 1;
    public static int blockId = 0;
    public static boolean debug = false;
    public static boolean testBorderPixels = false;
    public static boolean mirrorBorder = false;
    public static int borderSizePixel;
    public static float borderSizeMultiplier = 1;
    public static String psep = "\\";

    public static JFrame frame;
    public static JPanel panel;
    public static JPanel exportConfig;
    public static JPanel basicInputs;
    public static JPanel extraConfig1;
    public static JPanel extraConfig2;
    public static JPanel extraConfig3;

    public static void main(String[] args) throws IOException {

        List<String> args1 = Arrays.asList(args);

        System.out.println("Connected Texture Maker - By Orbyfied");
        System.out.println("- GUI getting more updates soon.");

        if (args.length < 3 && args.length != 0){
            System.out.println("[error] please provide parameters: gentextures <source> <borderoverlay> " +
                    "<bordersize> <outputpath> <texturename> [-coverlay <img>] [-blockid <id>]");
            System.out.println("[help] example: \"gentextures diamond_block.png blue_square.png " +
                    "2 out diamond_block");
            return;
        }

        if (args1.contains("-psep"))
            psep = args1.get(args1.indexOf("-psep")+1);

        if (args.length < 2 || args1.contains("-gui")){
            initGui();
        }

        if (args.length >= 3){
            command(args);
        }
    }

    public static void command(String[] args) {
        List<String> args1 = Arrays.asList(args);

        if (args1.contains("-blockid"))
            blockId = Integer.parseInt(args1.get(args1.indexOf("-blockid")+1));

        if (args1.contains("-debug"))
            debug = true;

        if (args1.contains("-pixsize"))
            userBorderSizeInPixels = Integer.parseInt(args[args1.indexOf("-pixsize")+1]);

        if (args1.contains("-testsize"))
            testBorderPixels = true;

        if (args1.contains("-mirrorborder"))
            mirrorBorder = true;

        if (args1.contains("-sizemultiplier"))
            borderSizeMultiplier = Float.parseFloat(args[args1.indexOf("-sizemultiplier")+1]);

        String psep = "\\";

        if (args1.contains("-psep"))
            psep = args1.get(args1.indexOf("-psep")+1);

        if (args1.contains("-coverlay"))
            loadCornerOverlay(args1.get(args1.indexOf("-coverlay")+1));

        loadImages(args[0], args[1]);
        borderSize = Float.parseFloat(args[2]);
        export(args[3], args[4], psep);
    }

    public static void reset(){
         borderOverlay = null;
         sourceImage = null;
         cornerOverlay = null;
         sourceImageGraphics = null;
         borderOverlayGraphics = null;
         cornerOverlayGraphics = null;
         borderSize = 1f;
         userBorderSizeInPixels = -1;
         blockId = 0;
         debug = false;
         testBorderPixels = false;
         mirrorBorder = false;
         borderSizeMultiplier = 1;
         psep = "\\";
    }

    public static void initGui(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        frame = new JFrame("ConnectedTextureMaker by Orbyfied");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(750, 200));
        exportConfig = new JPanel();
        basicInputs = new JPanel();
        extraConfig1 = new JPanel();
        extraConfig2 = new JPanel();
        extraConfig3 = new JPanel();
        panel = new JPanel();
//        try {
//            frame.setIconImage(ImageIO.read(Class.class.getResourceAsStream("icon.png")));
//        } catch (Exception e){ e.printStackTrace(); }
        frame.pack();
        frame.setVisible(true);
        panel.add(exportConfig);
        panel.add(basicInputs);
        panel.add(extraConfig1);
        panel.add(extraConfig2);
        panel.add(extraConfig3);
        frame.add(panel);

        JLabel elabel1 = new JLabel("Output Dir.");
        JTextField outputDir = new JTextField(10);
        JButton browseOD = new JButton("Browse");
        JLabel elabel2 = new JLabel("Texture Name");
        JTextField textureName = new JTextField(5);

        JButton export = new JButton("Export");

        JLabel label1 = new JLabel("Source Image: ");
        JTextField sourceImagePath = new JTextField(10);
        JButton browseSI = new JButton("Browse");
        JLabel label2 = new JLabel("Border Image: ");
        JTextField borderOverlayPath = new JTextField(10);
        JButton browseBO = new JButton("Browse");
        JLabel label3 = new JLabel("Border Size: ");
        JTextField borderSize = new JTextField(3);

        JLabel xlabel1 = new JLabel("Corner Overlay: ");
        JTextField cornerOverlayPath = new JTextField(10);
        JButton browseCO = new JButton("Browse");
        JLabel xlabel2 = new JLabel("Block ID: ");
        JTextField blockIdF = new JTextField(2);
        JLabel xlabel3 = new JLabel("Border Size In PX: ");
        JTextField bspix = new JTextField(4);
        JLabel xlabel4 = new JLabel("Mirror Border: ");
        JCheckBox mirrorb = new JCheckBox();
        JLabel xlabel5 = new JLabel("Test Border Size: ");
        JCheckBox testb = new JCheckBox();
        JLabel xlabel6 = new JLabel("Size Multiplier: ");
        JTextField sizemul = new JTextField(4);

        exportConfig.add(export);
        exportConfig.add(elabel1);
        exportConfig.add(outputDir);
        exportConfig.add(browseOD);
        exportConfig.add(elabel2);
        exportConfig.add(textureName);
        basicInputs.add(label1);
        basicInputs.add(sourceImagePath);
        basicInputs.add(browseSI);
        basicInputs.add(label2);
        basicInputs.add(borderOverlayPath);
        basicInputs.add(browseBO);
        basicInputs.add(label3);
        basicInputs.add(borderSize);
        extraConfig1.add(xlabel1);
        extraConfig1.add(cornerOverlayPath);
        extraConfig1.add(browseCO);
        extraConfig1.add(xlabel2);
        extraConfig1.add(blockIdF);
        extraConfig2.add(xlabel3);
        extraConfig2.add(bspix);
        extraConfig2.add(xlabel4);
        extraConfig2.add(mirrorb);
        extraConfig2.add(xlabel5);
        extraConfig2.add(testb);
        extraConfig2.add(xlabel6);
        extraConfig2.add(sizemul);

        export.addActionListener(e -> {
            reset();
            List<String> args = new ArrayList<>();
            args.add(sourceImagePath.getText());
            args.add(borderOverlayPath.getText());
            args.add(borderSize.getText());
            args.add(outputDir.getText());
            args.add(textureName.getText());
            if (!psep.equals("\\")){
                args.add("-psep");
                args.add(psep);
            }

            if (!cornerOverlayPath.getText().isEmpty()){
                args.add("-coverlay");
                args.add(cornerOverlayPath.getText());
            }
            if (!blockIdF.getText().isBlank()){
                args.add("-blockid");
                args.add(blockIdF.getText());
            }
            if (!bspix.getText().isBlank()){
                System.out.println("\""+bspix.getText()+"\"");
                args.add("-pixsize");
                args.add(bspix.getText());
            }
            if (mirrorb.isSelected()){
                args.add("-mirrorborder");
            }
            if (testb.isSelected()){
                args.add("-testsize");
            }
            if (!sizemul.getText().isBlank()){
                args.add("-sizemultiplier");
                args.add(sizemul.getText());
            }
            System.out.println(args);
            command(args.toArray(new String[0]));
        });

        browseOD.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.showSaveDialog(frame);
            outputDir.setText(chooser.getSelectedFile().getAbsolutePath());
        });

        browseSI.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.showSaveDialog(frame);
            sourceImagePath.setText(chooser.getSelectedFile().getAbsolutePath());
        });

        browseCO.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (sourceImagePath.getText().isEmpty())
                chooser.setCurrentDirectory(new File("."));
            else
                chooser.setCurrentDirectory(new File(new File(sourceImagePath.getText()).getParent()));
            chooser.showSaveDialog(frame);
            cornerOverlayPath.setText(chooser.getSelectedFile().getAbsolutePath());
        });

        browseBO.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (sourceImagePath.getText().isEmpty())
                chooser.setCurrentDirectory(new File("."));
            else
                chooser.setCurrentDirectory(new File(new File(sourceImagePath.getText()).getParent()));
            chooser.showSaveDialog(frame);
            borderOverlayPath.setText(chooser.getSelectedFile().getAbsolutePath());
        });

        frame.pack();
    }

    public static int testBorderSizeM(int res){
        System.out.println("detecting border size...");
        int size = 0;
        int[] avm = new int[4];

        int x = 0;
        int y = 0;
        boolean end;

        // Test Left
        y = res/2;
        end = false;
        while (!end){
            x++;
            if (new Color(borderOverlay.getRGB(x, y), true).getAlpha() == 0)
                end = true;
        }
        avm[0] = x;

        // Test Right
        y = res/2;
        x = res;
        end = false;
        while (!end){
            x--;
            if (new Color(borderOverlay.getRGB(x, y), true).getAlpha() == 0)
                end = true;
        }
        avm[1] = res - x;

        // Test Top
        y = 0;
        x = res/2;
        end = false;
        while (!end){
            y++;
            if (new Color(borderOverlay.getRGB(x, y), true).getAlpha() == 0)
                end = true;
        }
        avm[2] = y;

        // Test Bottom
        y = res;
        x = res/2;
        end = false;
        while (!end){
            y--;
            if (new Color(borderOverlay.getRGB(x, y), true).getAlpha() == 0)
                end = true;
        }
        avm[3] = res - y;

        // Get Average and return
        for (int a : avm)
            size += a;
        size = (int) Math.floor(size / 4f);
        System.out.println("size detected: "+size);
        return size;
    }

    public static float fromPixelAmt(int pix, int resolution){
        return pix / (resolution / 16f);
    }

    public static void loadImages(String srcPath, String ovrPath){
        File si = new File(srcPath);
        File oi = new File(ovrPath);

        try {
              sourceImage = ImageIO.read(si);
              borderOverlay = ImageIO.read(oi);

              borderOverlayGraphics = borderOverlay.createGraphics();
              sourceImageGraphics = sourceImage.createGraphics();
        } catch (IOException e){
            e.printStackTrace();
            throw new IllegalStateException("failed to load files: IOException", e);
        }
    }

    public static void mirrorBorderImage() {
        int w = sourceImage.getWidth();
        int h = sourceImage.getHeight();

        System.out.println("mirroring border image...");
        BufferedImage mirror = new BufferedImage(w/2, h/2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mg = mirror.createGraphics();
        mg.drawImage(borderOverlay, 0, 0, null);
        mg.dispose();
        for (int x = 0; x <= borderSizePixel; x++){
            for (int y = 0; y < h/2; y++){
                int y1 = y;
                if (y <= borderSizePixel)
                    y1 = y+x;
                mirror.setRGB(y1,x,mirror.getRGB(x, y1));
            }
        }

        borderOverlayGraphics.drawImage(mirror, 0, 0, null);
        borderOverlayGraphics.drawImage(rotate(flipH(mirror), 0), w/2, 0, null);
        borderOverlayGraphics.drawImage(rotate(mirror, 180), w/2, h/2, null);
        borderOverlayGraphics.drawImage(rotate(flipV(mirror), 0), 0, h/2, null);
        borderOverlayGraphics.dispose();
    }

    private static BufferedImage rotate(BufferedImage image, float degrees)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(degrees),
                image.getWidth()/2.0,
                image.getHeight()/2.0);
        return createTransformed(image, at);
    }

    public static BufferedImage flipHV(BufferedImage image){
        return flipH(flipV(image));
    }

    private static BufferedImage flipH(BufferedImage image)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        return createTransformed(image, at);
    }

    private static BufferedImage flipV(BufferedImage image)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();

        g.transform(at);
        g.drawImage(image, 0, 0, null);

        g.dispose();
        return newImage;
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

        if (testBorderPixels)
            borderSize = fromPixelAmt(testBorderSizeM(sr), sr) + borderSize;

        if (userBorderSizeInPixels != -1)
            borderSize = fromPixelAmt(userBorderSizeInPixels, sr) + borderSize;

        borderSizePixel = (int) (borderSize * sr / 16);

        borderSize = borderSize * borderSizeMultiplier;

        System.out.println("border size: "+borderSize);

        if (mirrorBorder)
            mirrorBorderImage();

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

            BufferedImage concat = new BufferedImage(sr, sr, BufferedImage.TYPE_INT_ARGB);
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
            if (blockId0 != null){
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
