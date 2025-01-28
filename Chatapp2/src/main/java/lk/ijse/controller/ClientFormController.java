package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.swing.filechooser.FileView;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;

public class ClientFormController {



    public static String userName;
    @FXML
    private Label lblClientName;

    @FXML
    private ScrollPane scpane;

    @FXML
    private TextField txtTextMessage;

    @FXML
    private VBox vBox;

    @FXML
    private TextArea txtArea;

    @FXML
    private Button btnFile;




   private BufferedReader bufferedReader;
    private  BufferedWriter bufferedWriter;


    public void initialize() {
        try {
            lblClientName.setText(userName);
            Socket socket = new Socket("localhost", 1234);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println(userName + " Connected to server : " + socket);
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(()->{
            try {
                while (true){
                    Label lblName = new Label();
                    Label lblMessage = new Label();

                    ImageView imgReserved = new ImageView();
                    imgReserved.setFitWidth(200);
                    imgReserved.setPreserveRatio(true);

                    String[] data = bufferedReader.readLine().split("/#sendingClientName#/");
                    String clientName = data[0];
                    String message = data[1];

                    lblName.setText(clientName);
                    if (message != null) {
                        Platform.runLater(() -> {
                            HBox hBoxMessage = new HBox();
                            hBoxMessage.setSpacing(10);
                            hBoxMessage.setStyle("-fx-pref-width: 584");


                            if (message.startsWith("Image:")) {
                                byte[] imageData = Base64.getDecoder().decode(message.substring(6));
                                imgReserved.setImage(new Image(new ByteArrayInputStream(imageData)));

                                HBox hBoxImage = new HBox(imgReserved);
                                hBoxImage.setStyle("-fx-padding: 20px 5px;");
                                hBoxMessage.getChildren().addAll(lblName,hBoxImage);

                            }else{
                                lblMessage.setText(message);
                                txtArea.appendText(clientName+":"+message+"\n");

                            }
                            vBox.getChildren().add(hBoxMessage);

                        });
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }



    @FXML
    void imageSendOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] imageData = Files.readAllBytes(file.toPath());
                String encodedImage = Base64.getEncoder().encodeToString(imageData);
                String message = "Image:" + encodedImage;
                ImageView imageView = new ImageView(new Image(file.getPath()));
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);

                HBox imageHbox = new HBox(imageView);
                imageHbox.setStyle("-fx-background-color: #78E08F;-fx-background-radius:15;-fx-alignment: center;-fx-padding: 20px 5px;");

                sendMessage(message, new HBox(imageHbox));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void txtSendOnAction(ActionEvent event) {
        String message = txtTextMessage.getText();
        Label messageLabel = new Label(message);

        if (!message.isEmpty()) {
            try {
                sendMessage(message, new HBox());
                txtTextMessage.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void sendMessage(String message, HBox hbox) throws IOException {
        if (hbox != null) {
            hbox.setStyle("-fx-alignment: center-right;");
            vBox.getChildren().add(hbox);
        }else{
            txtArea.appendText(userName+":"+message);
        }
            message = userName +"/#sendingClientName#/"+message;
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

    }

    @FXML
    void btnFileOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] fileData = Files.readAllBytes(file.toPath());




                //sendMessage(message, new HBox(imageHbox));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
