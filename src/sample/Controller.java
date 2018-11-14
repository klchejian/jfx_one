package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import utils.QRCodeUtil;

import javax.swing.*;
import java.io.*;

public class Controller {

    @FXML
    private Button genButton;

    @FXML
    private TextField textField;

    @FXML
    private javafx.scene.image.ImageView imgcode;

    @FXML
    private TextField savePath;

    @FXML
    private TextField change_line;

    @FXML
    private Text tips;

    @FXML
    private Text pathtips;


    @FXML
    private void setGenButtonOnClicked(){
        if(!isPathCorrect()) return;
        String text = textField.getText();
        if(text.length()<=0) {
            tips.setText("请先输入字符");
            return;
        } else{
//            tips.setText("");
        }


        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };

//        System.out.println("chejian hello"+text);
        try{
            StringBuffer parms = new StringBuffer();
//            if(change_line.getLength()<=0){
//                parms.append(text);
//            }else {
//                String[] splits = text.split(change_line.getText());
////                   String[] splits = str.split("CHANGE_LINE");
//                for (int i = 0; i < splits.length; i++) {
//                    if (i != 0) {
//                        parms.append("\n");
//                    }
//                    parms.append(splits[i]);
//                }
//            }
            parms = changeLineForString(parms,text);
            QRCodeUtil.encode(parms.toString(),savePath.getText());
//            QRCodeUtil.encode(parms.toString(),"C://Users/che/Desktop/ngcc.jpg",savePath.getText());
        }catch (Exception e){

        }
    }

    @FXML
    private void setGenButtonsOnClicked(){
        if(!isPathCorrect()) return;
        JFileChooser fd = new JFileChooser();
        fd.showOpenDialog(null);
        File textFile = fd.getSelectedFile();
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
//               if(change_line.getLength()<=0){
//                    parms.append(str);
//               }else {
//                   String[] splits = str.split(change_line.getText());
//                   for (int i = 0; i < splits.length; i++) {
//                       if (i != 0) {
//                           parms.append("\n");
//                       }
//                       parms.append(splits[i]);
//                   }
//               }

               parms = changeLineForString(parms,str);

               System.out.println(parms.toString());
//               QRCodeUtil.encode(parms.toString(),"C://Users/che/Desktop/ngcc.jpg","C://Users/che/Desktop/");
//               QRCodeUtil.encode(parms.toString(),"C://Users/che/Desktop/ngcc.jpg",savePath.getText());
//               QRCodeUtil.encode(parms.toString(),savePath.getText());
               QRCodeUtil.encode(parms.toString(),null,savePath.getText(),false,fileName.length()>0,fileName);
           }
            bufferedReader.close();
            inputStream.close();
       }catch(Exception e){
           e.printStackTrace();
       }finally {

       }
    }

    @FXML
    private void setchoosePathOnClicked(){
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fd.showOpenDialog(null);
        savePath.setText(fd.getSelectedFile().getPath());

    }

    @FXML
    private void setTextFieldAction(){
        int length = textField.getText().length();
        if(length==0){
            tips.setText("请先输入字符");
        }else {
            tips.setText(String.valueOf(length));
        }
    }

    private boolean isPathCorrect(){
        if(savePath.getText().length()<=0){
            pathtips.setText("请选择存储目录");
            return false;
        }else{
            pathtips.setText("");
        }
        return true;
    }

    private StringBuffer changeLineForString(StringBuffer stb,String str){
        if(change_line.getLength()<=0){
            stb.append(str);
        }else {
            String[] splits = str.split(change_line.getText());
//                   String[] splits = str.split("CHANGE_LINE");
            for (int i = 0; i < splits.length; i++) {
                if (i != 0) {
                    stb.append("\n");
                }
                stb.append(splits[i]);
            }
        }
        return stb;
    }

}
