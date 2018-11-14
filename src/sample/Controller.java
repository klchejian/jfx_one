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
        }

        try{
            QRCodeUtil.encode(text,savePath.getText());
        }catch (Exception e){
            pathtips.setText("系统错误!");
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
               String fileName = "";
               if(str.contains("SERIAL_SEGMENTATION")) {
                   String[] fileNameSplit = str.split("SERIAL_SEGMENTATION");
                   fileName = fileNameSplit[0];
                   str = fileNameSplit[1];
               }else{
               }

               System.out.println(str);
               QRCodeUtil.encode(str,null,savePath.getText(),false,fileName.length()>0,fileName);
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

}
