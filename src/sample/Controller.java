package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.w3c.dom.*;
import sample.Identification.IdentifiedPerson;
import sample.Validation.ValidatedPerson;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Controller {

    private File fileToParse;
    private List<File> inputFileArray;
    private String resultFileName;
    private final FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser= new DirectoryChooser();
    private ArrayList<IdentifiedPerson> identifiedPersonArrayList;
    private ArrayList<ValidatedPerson> validatedPersonArrayList;

    @FXML
    private TextField filePathTextField;

    @FXML
    private TextField unIdentifiedValueField;

    @FXML
    private TextField otherIdentifiedValueField;

    @FXML
    private TextField rowCounterTextField;

    @FXML
    private TextField successValueField;

    @FXML
    private Button startConvertionBtn;


    @FXML
    public void initialize(){

       createDirIfNotExist("IDENTIFICATION_ANSWER/");
        createDirIfNotExist("VALIDATION_ANSWER/");


        startConvertionBtn.setDisable(true);

        rowCounterTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    rowCounterTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        unIdentifiedValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    unIdentifiedValueField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        otherIdentifiedValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    otherIdentifiedValueField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        successValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    successValueField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    public void browseIdentAction(ActionEvent event){
        changeDataView(filePathTextField, startConvertionBtn);
    }

    private void changeDataView(TextField tf, Button btn){
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "XML files", "*.xml");

        File dir = directoryChooser.showDialog(btn.getScene().getWindow());
        fileChooser.getExtensionFilters().add(fileExtensions);
        //inputFileArray = fileChooser.showOpenMultipleDialog(btn.getScene().getWindow());
        inputFileArray = Arrays.asList(dir.listFiles());
        if(inputFileArray.size() == 1){
            tf.setEditable(true);
            fileToParse = inputFileArray.get(0);
            tf.setText(fileToParse.getPath());
        }
        else {
            tf.setEditable(false);
            StringBuilder stringBuilder = new StringBuilder();
            for(File f : inputFileArray){
                stringBuilder.append(f.getName() + "; ");
            }
            tf.setText(stringBuilder.toString());
        }
        btn.setDisable(false);
    }

    //Identification process
    @FXML
    public void startConvertingProcess(ActionEvent event){
        try {
            for(File singleFile : inputFileArray){
                fileToParse = singleFile;

                if(fileToParse.isFile()) {
                    if (fileToParse.getName().contains("IDENTIFICATION")) {
                        parseIdentificationXML();
                        createIdentificationAnswerXML();
                    }
                    if (fileToParse.getName().contains("VALIDATION")) {
                        parseValidationXML();
                        createValidationAnswerXML();
                    }
                }
            }
            alertInfo("Готовые файлы вы можете найти здесь: " + System.getProperty("user.dir") + "/ в папках IDENTIFICATION_ANSWER и VALIDATION_ANSWER");
        }
        catch (Exception e){
            System.err.println(e.getStackTrace());
        }
    }

    //IDENTIFICATION BLOCK

    private void parseIdentificationXML() throws Exception{
        //Parse file
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(fileToParse);

        //Get root node of tree DOM
        Node root = document.getDocumentElement();

        //Get children
        NodeList nodeList = root.getChildNodes();

        identifiedPersonArrayList = new ArrayList<IdentifiedPerson>();


        for (int i = 0; i < nodeList.getLength(); i++) {

            //Get item
            Node personNode = nodeList.item(i);

            //If type is not a text
            if (personNode.getNodeType() != Node.TEXT_NODE) {

                NodeList personProperties = personNode.getChildNodes();

                IdentifiedPerson identifiedPerson = new IdentifiedPerson();

                for(int j = 0; j < personProperties.getLength(); j++) {
                    Node personProperty = personProperties.item(j);
                    if (personProperty.getNodeType() != Node.TEXT_NODE) {
                        Node propValue = personProperty.getChildNodes().item(0);
                        if(propValue != null){

                            //Fill values to iPerson

                            //NEED TO REFACTOR
                            String check = personProperty.getNodeName();

                            if(check.equals("МестоРождения")){
                                NodeList birthPlace = personProperty.getChildNodes();
                                for(int b = 1; b < birthPlace.getLength(); b+=2){
                                    identifiedPerson.parseProperty(birthPlace.item(b).getNodeName(),birthPlace.item(b).getTextContent());
                                }
                            }else {
                                identifiedPerson.parseProperty(personProperty.getNodeName(), personProperty.getChildNodes().item(0).getTextContent());
                            }
                        }
                        else {

                            //For unnecessary parameters
                            identifiedPerson.parseProperty(personProperty.getNodeName(),null);
                        }
                    }
                }
                if(!identifiedPerson.isEmpty())
                    identifiedPersonArrayList.add(identifiedPerson);
            }
        }
    }

    private void createIdentificationAnswerXML(){

        resultFileName = fileToParse.getName().replace("REQ","ANS");

        double unIdentificated = Double.parseDouble(unIdentifiedValueField.getText())/100;
        double otherIdentificated = Double.parseDouble(otherIdentifiedValueField.getText())/100;

        int rowCounter = Integer.parseInt(rowCounterTextField.getText());

        int objectsNumber = identifiedPersonArrayList.size();

        if(objectsNumber > rowCounter){
            objectsNumber = rowCounter;
        }

        double unIdeficatedObjectsNumber = (int)Math.round(objectsNumber * unIdentificated);
        double otherIdentificatedObjectsNumber = (int)Math.round(objectsNumber * otherIdentificated);


        //Не идентифицированно
        for(int i = 0; i < unIdeficatedObjectsNumber; i++ ){
            identifiedPersonArrayList.get(i).getIdentificationResult().setStatus("НЕ ИДЕНТИФИЦИРОВАН");
        }


        int tmp_index = (int)(unIdeficatedObjectsNumber + otherIdentificatedObjectsNumber);
        for(int i = (int) unIdeficatedObjectsNumber;
            i < tmp_index;
            i ++)
        {
            identifiedPersonArrayList.get(i).getIdentificationResult().setStatus("ИДЕНТИФИЦИРОВАН НЕ ОДНОЗНАЧНО");
        }


        for(int i = tmp_index; i < objectsNumber; i ++)
        {
            identifiedPersonArrayList.get(i).getIdentificationResult().setStatus("ИДЕНТИФИЦИРОВАН");
            identifiedPersonArrayList.get(i).getIdentificationResult().generateSnils();
        }

        ArrayList<IdentifiedPerson> onlyIdentifiedPersonArrayList = new ArrayList<>();
        for(int i = 0; i < objectsNumber; i++){
            onlyIdentifiedPersonArrayList.add(identifiedPersonArrayList.get(i));
        }


        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            DOMSource source = buildIdentificationDOM(onlyIdentifiedPersonArrayList);
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "/IDENTIFICATION_ANSWER/" +resultFileName));
            transformer.transform(source, result);
        }
        catch (Exception e){
            System.err.println(e.getCause());
            System.err.println(e.getStackTrace());
            errorAlert();

        }

    }

    private DOMSource buildIdentificationDOM(ArrayList<IdentifiedPerson> ipar){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("ФайлПФР");
            doc.appendChild(rootElement);

            Element fName = doc.createElement("ИмяФайла");
            fName.appendChild(doc.createTextNode(resultFileName));
            rootElement.appendChild(fName);

            Element fDate = doc.createElement("ДатаФормирования");
            fDate.appendChild(doc.createTextNode(LocalDateTime.now().toString()));
            rootElement.appendChild(fDate);

            Element fVer = doc.createElement("ВерсияФормата");
            fVer.appendChild(doc.createTextNode("1.0"));
            rootElement.appendChild(fVer);

            Element fType = doc.createElement("ТипФайла");
            fType.appendChild(doc.createTextNode("ИДЕНТИФИКАЦИЯ_ОТВЕТ"));
            rootElement.appendChild(fType);

            Element rowNum = doc.createElement("КоличествоЗаписейВфайле");
            rowNum.appendChild(doc.createTextNode(ipar.size() + ""));
            rootElement.appendChild(rowNum);

            for(IdentifiedPerson ip : ipar) {
                if (!ip.isEmpty()) {

                    //IPerson data body
                    Element answer = doc.createElement("ИдентификацияОтвет");
                    rootElement.appendChild(answer);

                    Element requestIdentificator = doc.createElement("ИдентификаторЗапроса");
                    requestIdentificator.appendChild(doc.createTextNode(ip.getId_request()));
                    answer.appendChild(requestIdentificator);

                    Element lastNameElement = doc.createElement("Фамилия");
                    lastNameElement.appendChild(doc.createTextNode(ip.getLastname()));
                    answer.appendChild(lastNameElement);

                    Element firstNameElement = doc.createElement("Имя");
                    firstNameElement.appendChild(doc.createTextNode(ip.getFirstname()));
                    answer.appendChild(firstNameElement);

                    Element surNameElement = doc.createElement("Отчество");

                        surNameElement.appendChild(doc.createTextNode(ip.getSurname()));

                    answer.appendChild(surNameElement);

                    Element birthDateElement = doc.createElement("ДатаРождения");
                    birthDateElement.appendChild(doc.createTextNode(ip.getBirthDate()));
                    answer.appendChild(birthDateElement);

                    Element sexElement = doc.createElement("Пол");
                    sexElement.appendChild(doc.createTextNode(ip.getSex()));
                    answer.appendChild(sexElement);

                    Element birthPlaceElement = doc.createElement("МестоРождения");
                    answer.appendChild(birthDateElement);

                    //Birth place data body START

                    Element cityElement = doc.createElement("ГородРождения");
                    cityElement.appendChild(doc.createTextNode(ip.getBirthPlace().getCity()));
                    birthPlaceElement.appendChild(cityElement);

                    Element regionElement = doc.createElement("РайонРождения");
                    regionElement.appendChild(doc.createTextNode(ip.getBirthPlace().getRegion()));
                    birthPlaceElement.appendChild(regionElement);

                    Element areaElement = doc.createElement("ОбластьРождения");
                    areaElement.appendChild(doc.createTextNode(ip.getBirthPlace().getArea()));
                    birthPlaceElement.appendChild(areaElement);

                    Element countryElement = doc.createElement("СтранаРождения");
                    countryElement.appendChild(doc.createTextNode(ip.getBirthPlace().getCountry()));
                    birthPlaceElement.appendChild(countryElement);

                    //Identification result data body

                    Element identificationResultElement = doc.createElement("РезультатИдентификации");
                    answer.appendChild(identificationResultElement);

                    Element statusElement = doc.createElement("Статус");
                    statusElement.appendChild(doc.createTextNode(ip.getIdentificationResult().getStatus()));
                    identificationResultElement.appendChild(statusElement);

                    String snils_value = ip.getIdentificationResult().getSnils();
                    if (snils_value != null) {
                        Element snilsElement = doc.createElement("СНИЛС");
                        snilsElement.appendChild(doc.createTextNode(snils_value));
                        identificationResultElement.appendChild(snilsElement);
                    }
                }
            }

            return new DOMSource(doc);
        }
        catch (ParserConfigurationException pce){;
            System.err.println(pce.getStackTrace());
            return null;
        }
    }


    //VALIDATION BLOCK
    private void parseValidationXML() throws Exception{
        //Parse file
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(fileToParse);

        //Get root node of tree DOM
        Node root = document.getDocumentElement();

        //Get children
        NodeList nodeList = root.getChildNodes();

        validatedPersonArrayList = new ArrayList<>();


        for (int i = 0; i < nodeList.getLength(); i++) {

            //Get item
            Node personNode = nodeList.item(i);

            //If type is not a text
            if (personNode.getNodeType() != Node.TEXT_NODE) {

                NodeList personProperties = personNode.getChildNodes();

                ValidatedPerson validatedPerson = new ValidatedPerson();

                for(int j = 0; j < personProperties.getLength(); j++) {
                    Node personProperty = personProperties.item(j);
                    if (personProperty.getNodeType() != Node.TEXT_NODE) {
                        Node propValue = personProperty.getChildNodes().item(0);
                        if(propValue != null){

                            validatedPerson.parseProperty(personProperty.getNodeName(), personProperty.getChildNodes().item(0).getTextContent());
                        }
                        else {

                            //For unnecessary parameters
                            validatedPerson.parseProperty(personProperty.getNodeName(),null);
                        }
                    }
                }

                if(!validatedPerson.isEmpty())
                    validatedPersonArrayList.add(validatedPerson);
            }
        }
    }

    private void createValidationAnswerXML(){

        resultFileName = fileToParse.getName().replace("REQ","ANS");

        double successValidPercent = Double.parseDouble(successValueField.getText())/100;
        double errorValidPercent = 1 - successValidPercent;

        int rowCounter = Integer.parseInt(rowCounterTextField.getText());

        int objectsNumber = validatedPersonArrayList.size();
        ArrayList<ValidatedPerson> onlyValidatedPersoneList = new ArrayList<>();

        if(objectsNumber > rowCounter){
            objectsNumber = rowCounter;
        }

        //double successObjectsNumber = (int)Math.round(objectsNumber * successValidPercent);
        double errorObjectsNumber = (int)Math.round(objectsNumber * errorValidPercent);

        for(int i = 0; i < errorObjectsNumber; i++){
            validatedPersonArrayList.get(i).getValidationResult().setStatus("ОШИБКА");
            validatedPersonArrayList.get(i).getValidationResult().setDescription(errorGenerator());
            onlyValidatedPersoneList.add(validatedPersonArrayList.get(i));

        }

        for(int i = (int)errorObjectsNumber; i < objectsNumber; i++ ){
            validatedPersonArrayList.get(i).getValidationResult().setStatus("СНИЛС Подтерждён");
            onlyValidatedPersoneList.add(validatedPersonArrayList.get(i));

        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            DOMSource source = buildValidationDOM(onlyValidatedPersoneList);
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "/VALIDATION_ANSWER/" +resultFileName));
            transformer.transform(source, result);
        }
        catch (TransformerException te){
            System.err.println(te.getStackTrace());
            errorAlert();
        }


    }

    private DOMSource buildValidationDOM(ArrayList<ValidatedPerson> vpar){

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("ФайлПФР");
            doc.appendChild(rootElement);

            Element fName = doc.createElement("ИмяФайла");
            fName.appendChild(doc.createTextNode(resultFileName));
            rootElement.appendChild(fName);

            Element fDate = doc.createElement("ДатаФормирования");
            fDate.appendChild(doc.createTextNode(LocalDateTime.now().toString()));
            rootElement.appendChild(fDate);

            Element fVer = doc.createElement("ВерсияФормата");
            fVer.appendChild(doc.createTextNode("1.0"));
            rootElement.appendChild(fVer);

            Element fType = doc.createElement("ТипФайла");
            fType.appendChild(doc.createTextNode("ВАЛИДАЦИЯ_ОТВЕТ"));
            rootElement.appendChild(fType);

            Element rowNum = doc.createElement("КоличествоЗаписейВфайле");
            rowNum.appendChild(doc.createTextNode(validatedPersonArrayList.size() + ""));
            rootElement.appendChild(rowNum);

            for (ValidatedPerson vp : vpar) {

                    //System.out.println(vp);
                    //IPerson data body
                    Element answer = doc.createElement("ВалидацияОтвет");
                    rootElement.appendChild(answer);

                    Element requestIdentificator = doc.createElement("ИдентификаторЗапроса");
                    requestIdentificator.appendChild(doc.createTextNode(vp.getId_request()));
                    answer.appendChild(requestIdentificator);

                    Element snilsElement = doc.createElement("СНИЛС");
                    snilsElement.appendChild(doc.createTextNode(vp.getSnils()));
                    answer.appendChild(snilsElement);

                    Element lastNameElement = doc.createElement("Фамилия");
                    lastNameElement.appendChild(doc.createTextNode(vp.getLastname()));
                    answer.appendChild(lastNameElement);

                    Element firstNameElement = doc.createElement("Имя");
                    firstNameElement.appendChild(doc.createTextNode(vp.getFirstname()));
                    answer.appendChild(firstNameElement);

                    Element surNameElement = doc.createElement("Отчество");
                    if(vp.getSurname() != null) {
                        surNameElement.appendChild(doc.createTextNode(vp.getSurname()));
                    }

                    answer.appendChild(surNameElement);

                    Element birthDateElement = doc.createElement("ДатаРождения");
                    birthDateElement.appendChild(doc.createTextNode(vp.getBirthDate()));
                    answer.appendChild(birthDateElement);

                    Element sexElement = doc.createElement("Пол");
                    sexElement.appendChild(doc.createTextNode(vp.getSex()));
                    answer.appendChild(sexElement);

                    //Identification result data body
                    Element validationResultElement = doc.createElement("РезультатВалидации");
                    answer.appendChild(validationResultElement);


                    Element statusElement = doc.createElement("Статус");
                    statusElement.appendChild(doc.createTextNode(vp.getValidationResult().getStatus()));
                    validationResultElement.appendChild(statusElement);

                    String desc_value = vp.getValidationResult().getDescription();
                    if (desc_value != null) {
                        Element descElement = doc.createElement("Пояснение");
                        descElement.appendChild(doc.createTextNode(desc_value));
                        validationResultElement.appendChild(descElement);
                    }
            }

            return new DOMSource(doc);
        }
        catch (Exception e){
            return null;
        }
    }


    //ALERT BLOCK

    private void alertProcessSuccess(int iden_proc_count, int valid_proc_number){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Конвертирование завершено");
        alert.setHeaderText("Успешно завершена конвертация "
                + iden_proc_count + " файла(ов) идентифицкации "
                + valid_proc_number + " файла(ов) валидации");
        alert.setContentText("Готовые файлы вы можете найти здесь: " + System.getProperty("user.dir") + "/");
        alert.showAndWait();
    }

    private void alertInfo(String mes){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setContentText(mes);
        alert.showAndWait();
    }

    private void errorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("В результате процесса возникло исключение");
        alert.setContentText("Обратиесь к разработчику, предоставив ему входные данные, использованные при работе с программой");
        alert.showAndWait();
    }


    //EROR GENERATOR FOR VALIDATION

    private void createDirIfNotExist(String dir_name){
        File create_dir = new File(System.getProperty("user.dir") + "/" + dir_name );
        if(!create_dir.exists()){
            create_dir.mkdirs();
        }
    }

    private String errorGenerator(){
        Random rand= new Random();
        int rnd = rand.nextInt(3);
        if(rnd == 0)
            return "Неизвестная ошибка";
        if(rnd == 1)
            return "Нет данных";
        return "Неверный СНИЛС";

    }

}
