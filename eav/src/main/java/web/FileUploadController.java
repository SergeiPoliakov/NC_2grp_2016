package web;

/**
 * Created by Hroniko on 11.02.2017.
 */

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import entities.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.UploadServiceImp;
import service.UserServiceImp;
import service.application_settings.SettingsLoader;
import service.statistics.StatisticLogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


@Controller
public class FileUploadController {

    // Собственный внутренний логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();
    private UserServiceImp userService = new UserServiceImp();

    private String  server; // = "nc2.hop.ru"; // String server = "netcracker.hop.ru";
    private int     port; // = 21;
    private String  user; // = "w513411"; // String user = "w513022";
    private String  pass; // = "jtgashiw"; // String pass = "oi4qe6l4";


    final Random random = new Random();

    private static final Logger logger = LoggerFactory
            .getLogger(FileUploadController.class);

    private UploadServiceImp uploadService = UploadServiceImp.getInstance();

    // Конструктор
    public FileUploadController() throws IOException {
        // Вызов метода загрузки настроек
        loadSetting();
    }

    @RequestMapping("/upload")
    public String uploadPage() throws Exception {
        // Логируем в базу:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "upload", idUser);
        return "upload";
    }

    @RequestMapping("/uploadMultiple")
    public String uploadMultiplePage() throws Exception {
        // Логируем в базу:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "uploadMultiple", idUser);
        return "uploadMultiple";
    }

    /*
    @RequestMapping("/uploadAvatar")
    public String uploadAvatar() throws Exception {
        return "profile";
    }
    */

    // Загрузка одного файла: // Надо переделать, будет другой метод!!!!!!! 2017-03-16
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileHandler(@RequestParam("name") String name,
                             @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                System.out.println(file.getSize());

                // Проверяем есть ли такая папка и создаем ее, если нет
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Создаем файл на сервере
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Server File Location="
                        + serverFile.getAbsolutePath());

                return "Ваш файл успешно загружен =" + name;
            } catch (Exception e) {
                return "Неудачная загружка файла " + name + " = " + e.getMessage();
            }
        } else {
            return "Неудачная загрузка " + name
                    + " - файл пуст.";
        }
    }

    // Многофайловая загрузка: // Надо переделать, будет другой метод! 2017-03-16
    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadMultipleFileHandler(@RequestParam("name") String[] names,
                                     @RequestParam("file") MultipartFile[] files) {

        if (files.length != names.length)
            return "Отсутствует обязательная инфа";

        String message = "";
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String name = names[i];
            try {
                byte[] bytes = file.getBytes();

                // Проверяем есть ли такая папка и создаем ее, если нет
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Создаем файл на сервере
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Расположение файла на сервере ="
                        + serverFile.getAbsolutePath());

                message = message + "Загрузка файлов выполнена успешно = " + name
                        + "=>";
            } catch (Exception e) {
                return "Неудачная загрузка файлов " + name + " => " + e.getMessage();
            }
        }
        return message;
    }


    // Загрузка аватара
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    //@ResponseBody // Пришлось убрать, так как иначе не перенаправляет на другую страницу
    public String uploadAvatarHandler(@RequestParam("file") MultipartFile file) throws SQLException {

        if (!file.isEmpty()) {

            FTPClient ftpClient = new FTPClient();
            try {

                // Получаем id текущего пользователя для формирования пути к его папке с файлами
                Integer currentUserId = new UserServiceImp().getCurrentUser().getId();
                // Получаем расширение файла
                String[] extFile = file.getOriginalFilename().split("\\.");
                // Формируем имя файла, пример: avatar_10005.png
                ///String name = "avatar_" + random.nextInt(9) + "_" + currentUserId + "." + extFile[extFile.length-1]; // Пришлось так сделать, чтобы браузер реагировал на смену имени, а не тянул из своего кэша
                String name = "avatar_"  + currentUserId + "." + "png";


                // 2. Выполняем подключение к ФТП серверу:
                ftpClient.connect(server, port);
                ftpClient.login(user, pass);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);


                // 3. Проверяем, есть ли такая папка на сервере  /upload/10005/avatar*/
                String relativePatchToFolder = "upload" + "/" + currentUserId  + "/" + "avatar"; // String relativePatchToFolder = "public_html/upload" + "/" + currentUserId  + "/" + "avatar";
                ftpCreateDirectoryTree(ftpClient, relativePatchToFolder); // (и создаем ее, если ее нет)
                ftpClient.changeWorkingDirectory("/"); // Переходим в корневую папку
                ftpClient.changeWorkingDirectory(relativePatchToFolder); // Переходим в эту папку
                ftpClient.deleteFile(name); // Удаляем файл с таким именем


                // 4. Загружаем
                String firstRemoteFile = name;
                InputStream inputStream = file.getInputStream();

                System.out.println("Запускаем загрузку");
                boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("Аватар загружен на ftp");
                }

                //5. Выполняем обновление ссылки в базе:
                //String fullPatchToFolder = rootPath + relativePatchToFolder;
                //String fullPatchToFile = fullPatchToFolder + File.separator + name;

                relativePatchToFolder = "ftp://"+server+"/" + "upload" + "/" + currentUserId  + "/" + "avatar" + "/" + name; // relativePatchToFolder = "http://"+server+"/" + "upload" + "/" + currentUserId  + "/" + "avatar" + "/" + name;
                uploadService.updateAvatar(currentUserId, relativePatchToFolder); // uploadService.updateAvatar(currentUserId, serverFile.getAbsolutePath());

                // Логируем в базу:
                int idUser = userService.getObjID(userService.getCurrentUsername());
                loggerLog.add(Log.AVATAR, relativePatchToFolder, idUser); // обавление (смена) аватара и ссылка на него в строке

            } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
                ex.printStackTrace();
            }  finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        return "redirect:/profile";
    }


    // Создание иерархии папок по строке пути
    private static void ftpCreateDirectoryTree( FTPClient client, String dirTree ) throws IOException {

        boolean dirExists = true;
        String[] directories = dirTree.split("/");
        for (String dir : directories ) {
            if (!dir.isEmpty() ) {
                if (dirExists) {
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!client.makeDirectory(dir)) {
                        throw new IOException("Невозможно создать папку '" + dir + "'.  Ошибка ='" + client.getReplyString()+"'");
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new IOException("Невозможно сменить директорию '" + dir + "'.  Ошибка ='" + client.getReplyString()+"'");
                    }
                }
            }
        }
    }


    // 2017-03-21 Метод загрузки настроек ftp-сервера из настроечного файла приложения:
    private void loadSetting() throws IOException {
        SettingsLoader settingsLoader = new SettingsLoader();
        this.server = settingsLoader.getSetting("ftp_server");
        this.port = new Integer(settingsLoader.getSetting("ftp_port"));
        this.user = settingsLoader.getSetting("ftp_login");
        this.pass = settingsLoader.getSetting("ftp_password");

    }

}