package utils;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Created by che on 2018/10/15.
 */

@SuppressWarnings("all")
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 500;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }


    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println(""+imgPath+"   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }


    public static void encode(String content, String imgPath, String destPath,
                              boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999)+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
    }

    public static void encode(String content, String imgPath, String destPath,
                              boolean needCompress,boolean addFileName,String fileName) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress);
        mkdirs(destPath);
        String file = "";
        if(addFileName){
            file = fileName+".jpg";
        }else{
            file = new Random().nextInt(99999999)+".jpg";
        }
        String abslutePath = destPath+"/"+file;
//        System.out.println(abslutePath);
        ImageIO.write(image, FORMAT_NAME, new File(abslutePath));
        FontText.drawTextInImg(abslutePath, abslutePath, new FontText(file.split(".jpg")[0], 2, "#000000", 40, "黑体"));
    }


    public static void mkdirs(String destPath) {
        File file =new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }


    public static void encode(String content, String imgPath, String destPath)
            throws Exception {
        QRCodeUtil.encode(content, imgPath, destPath, false);
    }


    public static void encode(String content, String destPath,
                              boolean needCompress) throws Exception {
        QRCodeUtil.encode(content, null, destPath, needCompress);
    }


    public static void encode(String content, String destPath) throws Exception {
        QRCodeUtil.encode(content, null, destPath, false);
    }


    public static void encode(String content, String imgPath,
                              OutputStream output, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }


    public static void encode(String content, OutputStream output)
            throws Exception {
        QRCodeUtil.encode(content, null, output, false);
    }


    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }


    public static String decode(String path) throws Exception {
        return QRCodeUtil.decode(new File(path));
    }

    public static void batchEncode(String filePath,String desPath,String change_line){
        File textFile = new File(filePath);
        try{
            FileInputStream inputStream = new FileInputStream(textFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine()) != null){
                StringBuffer parms = new StringBuffer();
                String fileName = "";
                if(str.contains("SERIAL_SEGMENTATION")) {
                    String[] fileNameSplit = str.split("SERIAL_SEGMENTATION");
                    fileName = fileNameSplit[0];
                    str = fileNameSplit[1];
                }else{
                }
                if(change_line.length()<=0){
                    parms.append(str);
                }else {
                    String[] splits = str.split(change_line);
//                   String[] splits = str.split("CHANGE_LINE");
                    for (int i = 0; i < splits.length; i++) {
                        if (i != 0) {
                            parms.append("\n");
                        }
                        parms.append(splits[i]);
                    }
                }

                System.out.println(parms.toString());
                QRCodeUtil.encode(parms.toString(),null,desPath,false,fileName.length()>0,fileName);
            }
            bufferedReader.close();
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    private StringBuffer changeLineForString(StringBuffer stb,String str){

        return stb;
    }

    public static void main(String[] args) throws Exception {
        String str = "1-CISCO 3845SERIAL_SEGMENTATION产品名:Router 资产名:CISCO 3845 路由器-1 IP地址:218.244.250.83 厂商名称:CISCO 资产成本:79297 购进日期:40057 资产状态:In Use 责任人:陈杰 部门:网络技术部 机柜号:FSW-6 功能:网络接入 用途:28号院互联网接入 部门:网络技术部 设备型号:CISCO 3845 是否涉密:否 配置: 地理位置:28号院 资产名:CISCO 3845 路由器-1 ";
        System.out.println(str.getBytes().length);

//        StringBuffer stb = new StringBuffer();
//        stb.append("abcd");
//        for(; stb.length()<2000;){
//            stb.append(stb);
//        }
//        for(int i = 0; i < 280 ;i++){
//            stb.append("a");
//        }
//
//        stb.append("\n");
//        stb.append("x");
//        System.out.println(stb.length());
//        QRCodeUtil.encode(stb.toString(),null,"C://Users/che/Desktop/",false,true,stb.length()+".jpg");
//        stb.append("y");
//        System.out.println(stb.length());
//        QRCodeUtil.encode(stb.toString(),null,"C://Users/che/Desktop/",false,true,stb.length()+".jpg");
//        stb.append("z");
//        System.out.println(stb.length());
//        QRCodeUtil.encode(stb.toString(),null,"C://Users/che/Desktop/",false,true,stb.length()+".jpg");
    }

}
